package com.example.ui.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.LoginRequest
import com.example.data.api.NetworkClient
import com.example.data.api.UserData
import com.example.data.local.UserSessionManager
import com.example.data.model.ErrorBox
import com.example.data.model.GradeResult
import com.example.data.repository.GradeRepository
import com.example.data.repository.SampleEssays
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class GradingUiState {
    object Idle : GradingUiState()
    data class Processing(val stepDescription: String, val progress: Float) : GradingUiState()
    data class Success(val result: GradeResult) : GradingUiState()
    data class Error(val message: String) : GradingUiState()
}

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = GradeRepository(application.applicationContext)
    private val sessionManager = UserSessionManager(application.applicationContext)

    // Auth & User State
    private val _currentUser = MutableStateFlow<UserData?>(sessionManager.getUser())
    val currentUser: StateFlow<UserData?> = _currentUser.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(sessionManager.isLoggedIn())
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _loginLoading = MutableStateFlow(false)
    val loginLoading: StateFlow<Boolean> = _loginLoading.asStateFlow()

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError.asStateFlow()

    private val _gradingState = MutableStateFlow<GradingUiState>(GradingUiState.Idle)
    val gradingState: StateFlow<GradingUiState> = _gradingState.asStateFlow()

    private val _currentResult = MutableStateFlow<GradeResult?>(SampleEssays.sample2Good)
    val currentResult: StateFlow<GradeResult?> = _currentResult.asStateFlow()

    private val _selectedErrorId = MutableStateFlow<String?>(SampleEssays.sample2Good.errors.firstOrNull()?.id)
    val selectedErrorId: StateFlow<String?> = _selectedErrorId.asStateFlow()

    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    private val _serverUrl = MutableStateFlow(NetworkClient.DEFAULT_BASE_URL)
    val serverUrl: StateFlow<String> = _serverUrl.asStateFlow()

    private val _pingStatus = MutableStateFlow<Pair<Boolean?, String>>(Pair(null, "Chưa kiểm tra"))
    val pingStatus: StateFlow<Pair<Boolean?, String>> = _pingStatus.asStateFlow()

    private val _isPinging = MutableStateFlow(false)
    val isPinging: StateFlow<Boolean> = _isPinging.asStateFlow()

    val historyRecords: StateFlow<List<GradeResult>> = repository.allGradedRecords
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Phase 5: Classes, Students & Cloud Sync State
    private val _classList = MutableStateFlow<List<com.example.data.api.ClassItem>>(com.example.data.repository.GradeRepository.defaultClasses)
    val classList: StateFlow<List<com.example.data.api.ClassItem>> = _classList.asStateFlow()

    private val _studentList = MutableStateFlow<List<com.example.data.api.StudentItem>>(com.example.data.repository.GradeRepository.defaultStudents)
    val studentList: StateFlow<List<com.example.data.api.StudentItem>> = _studentList.asStateFlow()

    private val _selectedClass = MutableStateFlow("Lớp 3A")
    val selectedClass: StateFlow<String> = _selectedClass.asStateFlow()

    private val _selectedStudent = MutableStateFlow("Nguyễn Văn An")
    val selectedStudent: StateFlow<String> = _selectedStudent.asStateFlow()

    private val _syncStatus = MutableStateFlow<Pair<Boolean?, String>>(Pair(null, "Chưa đồng bộ"))
    val syncStatus: StateFlow<Pair<Boolean?, String>> = _syncStatus.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val _serverGradesList = MutableStateFlow<List<GradeResult>>(emptyList())
    val serverGradesList: StateFlow<List<GradeResult>> = _serverGradesList.asStateFlow()

    private val _isLoadingServerGrades = MutableStateFlow(false)
    val isLoadingServerGrades: StateFlow<Boolean> = _isLoadingServerGrades.asStateFlow()

    init {
        // Pre-populate sample in database if history is empty
        viewModelScope.launch {
            repository.saveRecord(SampleEssays.sample2Good)
            repository.saveRecord(SampleEssays.sample1Eureka)
            fetchClassesAndStudents()
            fetchServerGrades()
        }
    }

    fun setSelectedClass(cls: String) {
        _selectedClass.value = cls
        val inClass = _studentList.value.filter { it.className == cls }
        if (inClass.isNotEmpty()) {
            _selectedStudent.value = inClass[0].name
        }
    }

    fun setSelectedStudent(student: String) {
        _selectedStudent.value = student
    }

    fun fetchClassesAndStudents() {
        viewModelScope.launch {
            try {
                val classes = repository.getClassesList(_serverUrl.value)
                if (classes.isNotEmpty()) _classList.value = classes
                val students = repository.getStudentsList(_serverUrl.value)
                if (students.isNotEmpty()) _studentList.value = students
            } catch (_: Exception) {}
        }
    }

    fun fetchServerGrades(className: String? = null) {
        viewModelScope.launch {
            _isLoadingServerGrades.value = true
            try {
                val list = repository.fetchGradesFromServer(_serverUrl.value, className)
                if (list.isNotEmpty()) {
                    _serverGradesList.value = list
                }
            } catch (e: Exception) {
                android.util.Log.e("MainViewModel", "Lỗi fetchServerGrades: ${e.message}")
            } finally {
                _isLoadingServerGrades.value = false
            }
        }
    }

    fun syncAllGradesToServer() {
        viewModelScope.launch {
            _isSyncing.value = true
            _syncStatus.value = Pair(null, "Đang đồng bộ sổ điểm lên máy chủ...")
            try {
                val (success, fail) = repository.syncAllGradesToServer(_serverUrl.value)
                _isSyncing.value = false
                if (fail == 0 && success > 0) {
                    _syncStatus.value = Pair(true, "Đã đồng bộ thành công $success bài chấm về trường!")
                } else if (success > 0) {
                    _syncStatus.value = Pair(true, "Đã gửi $success bài, lỗi $fail bài.")
                } else {
                    _syncStatus.value = Pair(false, "Không có bài cần gửi hoặc kết nối máy chủ gián đoạn.")
                }
            } catch (e: Exception) {
                _isSyncing.value = false
                _syncStatus.value = Pair(false, "Lỗi đồng bộ: ${e.localizedMessage}")
            }
        }
    }

    fun toggleDarkTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
    }

    fun setDarkTheme(dark: Boolean) {
        _isDarkTheme.value = dark
    }

    fun gradeBitmap(
        bitmap: Bitmap,
        studentGrade: Int = 3,
        studentName: String = _selectedStudent.value,
        className: String = _selectedClass.value
    ) {
        viewModelScope.launch {
            _gradingState.value = GradingUiState.Processing("1/4. Tiền xử lý ảnh: Khử bóng, cân bằng trắng CLAHE...", 0.25f)
            delay(150)
            _gradingState.value = GradingUiState.Processing("2/4. Google Gemini Flash Lite bóc tách văn bản chữ viết tay...", 0.50f)
            delay(180)
            _gradingState.value = GradingUiState.Processing("3/4. YOLOv8 quét tọa độ Bounding Box từng từ viết tay...", 0.75f)
            delay(150)
            _gradingState.value = GradingUiState.Processing("4/4. ViT5 & Qwen SLM kiểm tra chính tả & sinh lời nhận xét...", 0.90f)

            try {
                val result = repository.gradeImage(
                    bitmap = bitmap,
                    serverUrl = _serverUrl.value,
                    studentGrade = studentGrade,
                    studentName = studentName,
                    className = className
                )
                _currentResult.value = result
                _selectedErrorId.value = result.errors.firstOrNull()?.id
                _gradingState.value = GradingUiState.Success(result)
                // Tự động làm mới danh sách bài chấm từ Server về điện thoại
                fetchServerGrades()
            } catch (e: Exception) {
                _gradingState.value = GradingUiState.Error(e.message ?: "Có lỗi khi kết nối máy chủ chấm bài")
            }
        }
    }

    fun loadSample(sample: GradeResult) {
        viewModelScope.launch {
            _gradingState.value = GradingUiState.Processing("Đang tải dữ liệu bài thi mẫu Euréka...", 0.5f)
            delay(100)
            _currentResult.value = sample
            _selectedErrorId.value = sample.errors.firstOrNull()?.id
            _gradingState.value = GradingUiState.Success(sample)
            repository.saveRecord(sample)
        }
    }

    fun selectError(errorId: String?) {
        _selectedErrorId.value = errorId
    }

    fun updateServerUrl(newUrl: String) {
        _serverUrl.value = newUrl.trim()
        fetchClassesAndStudents()
        fetchServerGrades()
    }

    fun resetGradingState() {
        _gradingState.value = GradingUiState.Idle
    }

    fun testConnection() {
        viewModelScope.launch {
            _isPinging.value = true
            val result = repository.testConnection(_serverUrl.value)
            _pingStatus.value = result
            _isPinging.value = false
        }
    }

    fun deleteHistoryItem(idString: String) {
        viewModelScope.launch {
            idString.toLongOrNull()?.let {
                repository.deleteRecord(it)
            }
        }
    }

    fun clearCurrentResult() {
        _currentResult.value = null
        _selectedErrorId.value = null
        _gradingState.value = GradingUiState.Idle
    }

    // ==========================================
    // AUTH & RBAC ACTIONS
    // ==========================================
    fun login(username: String, password: String) {
        viewModelScope.launch {
            _loginLoading.value = true
            _loginError.value = null
            val result = repository.login(_serverUrl.value, LoginRequest(username, password))
            _loginLoading.value = false
            result.onSuccess { userData ->
                sessionManager.saveUser(userData)
                _currentUser.value = userData
                _isLoggedIn.value = true
                _loginError.value = null
                // Tự động gán lớp quản lý nếu là GV
                userData.classes?.firstOrNull()?.let { firstCls ->
                    _selectedClass.value = firstCls
                }
                fetchClassesAndStudents()
                fetchServerGrades()
            }.onFailure { err ->
                _loginError.value = err.message ?: "Tài khoản hoặc mật khẩu không chính xác"
            }
        }
    }

    fun loginAsGuest(role: String = "teacher") {
        sessionManager.loginAsGuest(role)
        _currentUser.value = sessionManager.getUser()
        _isLoggedIn.value = true
        _loginError.value = null
        fetchClassesAndStudents()
        fetchServerGrades()
    }

    fun logout() {
        sessionManager.logout()
        _currentUser.value = null
        _isLoggedIn.value = false
    }

    // ==========================================
    // CONTINUOUS BATCH SCAN MODE
    // ==========================================
    fun gradeBatchBitmaps(
        bitmaps: List<Bitmap>,
        className: String,
        studentGrade: Int = 3
    ) {
        viewModelScope.launch {
            val total = bitmaps.size
            if (total == 0) return@launch

            _gradingState.value = GradingUiState.Processing("Bắt đầu chấm cả lớp ($total bài)...", 0.05f)

            var successCount = 0
            var failCount = 0
            var lastSuccessResult: GradeResult? = null

            bitmaps.forEachIndexed { index, bmp ->
                val currentNum = index + 1
                val progress = currentNum.toFloat() / total.toFloat()
                _gradingState.value = GradingUiState.Processing(
                    "Đang chấm bài $currentNum / $total (${(progress * 100).toInt()}%)...",
                    progress
                )

                try {
                    val res = repository.gradeImage(
                        bitmap = bmp,
                        serverUrl = _serverUrl.value,
                        studentGrade = studentGrade,
                        studentName = "Học sinh $currentNum",
                        className = className
                    )
                    successCount++
                    lastSuccessResult = res
                } catch (_: Exception) {
                    failCount++
                }
            }

            fetchServerGrades()

            if (lastSuccessResult != null) {
                _currentResult.value = lastSuccessResult
                _selectedErrorId.value = lastSuccessResult?.errors?.firstOrNull()?.id
                _gradingState.value = GradingUiState.Success(lastSuccessResult!!)
            } else {
                _gradingState.value = GradingUiState.Idle
            }

            _syncStatus.value = Pair(
                failCount == 0,
                "Hoàn thành chấm $total bài: Thành công $successCount bài, Lỗi $failCount bài."
            )
        }
    }

    // ==========================================
    // TOUCH ERROR EDITOR & SCORE OVERRIDE
    // ==========================================
    fun saveModifiedGrade(modified: GradeResult) {
        viewModelScope.launch {
            _currentResult.value = modified
            _selectedErrorId.value = modified.errors.firstOrNull()?.id
            // Cập nhật Room DB
            repository.saveRecord(modified)
            // Cập nhật Server SQLite vihand.db
            repository.updateGradeOnServer(
                serverUrl = _serverUrl.value,
                gradeId = modified.id,
                updatedErrors = modified.errors,
                newCriteria = modified.criteria,
                pedagogicalComment = modified.pedagogicalComment
            )
            fetchServerGrades()
        }
    }
}
