package com.example.ui.viewmodel

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.GradeRecordEntity
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

// ============================================================
// REPORTS VIEW MODEL — Thống kê sư phạm từ Room DB
// ============================================================
@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class ReportsViewModel(application: Application) : AndroidViewModel(application) {

    private val context: Context get() = getApplication<Application>().applicationContext
    private val dao = AppDatabase.getDatabase(context).gradeRecordDao()
    private val moshi = Moshi.Builder().build()
    private val listType = Types.newParameterizedType(List::class.java, Map::class.java)

    // ============ FILTERS ============
    private val _selectedClass = MutableStateFlow("Tất cả lớp")
    val selectedClass: StateFlow<String> = _selectedClass

    private val _selectedTimeframe = MutableStateFlow("Tất cả")
    val selectedTimeframe: StateFlow<String> = _selectedTimeframe

    private val _exportResult = MutableStateFlow<ExportResult?>(null)
    val exportResult: StateFlow<ExportResult?> = _exportResult

    // ============ DERIVED DATA ============
    val classList: StateFlow<List<String>> = dao.getDistinctClasses()
        .map { classes -> listOf("Tất cả lớp") + classes }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), listOf("Tất cả lớp"))

    val timeframeList = listOf("Tất cả", "Tuần này", "Tháng này", "Học kỳ 1", "Cả năm")

    /** Bản ghi đã lọc theo class + timeframe */
    val filteredRecords: StateFlow<List<GradeRecordEntity>> =
        combine(_selectedClass, _selectedTimeframe) { cls, tf -> Pair(cls, tf) }
            .flatMapLatest { (cls, tf) ->
                val since = getTimestampForTimeframe(tf)
                when {
                    cls == "Tất cả lớp" && since == 0L -> dao.getAllRecords()
                    cls == "Tất cả lớp" -> dao.getRecordsSince(since)
                    since == 0L -> dao.getRecordsByClass(cls)
                    else -> dao.getRecordsByClassSince(cls, since)
                }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** Thống kê tổng hợp */
    val stats: StateFlow<ReportStats> = filteredRecords
        .map { records -> computeStats(records) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ReportStats.EMPTY)

    // ============ ACTIONS ============
    fun setSelectedClass(cls: String) { _selectedClass.value = cls }
    fun setSelectedTimeframe(tf: String) { _selectedTimeframe.value = tf }
    fun clearExportResult() { _exportResult.value = null }

    fun exportCsv() {
        viewModelScope.launch {
            try {
                val records = filteredRecords.value
                if (records.isEmpty()) {
                    _exportResult.value = ExportResult(false, "Không có dữ liệu để xuất")
                    return@launch
                }
                val fileName = "BangDiem_ChinhTa_${_selectedClass.value.replace(" ", "_")}_${
                    SimpleDateFormat("yyyyMMdd_HHmm", Locale.getDefault()).format(Date())
                }.csv"
                
                val csvContent = buildCsvContent(records)
                val savedPath = saveCsvFile(fileName, csvContent)
                
                _exportResult.value = ExportResult(true, "Đã lưu file $fileName vào Thư mục Tải về", savedPath)
            } catch (e: Exception) {
                _exportResult.value = ExportResult(false, "Lỗi xuất CSV: ${e.localizedMessage}")
            }
        }
    }

    // ============ HELPERS ============
    private fun getTimestampForTimeframe(tf: String): Long {
        val cal = Calendar.getInstance()
        return when (tf) {
            "Tuần này" -> {
                cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.timeInMillis
            }
            "Tháng này" -> {
                cal.set(Calendar.DAY_OF_MONTH, 1)
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.timeInMillis
            }
            "Học kỳ 1" -> {
                // Học kỳ 1: Từ tháng 9 năm trước (hoặc hiện tại)
                val year = if (cal.get(Calendar.MONTH) >= Calendar.SEPTEMBER) cal.get(Calendar.YEAR) else cal.get(Calendar.YEAR) - 1
                cal.set(year, Calendar.SEPTEMBER, 1, 0, 0, 0)
                cal.timeInMillis
            }
            "Cả năm" -> {
                cal.set(Calendar.DAY_OF_YEAR, 1)
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.timeInMillis
            }
            else -> 0L // "Tất cả"
        }
    }

    private fun computeStats(records: List<GradeRecordEntity>): ReportStats {
        if (records.isEmpty()) return ReportStats.EMPTY

        val total = records.size
        val avgScore = records.map { it.totalScore }.average().toFloat()
        val excellentCount = records.count { it.totalScore >= 9.0f }
        val goodCount = records.count { it.totalScore >= 8.0f && it.totalScore < 9.0f }
        val fairCount = records.count { it.totalScore >= 6.5f && it.totalScore < 8.0f }
        val needsImprovementCount = records.count { it.totalScore < 6.5f }

        val excellentGoodPct = if (total > 0) ((excellentCount + goodCount).toFloat() / total * 100).toInt() else 0

        // Phân tích lỗi từ errorsJson
        val errorCategories = mutableMapOf<String, Int>()
        records.forEach { record ->
            try {
                val adapter = moshi.adapter<List<Map<String, Any>>>(listType)
                val errors = adapter.fromJson(record.errorsJson) ?: emptyList()
                errors.forEach { err ->
                    val type = err["errorType"]?.toString() ?: "khac"
                    errorCategories[type] = (errorCategories[type] ?: 0) + 1
                }
            } catch (_: Exception) { /* skip malformed JSON */ }
        }

        val totalErrors = errorCategories.values.sum()
        val errorStats = errorCategories.entries
            .sortedByDescending { it.value }
            .take(5)
            .map { (type, count) ->
                ErrorCategoryData(
                    categoryName = errorTypeDisplayName(type),
                    count = count,
                    percentage = if (totalErrors > 0) count.toFloat() / totalErrors else 0f
                )
            }

        // Học sinh cần rèn luyện — nhóm theo tên
        val studentGroups = records.groupBy { it.studentName }
        val underperforming = studentGroups
            .map { (name, recs) ->
                val avg = recs.map { it.totalScore }.average().toFloat()
                val cls = recs.firstOrNull()?.className ?: ""
                Triple(name, cls, avg)
            }
            .filter { it.third < 6.5f }
            .sortedBy { it.third }
            .take(5)
            .map { (name, cls, avg) ->
                UnderperformingStudentData(
                    name = name,
                    className = cls,
                    averageScore = avg
                )
            }

        return ReportStats(
            totalGraded = total,
            averageScore = avgScore,
            excellentCount = excellentCount,
            goodCount = goodCount,
            fairCount = fairCount,
            needsImprovementCount = needsImprovementCount,
            excellentGoodPct = excellentGoodPct,
            totalErrors = totalErrors,
            errorCategories = errorStats,
            underperformingStudents = underperforming
        )
    }

    private fun errorTypeDisplayName(type: String): String = when (type) {
        "phu_am_dau" -> "Phụ âm đầu (s/x, ch/tr, d/gi/r, l/n)"
        "van" -> "Vần khó (an/ang, uôn/uông, iên/iêng)"
        "dau_thanh" -> "Dấu thanh điệu (Hỏi / Ngã)"
        "viet_hoa" -> "Viết hoa đầu dòng & quy chuẩn vở ô ly"
        "bo_sot_them" -> "Bỏ sót / thêm từ"
        "ngu_phap" -> "Ngữ pháp / Cấu trúc câu"
        else -> type.replace("_", " ").replaceFirstChar { it.uppercaseChar() }
    }

    private fun buildCsvContent(records: List<GradeRecordEntity>): String {
        val sb = StringBuilder()
        // BOM for UTF-8 Excel compatibility
        sb.append("\uFEFF")
        sb.appendLine("STT,Họ tên,Lớp,Bài thi,Chính tả,Hình thức,Nội dung,Sáng tạo,Tổng điểm,Xếp loại,Nhận xét,Ngày chấm")

        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        records.forEachIndexed { idx, r ->
            val rating = when {
                r.totalScore >= 9.0f -> "Xuất sắc"
                r.totalScore >= 8.0f -> "Tốt"
                r.totalScore >= 6.5f -> "Khá"
                else -> "Cần rèn luyện"
            }
            val comment = r.pedagogicalComment
                .replace("\"", "\"\"") // Escape double quotes for CSV
                .replace("\n", " ")
            sb.appendLine(
                "${idx + 1},\"${r.studentName}\",\"${r.className}\",\"${r.essayTitle}\"," +
                "${r.spellingScore},${r.formatScore},${r.contentScore},${r.creativityScore}," +
                "${r.totalScore},\"$rating\",\"$comment\",\"${dateFormat.format(Date(r.timestamp))}\""
            )
        }
        return sb.toString()
    }

    private fun saveCsvFile(fileName: String, content: String): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10+ : MediaStore API (scoped storage)
            val values = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                put(MediaStore.Downloads.MIME_TYPE, "text/csv")
                put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
            val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                ?: throw Exception("Không thể tạo file trong Downloads")
            context.contentResolver.openOutputStream(uri)?.use { it.write(content.toByteArray(Charsets.UTF_8)) }
            "Downloads/$fileName"
        } else {
            // Android 9 trở xuống
            @Suppress("DEPRECATION")
            val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(dir, fileName)
            FileWriter(file).use { it.write(content) }
            file.absolutePath
        }
    }
}

// ============================================================
// DATA MODELS
// ============================================================
data class ReportStats(
    val totalGraded: Int,
    val averageScore: Float,
    val excellentCount: Int,
    val goodCount: Int,
    val fairCount: Int,
    val needsImprovementCount: Int,
    val excellentGoodPct: Int,
    val totalErrors: Int,
    val errorCategories: List<ErrorCategoryData>,
    val underperformingStudents: List<UnderperformingStudentData>
) {
    companion object {
        val EMPTY = ReportStats(0, 0f, 0, 0, 0, 0, 0, 0, emptyList(), emptyList())
    }
}

data class ErrorCategoryData(
    val categoryName: String,
    val count: Int,
    val percentage: Float
)

data class UnderperformingStudentData(
    val name: String,
    val className: String,
    val averageScore: Float
)

data class ExportResult(
    val success: Boolean,
    val message: String,
    val filePath: String? = null
)
