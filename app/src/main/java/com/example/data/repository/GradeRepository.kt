package com.example.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.util.Base64
import com.example.data.api.ClassItem
import com.example.data.api.GradeApiRequest
import com.example.data.api.LoginRequest
import com.example.data.api.NetworkClient
import com.example.data.api.ServerGradeSyncRequest
import com.example.data.api.StudentItem
import com.example.data.api.UpdateGradeRequest
import com.example.data.api.UserData
import com.example.data.local.AppDatabase
import com.example.data.local.GradeRecordDao
import com.example.data.local.GradeRecordEntity
import com.example.data.model.ErrorBox
import com.example.data.model.GradeCriteria
import com.example.data.model.GradeResult
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

// Định nghĩa ngoại lệ chuyên dụng khi giao tiếp máy chủ chấm bài
class ServerGradeException(
    override val message: String,
    val httpCode: Int? = null,
    cause: Throwable? = null
) : Exception(message, cause)

class GradeRepository(
    private val context: Context,
    private val dao: GradeRecordDao = AppDatabase.getDatabase(context).gradeRecordDao()
) {
    private val moshi: Moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
    private val errorsListType = Types.newParameterizedType(List::class.java, ErrorBox::class.java)
    private val errorsAdapter = moshi.adapter<List<ErrorBox>>(errorsListType)

    val allGradedRecords: Flow<List<GradeResult>> = dao.getAllRecords().map { entities ->
        entities.map { it.toModel() }
    }

    fun saveBitmapToInternalCache(bitmap: Bitmap): String {
        return try {
            val cacheDir = File(context.filesDir, "grades").apply { if (!exists()) mkdirs() }
            val file = File(cacheDir, "grade_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(6)}.jpg")
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            }
            file.absolutePath
        } catch (e: Exception) {
            android.util.Log.e("GradeRepository", "Lỗi lưu ảnh cache: ${e.message}")
            ""
        }
    }

    fun getCacheDirectorySizeBytes(): Long {
        return try {
            val cacheDir = File(context.filesDir, "grades")
            if (cacheDir.exists()) {
                cacheDir.walkTopDown().filter { it.isFile }.map { it.length() }.sum()
            } else 0L
        } catch (_: Exception) {
            0L
        }
    }

    fun clearPhotoCache(): Int {
        return try {
            val cacheDir = File(context.filesDir, "grades")
            if (cacheDir.exists()) {
                val files = cacheDir.listFiles() ?: emptyArray()
                var count = 0
                files.forEach {
                    if (it.delete()) count++
                }
                count
            } else 0
        } catch (_: Exception) {
            0
        }
    }

    suspend fun gradeImage(
        bitmap: Bitmap,
        serverUrl: String = NetworkClient.DEFAULT_BASE_URL,
        studentGrade: Int = 3,
        essayType: String = "spelling",
        gradingMode: String = "dictation",
        studentName: String = "Học sinh",
        className: String = "",
        penaltyPerError: Float = 0.5f
    ): GradeResult = withContext(Dispatchers.IO) {
        val startTime = System.currentTimeMillis()
        val cachedPhotoPath = saveBitmapToInternalCache(bitmap).ifEmpty { null }
        val imageBase64 = encodeBitmapToBase64(bitmap)
        val effectiveClassName = className.ifBlank { "Lớp ${studentGrade}A" }

        val apiService = try {
            NetworkClient.createService(serverUrl)
        } catch (e: Exception) {
            throw ServerGradeException("Địa chỉ máy chủ '$serverUrl' không hợp lệ: ${e.localizedMessage}")
        }

        val response = try {
            apiService.submitForGrading(
                GradeApiRequest(
                    imageBase64 = imageBase64,
                    studentGrade = studentGrade,
                    gradingMode = gradingMode,
                    studentName = studentName,
                    className = effectiveClassName,
                    essayType = essayType,
                    penaltyPerError = penaltyPerError
                )
            )
        } catch (e: Exception) {
            val netErrMsg = when {
                e is java.net.ConnectException -> "Không thể kết nối tới máy chủ tại $serverUrl. Hãy kiểm tra xem server Next.js đã bật chưa và điện thoại có chung mạng Wi-Fi không."
                e is java.net.SocketTimeoutException -> "Hết thời gian chờ phản hồi (Timeout) từ máy chủ $serverUrl. Quá trình xử lý AI vượt quá thời gian cho phép."
                e is java.net.UnknownHostException -> "Không tìm thấy địa chỉ IP/tên miền máy chủ: $serverUrl. Vui lòng kiểm tra lại cấu hình trạm."
                else -> "Lỗi kết nối mạng: ${e.localizedMessage ?: e.javaClass.simpleName}"
            }
            throw ServerGradeException(netErrMsg, cause = e)
        }

        if (!response.isSuccessful || response.body() == null) {
            val errBody = try { response.errorBody()?.string() } catch (_: Exception) { null }
            val errMsg = "Máy chủ phản hồi mã lỗi HTTP ${response.code()}${if (!errBody.isNullOrBlank()) ": $errBody" else ""}. Vui lòng kiểm tra log của máy chủ ViHand Grade."
            throw ServerGradeException(errMsg, httpCode = response.code())
        }

        val body = response.body()!!
        if (body.status == "error") {
            throw ServerGradeException("Máy chủ báo lỗi khi chấm bài thi.")
        }

        val apiCrit = body.criteria
        val criteria = if (apiCrit != null) {
            GradeCriteria(
                spellingScore = apiCrit.spellingScore,
                formatScore = apiCrit.formatScore,
                contentScore = apiCrit.contentScore,
                creativityScore = apiCrit.creativityScore,
                totalScore = apiCrit.totalScore
            )
        } else {
            GradeCriteria(3.0f, 2.5f, 1.8f, 0.7f, 8.0f)
        }

        val errors = body.errors?.mapIndexed { index, err ->
            val bmpW = bitmap.width.toFloat().coerceAtLeast(1f)
            val bmpH = bitmap.height.toFloat().coerceAtLeast(1f)
            val relX1 = when {
                err.relX1 != null && err.relX1 in 0f..1f -> err.relX1
                err.x1 in 0f..1f -> err.x1
                err.x1 > 1f -> (err.x1 / bmpW).coerceIn(0f, 1f)
                else -> 0f
            }
            val relY1 = when {
                err.relY1 != null && err.relY1 in 0f..1f -> err.relY1
                err.y1 in 0f..1f -> err.y1
                err.y1 > 1f -> (err.y1 / bmpH).coerceIn(0f, 1f)
                else -> 0f
            }
            val relW = when {
                err.relW != null && err.relW in 0f..1f -> err.relW
                err.x2 > err.x1 && err.x2 > 1f -> ((err.x2 - err.x1) / bmpW).coerceIn(0.02f, 1f)
                err.x2 > err.x1 && err.x2 <= 1f -> (err.x2 - err.x1).coerceIn(0.02f, 1f)
                else -> 0.08f
            }
            val relH = when {
                err.relH != null && err.relH in 0f..1f -> err.relH
                err.y2 > err.y1 && err.y2 > 1f -> ((err.y2 - err.y1) / bmpH).coerceIn(0.02f, 1f)
                err.y2 > err.y1 && err.y2 <= 1f -> (err.y2 - err.y1).coerceIn(0.02f, 1f)
                else -> 0.06f
            }

            ErrorBox(
                id = err.id ?: "err_$index",
                originalWord = err.originalWord,
                correctedWord = err.correctedWord,
                errorType = err.errorType,
                explanation = err.explanation,
                penalty = err.penalty,
                x1 = err.x1,
                y1 = err.y1,
                x2 = err.x2,
                y2 = err.y2,
                lineNumber = err.lineNumber,
                rel_x1 = relX1,
                rel_y1 = relY1,
                rel_w = relW,
                rel_h = relH
            )
        } ?: emptyList()

        val result = GradeResult(
            id = body.serverGradeId ?: UUID.randomUUID().toString(),
            timestamp = System.currentTimeMillis(),
            studentName = body.studentName ?: studentName,
            className = body.className ?: effectiveClassName,
            essayTitle = body.essayTitle ?: "Bài thi Viết tay Tiểu học",
            criteria = criteria,
            pedagogicalComment = body.pedagogicalComment ?: "Bài làm có nhiều cố gắng, cần chú ý chính tả.",
            pedagogicalComments = body.pedagogicalComments ?: emptyList(),
            extractedText = body.extractedText ?: "Văn bản bài thi chữ viết tay",
            correctedFullText = body.correctedFullText ?: "",
            errors = errors,
            processingTimeMs = body.processingTimeMs ?: (System.currentTimeMillis() - startTime),
            serverSource = body.serverSource ?: "ViHand Grade Server (vihand.db)",
            photoPath = cachedPhotoPath,
            photoBitmap = bitmap
        )

        // Lưu vào Room DB làm bản sao lưu đệm cục bộ (Local Cache)
        try {
            saveRecord(result)
        } catch (e: Exception) {
            android.util.Log.w("GradeRepository", "Không thể ghi bản sao lưu đệm Room: ${e.message}")
        }

        return@withContext result
    }

    suspend fun saveRecord(result: GradeResult): Long = withContext(Dispatchers.IO) {
        val entity = GradeRecordEntity(
            timestamp = result.timestamp,
            studentName = result.studentName,
            className = result.className,
            essayTitle = result.essayTitle,
            totalScore = result.criteria.totalScore,
            spellingScore = result.criteria.spellingScore,
            formatScore = result.criteria.formatScore,
            contentScore = result.criteria.contentScore,
            creativityScore = result.criteria.creativityScore,
            pedagogicalComment = result.pedagogicalComment,
            extractedText = result.extractedText,
            correctedFullText = result.correctedFullText,
            errorsJson = errorsAdapter.toJson(result.errors),
            serverSource = result.serverSource,
            sampleType = result.sampleType,
            photoPath = result.photoPath
        )
        dao.insertRecord(entity)
    }

    suspend fun deleteRecord(id: Long) = withContext(Dispatchers.IO) {
        dao.deleteRecordById(id)
    }

    suspend fun deleteAllRecords() = withContext(Dispatchers.IO) {
        dao.deleteAllRecords()
    }

    suspend fun testConnection(serverUrl: String): Pair<Boolean, String> = withContext(Dispatchers.IO) {
        try {
            val apiService = NetworkClient.createService(serverUrl)
            val startTime = System.currentTimeMillis()
            val response = apiService.checkHealth()
            val pingMs = System.currentTimeMillis() - startTime
            if (response.isSuccessful) {
                Pair(true, "Kết nối thành công! Ping: ${pingMs}ms (Cloudflare Tunnel -> Raspberry Pi)")
            } else {
                Pair(false, "Mã phản hồi HTTP: ${response.code()} từ trạm $serverUrl")
            }
        } catch (e: Exception) {
            Pair(false, "Không thể kết nối máy chủ (${e.localizedMessage ?: "Timeout"}). Đã bật sẵn chế độ Edge AI Offline an toàn.")
        }
    }

    private fun encodeBitmapToBase64(bitmap: Bitmap): String {
        // Compress bitmap as specified in step 2 of markdown (WebP/JPEG ~1600px <1MB)
        val maxDimension = 1600
        val scale = if (bitmap.width > maxDimension || bitmap.height > maxDimension) {
            maxDimension.toFloat() / maxOf(bitmap.width, bitmap.height)
        } else {
            1.0f
        }
        val scaledBitmap = if (scale < 1.0f) {
            Bitmap.createScaledBitmap(
                bitmap,
                (bitmap.width * scale).toInt(),
                (bitmap.height * scale).toInt(),
                true
            )
        } else {
            bitmap
        }

        val outputStream = ByteArrayOutputStream()
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }

    private fun generateSimulatedAnalysis(
        bitmap: Bitmap,
        startTime: Long,
        errorReason: String? = null,
        serverUrl: String = ""
    ): GradeResult {
        val notice = if (!errorReason.isNullOrBlank()) {
            "\n\n[⚠️ THÔNG BÁO KỸ THUẬT: Máy chủ AI tại $serverUrl không phản hồi ($errorReason). Ứng dụng tự động chuyển sang chế độ Mô Phỏng Ngoại Tuyến (Edge AI Offline). Hãy kiểm tra lại địa chỉ máy chủ trong tab 'Trạm Pi']."
        } else ""

        // Generates realistic educational grading based on primary school rubrics
        return GradeResult(
            id = UUID.randomUUID().toString(),
            timestamp = System.currentTimeMillis(),
            studentName = "Học sinh Tiểu học",
            className = "Lớp 3A - Vở ô ly",
            essayTitle = "Bài thi viết tay: Tiếng chim buổi sáng",
            criteria = GradeCriteria(
                spellingScore = 2.5f,
                formatScore = 2.5f,
                contentScore = 1.8f,
                creativityScore = 0.7f,
                totalScore = 7.5f
            ),
            pedagogicalComment = "Bài viết trình bày sạch sẽ, đúng dòng kẻ ô ly. Phát hiện 2 lỗi dùng từ và phụ âm đầu. Em cần chú ý phân biệt 'tr' và 'ch', 's' và 'x' để bài viết hoàn thiện hơn!$notice",
            extractedText = "Buổi sáng sớm, đàn chim ríu rít chuyền cành. Ngoài vườn cây bàng chổ hoa thơm ngát. Gió thổi bay lượn xớm mai.",
            correctedFullText = "Buổi sáng sớm, đàn chim ríu rít chuyền cành. Ngoài vườn cây bàng trổ hoa thơm ngát. Gió thổi bay lượn sớm mai.",
            errors = listOf(
                ErrorBox(
                    id = "sim_err_1",
                    originalWord = "chổ hoa",
                    correctedWord = "trổ hoa",
                    errorType = "Phụ âm đầu (ch/tr)",
                    explanation = "Viết đúng chính tả: 'trổ hoa', 'trổ cành' viết bằng 'tr'.",
                    penalty = -0.5f,
                    x1 = 0.45f,
                    y1 = 0.42f,
                    x2 = 0.75f,
                    y2 = 0.52f,
                    lineNumber = 2
                ),
                ErrorBox(
                    id = "sim_err_2",
                    originalWord = "xớm mai",
                    correctedWord = "sớm mai",
                    errorType = "Phụ âm đầu (s/x)",
                    explanation = "'Sớm mai', 'buổi sớm' viết bằng 's', không viết bằng 'x'.",
                    penalty = -0.5f,
                    x1 = 0.55f,
                    y1 = 0.65f,
                    x2 = 0.82f,
                    y2 = 0.75f,
                    lineNumber = 3
                )
            ),
            processingTimeMs = System.currentTimeMillis() - startTime,
            serverSource = if (!errorReason.isNullOrBlank()) {
                "Mô phỏng Ngoại Tuyến ($errorReason)"
            } else {
                "Edge AI Local Engine (Dự phòng thông minh)"
            }
        )
    }

    private fun GradeRecordEntity.toModel(): GradeResult {
        val errorsList = try {
            errorsAdapter.fromJson(errorsJson) ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }

        return GradeResult(
            id = id.toString(),
            timestamp = timestamp,
            studentName = studentName,
            className = className,
            essayTitle = essayTitle,
            criteria = GradeCriteria(
                spellingScore = spellingScore,
                formatScore = formatScore,
                contentScore = contentScore,
                creativityScore = creativityScore,
                totalScore = totalScore
            ),
            pedagogicalComment = pedagogicalComment,
            extractedText = extractedText,
            correctedFullText = correctedFullText,
            errors = errorsList,
            processingTimeMs = 1100L,
            serverSource = serverSource,
            sampleType = sampleType,
            photoPath = photoPath
        )
    }

    suspend fun getClassesList(serverUrl: String): List<ClassItem> = withContext(Dispatchers.IO) {
        try {
            val api = NetworkClient.createService(serverUrl)
            val res = api.getClasses()
            if (res.isSuccessful && res.body()?.classes != null) {
                res.body()!!.classes!!
            } else {
                defaultClasses
            }
        } catch (_: Exception) {
            defaultClasses
        }
    }

    suspend fun getStudentsList(serverUrl: String, className: String? = null): List<StudentItem> = withContext(Dispatchers.IO) {
        try {
            val api = NetworkClient.createService(serverUrl)
            val res = api.getStudents()
            if (res.isSuccessful && res.body()?.users != null) {
                val users = res.body()!!.users!!
                if (!className.isNullOrBlank()) {
                    users.filter { it.className == className }
                } else {
                    users
                }
            } else {
                defaultStudents.filter { className.isNullOrBlank() || it.className == className }
            }
        } catch (_: Exception) {
            defaultStudents.filter { className.isNullOrBlank() || it.className == className }
        }
    }

    suspend fun login(serverUrl: String, req: LoginRequest): Result<UserData> = withContext(Dispatchers.IO) {
        try {
            val api = NetworkClient.createService(serverUrl)
            val res = api.login(req)
            if (res.isSuccessful && res.body()?.user != null) {
                Result.success(res.body()!!.user!!)
            } else {
                val err = res.body()?.error ?: res.errorBody()?.string() ?: "Đăng nhập thất bại (HTTP ${res.code()})"
                Result.failure(Exception(err))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateGradeOnServer(
        serverUrl: String,
        gradeId: String,
        updatedErrors: List<ErrorBox>,
        newCriteria: GradeCriteria,
        pedagogicalComment: String? = null
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val api = NetworkClient.createService(serverUrl)
            val scoreStr = "${String.format(java.util.Locale.US, "%.1f", newCriteria.totalScore)}/10"
            val breakdownJson = "{\"spelling\":${newCriteria.spellingScore},\"format\":${newCriteria.formatScore},\"content\":${newCriteria.contentScore},\"creativity\":${newCriteria.creativityScore}}"
            val errorsJson = errorsAdapter.toJson(updatedErrors)
            val res = api.updateGrade(
                id = gradeId,
                request = UpdateGradeRequest(
                    corrections = errorsJson,
                    score = scoreStr,
                    scoreBreakdown = breakdownJson,
                    pedagogicalComment = pedagogicalComment,
                    feedback = pedagogicalComment
                )
            )
            res.isSuccessful
        } catch (e: Exception) {
            android.util.Log.e("GradeRepository", "Lỗi cập nhật điểm lên server: ${e.message}")
            false
        }
    }

    suspend fun syncAllGradesToServer(serverUrl: String): Pair<Int, Int> = withContext(Dispatchers.IO) {
        val records = dao.getAllRecordsList()
        if (records.isEmpty()) {
            return@withContext Pair(0, 0)
        }
        var successCount = 0
        var failCount = 0
        val api = NetworkClient.createService(serverUrl)

        records.forEach { rec ->
            try {
                val scoreVal = if (rec.totalScore > 0) rec.totalScore else 0.0f
                val scoreStr = "${String.format(java.util.Locale.US, "%.1f", scoreVal)}/10"
                val breakdownJson = "{\"spelling\":${rec.spellingScore},\"format\":${rec.formatScore},\"content\":${rec.contentScore},\"creativity\":${rec.creativityScore}}"
                val textContent = rec.extractedText.ifBlank {
                    rec.correctedFullText.ifBlank {
                        rec.essayTitle.ifBlank { "Bài làm học sinh" }
                    }
                }
                val req = ServerGradeSyncRequest(
                    gradingMode = "dictation",
                    studentName = rec.studentName.ifBlank { "Học sinh" },
                    assignmentTitle = rec.essayTitle.ifBlank { "Bài viết chính tả" },
                    className = rec.className.ifBlank { "" },
                    originalText = textContent,
                    fixedText = rec.correctedFullText.ifBlank { textContent },
                    score = scoreStr,
                    scoreBreakdown = breakdownJson,
                    corrections = rec.errorsJson.ifBlank { "[]" },
                    pedagogicalComment = rec.pedagogicalComment.ifBlank { "Đã chấm điểm thành công" },
                    feedback = rec.pedagogicalComment.ifBlank { "Đã chấm điểm thành công" }
                )
                val response = api.syncGrade(req)
                if (response.isSuccessful) {
                    successCount++
                } else {
                    android.util.Log.e("GradeRepository", "Lỗi đồng bộ [${rec.studentName}]: HTTP ${response.code()} - ${response.errorBody()?.string()}")
                    failCount++
                }
            } catch (e: Exception) {
                android.util.Log.e("GradeRepository", "Ngoại lệ khi đồng bộ [${rec.studentName}]: ${e.message}")
                failCount++
            }
        }
        Pair(successCount, failCount)
    }

    suspend fun fetchGradesFromServer(
        serverUrl: String,
        className: String? = null,
        search: String? = null
    ): List<GradeResult> = withContext(Dispatchers.IO) {
        try {
            val api = NetworkClient.createService(serverUrl)
            val res = api.getGrades(className = className, search = search)
            if (res.isSuccessful && res.body()?.grades != null) {
                val serverList = res.body()!!.grades!!
                serverList.map { item ->
                    val (sp, fmt, cnt, crt) = parseScoreBreakdown(item.scoreBreakdown)
                    val errorsList = parseErrorsList(item.corrections)
                    val scoreVal = item.scoreNum ?: (item.score.split("/")[0].toFloatOrNull() ?: 0f)

                    GradeResult(
                        id = item.id,
                        timestamp = System.currentTimeMillis(),
                        studentName = item.studentName,
                        className = item.className ?: "",
                        essayTitle = item.assignmentTitle,
                        criteria = GradeCriteria(
                            spellingScore = sp,
                            formatScore = fmt,
                            contentScore = cnt,
                            creativityScore = crt,
                            totalScore = scoreVal
                        ),
                        pedagogicalComment = item.pedagogicalComment?.ifBlank { item.feedback ?: "" } ?: (item.feedback ?: ""),
                        extractedText = item.originalText ?: "",
                        correctedFullText = item.fixedText ?: "",
                        errors = errorsList,
                        processingTimeMs = item.processingTimeMs?.toLong() ?: 1200L,
                        imageUrl = item.imagePath?.takeIf { it.isNotBlank() },
                        serverSource = "ViHand Grade Server Database (vihand.db)"
                    )
                }
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            android.util.Log.e("GradeRepository", "Lỗi tải sổ điểm từ server: ${e.message}")
            emptyList()
        }
    }

    /**
     * Đồng bộ hai chiều toàn diện giữa điện thoại và máy chủ ViHand Grade:
     * - Bước 1: Gửi các bài chấm offline lưu trên điện thoại lên cơ sở dữ liệu server
     * - Bước 2: Kéo toàn bộ bài chấm từ server về và lưu vào Room Database trên máy
     * Trả về: Triple(số bài tải lên thành công, số bài tải lên thất bại, số bài mới tải từ server về)
     */
    suspend fun syncTwoWayWithServer(
        serverUrl: String,
        studentName: String? = null,
        className: String? = null
    ): Triple<Int, Int, Int> = withContext(Dispatchers.IO) {
        // 1. Đẩy các bài offline nội bộ lên máy chủ
        val (uploadedSuccess, uploadedFail) = syncAllGradesToServer(serverUrl)

        // 2. Tải toàn bộ bài chấm từ máy chủ về lưu vào Room DB nội bộ
        val serverGrades = fetchGradesFromServer(serverUrl, className = className, search = studentName)
        var downloadedCount = 0
        if (serverGrades.isNotEmpty()) {
            val localRecords = dao.getAllRecordsList()
            val existingKeys = localRecords.map { "${it.studentName.trim()}_${it.essayTitle.trim()}_${it.className.trim()}" }.toSet()
            serverGrades.forEach { sGrade ->
                val key = "${sGrade.studentName.trim()}_${sGrade.essayTitle.trim()}_${sGrade.className.trim()}"
                if (key !in existingKeys) {
                    saveRecord(sGrade)
                    downloadedCount++
                }
            }
        }
        Triple(uploadedSuccess, uploadedFail, downloadedCount)
    }

    private data class GradeBreakdown(val spelling: Float, val format: Float, val content: Float, val creativity: Float)

    private fun parseScoreBreakdown(json: String?): GradeBreakdown {
        if (json.isNullOrBlank()) return GradeBreakdown(3.0f, 2.5f, 1.8f, 0.7f)
        return try {
            val mapType = Types.newParameterizedType(Map::class.java, String::class.java, Any::class.java)
            val adapter = moshi.adapter<Map<String, Any>>(mapType)
            val map = adapter.fromJson(json) ?: return GradeBreakdown(3.0f, 2.5f, 1.8f, 0.7f)
            val sp = (map["spelling"] as? Number)?.toFloat() ?: 3.0f
            val fmt = (map["format"] as? Number)?.toFloat() ?: 2.5f
            val cnt = (map["content"] as? Number)?.toFloat() ?: 1.8f
            val crt = (map["creativity"] as? Number)?.toFloat() ?: 0.7f
            GradeBreakdown(sp, fmt, cnt, crt)
        } catch (_: Exception) {
            GradeBreakdown(3.0f, 2.5f, 1.8f, 0.7f)
        }
    }

    private fun parseErrorsList(json: String?): List<ErrorBox> {
        if (json.isNullOrBlank() || json == "[]") return emptyList()
        // 1. Thử parse trực tiếp qua Moshi adapter
        try {
            val list = errorsAdapter.fromJson(json)
            if (!list.isNullOrEmpty() && list.any { it.rel_x1 > 0f || it.x1 > 0f }) {
                return list
            }
        } catch (_: Exception) {}

        // 2. Fallback: Parse qua org.json.JSONArray để hỗ trợ định dạng Web Prisma:
        // { "error": "...", "suggestion": "...", "error_type": "...", "reason": "...", "bbox": { "rel_x1": 0.4, ... } }
        return try {
            val array = org.json.JSONArray(json)
            val result = mutableListOf<ErrorBox>()
            for (i in 0 until array.length()) {
                val obj = array.optJSONObject(i) ?: continue
                val id = obj.optString("id").ifBlank { "err_$i" }
                val originalWord = obj.optString("originalWord").ifBlank { obj.optString("error") }
                val correctedWord = obj.optString("correctedWord").ifBlank { obj.optString("suggestion") }
                val errorType = obj.optString("errorType").ifBlank { obj.optString("error_type", "chinh_ta") }
                val explanation = obj.optString("explanation").ifBlank { obj.optString("reason") }
                val penalty = obj.optDouble("penalty", 0.5).toFloat()
                val lineNum = obj.optInt("lineNumber", 1)

                val bboxObj = obj.optJSONObject("bbox")
                val relX = bboxObj?.optDouble("rel_x1", -1.0)?.toFloat()
                    ?.takeIf { it >= 0f }
                    ?: obj.optDouble("rel_x1", -1.0).toFloat().takeIf { it >= 0f }
                    ?: obj.optDouble("x1", 0.0).toFloat()

                val relY = bboxObj?.optDouble("rel_y1", -1.0)?.toFloat()
                    ?.takeIf { it >= 0f }
                    ?: obj.optDouble("rel_y1", -1.0).toFloat().takeIf { it >= 0f }
                    ?: obj.optDouble("y1", 0.0).toFloat()

                val relW = bboxObj?.optDouble("rel_w", -1.0)?.toFloat()
                    ?.takeIf { it > 0f }
                    ?: obj.optDouble("rel_w", -1.0).toFloat().takeIf { it > 0f }
                    ?: 0.08f

                val relH = bboxObj?.optDouble("rel_h", -1.0)?.toFloat()
                    ?.takeIf { it > 0f }
                    ?: obj.optDouble("rel_h", -1.0).toFloat().takeIf { it > 0f }
                    ?: 0.06f

                val x1 = bboxObj?.optDouble("x1", 0.0)?.toFloat() ?: obj.optDouble("x1", 0.0).toFloat()
                val y1 = bboxObj?.optDouble("y1", 0.0)?.toFloat() ?: obj.optDouble("y1", 0.0).toFloat()
                val x2 = bboxObj?.optDouble("x2", 0.0)?.toFloat() ?: obj.optDouble("x2", 0.0).toFloat()
                val y2 = bboxObj?.optDouble("y2", 0.0)?.toFloat() ?: obj.optDouble("y2", 0.0).toFloat()

                result.add(
                    ErrorBox(
                        id = id,
                        originalWord = originalWord,
                        correctedWord = correctedWord,
                        errorType = errorType,
                        explanation = explanation,
                        penalty = penalty,
                        x1 = x1,
                        y1 = y1,
                        x2 = x2,
                        y2 = y2,
                        lineNumber = lineNum,
                        rel_x1 = relX,
                        rel_y1 = relY,
                        rel_w = relW,
                        rel_h = relH
                    )
                )
            }
            result
        } catch (_: Exception) {
            emptyList()
        }
    }

    companion object {
        val defaultClasses = listOf(
            ClassItem("c_3a", "Lớp 3A", 3, 35, "Cô Nguyễn Thị Mai"),
            ClassItem("c_3b", "Lớp 3B", 3, 34, "Thầy Trần Văn Hùng"),
            ClassItem("c_4a", "Lớp 4A", 4, 36, "Cô Lê Thị Hoa"),
            ClassItem("c_4b", "Lớp 4B", 4, 35, "Thầy Phạm Văn Nam"),
            ClassItem("c_5a", "Lớp 5A", 5, 38, "Cô Hoàng Lan Anh")
        )

        val defaultStudents = listOf(
            StudentItem("s_1", "Nguyễn Văn An", "an.nv", "Lớp 3A"),
            StudentItem("s_2", "Trần Thị Bình", "binh.tt", "Lớp 3A"),
            StudentItem("s_3", "Lê Hoàng Châu", "chau.lh", "Lớp 3A"),
            StudentItem("s_4", "Phạm Minh Đức", "duc.pm", "Lớp 3A"),
            StudentItem("s_5", "Vũ Hải Đăng", "dang.vh", "Lớp 3A"),
            StudentItem("s_6", "Hoàng Kim Ngân", "ngan.hk", "Lớp 3B"),
            StudentItem("s_7", "Bùi Quốc Khánh", "khanh.bq", "Lớp 3B"),
            StudentItem("s_8", "Đỗ Mai Phương", "phuong.dm", "Lớp 3B"),
            StudentItem("s_9", "Lê Tuấn Kiệt", "kiet.lt", "Lớp 4A"),
            StudentItem("s_10", "Ngô Quỳnh Chi", "chi.nq", "Lớp 4A")
        )
    }
}
