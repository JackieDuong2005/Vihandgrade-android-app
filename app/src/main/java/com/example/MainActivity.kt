package com.example

import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.TextButton
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Description
import androidx.compose.runtime.LaunchedEffect
import com.example.ui.screens.CameraScanScreen
import com.example.ui.screens.DictationScreen
import com.example.ui.screens.GradingResultScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.ReportsAnalyticsScreen
import com.example.ui.screens.ServerSettingsScreen
import com.example.ui.theme.AppTheme
import com.example.ui.theme.AccentCoral
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.GradingUiState
import com.example.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDarkTheme by viewModel.isDarkTheme.collectAsStateWithLifecycle()
            val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
            val loginLoading by viewModel.loginLoading.collectAsStateWithLifecycle()
            val loginError by viewModel.loginError.collectAsStateWithLifecycle()
            val serverUrl by viewModel.serverUrl.collectAsStateWithLifecycle()

            MyApplicationTheme(darkTheme = isDarkTheme) {
                if (!isLoggedIn) {
                    LoginScreen(
                        isLoading = loginLoading,
                        errorMessage = loginError,
                        serverUrl = serverUrl,
                        onLogin = { username, password ->
                            viewModel.login(username, password)
                        },
                        onLoginAsGuest = { role: String ->
                            viewModel.loginAsGuest(role)
                        },
                        onSaveServerUrl = { newUrl ->
                            viewModel.updateServerUrl(newUrl)
                        },
                        isDarkTheme = isDarkTheme,
                        onToggleTheme = { viewModel.toggleDarkTheme() }
                    )
                } else {
                    ViHandGradeApp(
                        viewModel = viewModel,
                        isDarkTheme = isDarkTheme,
                        onToggleTheme = { viewModel.toggleDarkTheme() }
                    )
                }
            }
        }
    }
}

@Composable
fun ViHandGradeApp(
    viewModel: MainViewModel,
    isDarkTheme: Boolean = false,
    onToggleTheme: () -> Unit = {}
) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val gradingState by viewModel.gradingState.collectAsStateWithLifecycle()
    val currentResult by viewModel.currentResult.collectAsStateWithLifecycle()
    val selectedErrorId by viewModel.selectedErrorId.collectAsStateWithLifecycle()
    val historyList by viewModel.historyRecords.collectAsStateWithLifecycle()
    val serverUrl by viewModel.serverUrl.collectAsStateWithLifecycle()
    val pingStatus by viewModel.pingStatus.collectAsStateWithLifecycle()
    val isPinging by viewModel.isPinging.collectAsStateWithLifecycle()
    val syncStatus by viewModel.syncStatus.collectAsStateWithLifecycle()
    val isSyncing by viewModel.isSyncing.collectAsStateWithLifecycle()
    val classList by viewModel.classList.collectAsStateWithLifecycle()
    val studentList by viewModel.studentList.collectAsStateWithLifecycle()
    val serverGrades by viewModel.serverGradesList.collectAsStateWithLifecycle()

    val schoolName by viewModel.schoolName.collectAsStateWithLifecycle()
    val penaltyPerError by viewModel.penaltyPerError.collectAsStateWithLifecycle()
    val autoEncouragement by viewModel.autoEncouragement.collectAsStateWithLifecycle()
    val autoBoundingBox by viewModel.autoBoundingBox.collectAsStateWithLifecycle()
    val ttsVoice by viewModel.ttsVoice.collectAsStateWithLifecycle()
    val ttsSpeed by viewModel.ttsSpeed.collectAsStateWithLifecycle()
    val photoCacheSizeBytes by viewModel.photoCacheSizeBytes.collectAsStateWithLifecycle()

    // 5 Screen tabs matching the HTML Mockup: "home" (default), "grade", "camera", "dictation", "settings"
    var activeTab by remember { mutableStateOf("home") }

    // Intercept hardware/system back button so user is never trapped in any sub-screen
    BackHandler(enabled = activeTab != "home") {
        activeTab = "home"
    }

    // Only navigate to "grade" tab when grading state transitions to Success
    LaunchedEffect(gradingState) {
        if (gradingState is GradingUiState.Success) {
            activeTab = "grade"
        }
    }

    Scaffold(
        bottomBar = {
            // 5-Tab Bottom Navigation Bar matching HTML Mockup (always accessible across all screens)
            // [1.Tổng quan] [2.Điểm số] [3.Chấm bài (Center)] [4.Chính tả] [5.Trạm Pi]
            Surface(
                color = AppTheme.colors.card,
                tonalElevation = 8.dp,
                shadowElevation = 12.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, AppTheme.colors.border),
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Tab 1: Tổng quan
                        BottomNavTabItem(
                            icon = Icons.Default.Dashboard,
                            label = "Tổng quan",
                            isSelected = activeTab == "home",
                            onClick = { activeTab = "home" },
                            testTag = "nav_home"
                        )

                        // Tab 2: Điểm số
                        BottomNavTabItem(
                            icon = Icons.Default.BarChart,
                            label = "Điểm số",
                            isSelected = activeTab == "grade",
                            onClick = { activeTab = "grade" },
                            testTag = "nav_grade"
                        )

                        // Tab 3: Chấm bài (Elevated Center Button)
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .offset(y = (-6).dp)
                                .clickable { activeTab = "camera" }
                                .testTag("nav_camera")
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(if (AppTheme.colors.isDark) EmeraldPrimary else Color(0xFF059669))
                                    .shadow(6.dp, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = "Chấm bài",
                                    tint = if (AppTheme.colors.isDark) Color(0xFF064E3B) else Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Chấm bài",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (activeTab == "camera") AppTheme.colors.primary else AppTheme.colors.textMuted,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        }

                        // Tab 4: Chính tả
                        BottomNavTabItem(
                            icon = Icons.Default.VolumeUp,
                            label = "Chính tả",
                            isSelected = activeTab == "dictation",
                            onClick = { activeTab = "dictation" },
                            testTag = "nav_dictation"
                        )

                        // Tab 5: Cài đặt
                        BottomNavTabItem(
                            icon = Icons.Default.Settings,
                            label = "Cài đặt",
                            isSelected = activeTab == "settings",
                            onClick = { activeTab = "settings" },
                            testTag = "nav_settings"
                        )
                    }
                }
        },
        containerColor = AppTheme.colors.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            Crossfade(targetState = activeTab, label = "TabTransition") { tab ->
                when (tab) {
                    "home" -> {
                        HomeScreen(
                            gradingState = gradingState,
                            historyList = historyList,
                            serverUrl = serverUrl,
                            currentUser = currentUser,
                            onOpenScanner = { activeTab = "camera" },
                            onSelectSample = { sample ->
                                viewModel.loadSample(sample)
                                activeTab = "grade"
                            },
                            onOpenHistory = { activeTab = "history" },
                            onOpenSettings = { activeTab = "settings" },
                            onSelectHistoryItem = { record ->
                                viewModel.loadSample(record)
                                activeTab = "grade"
                            },
                            onOpenReports = { activeTab = "reports" }
                        )
                    }
                    "camera" -> {
                        CameraScanScreen(
                            onCapture = { bitmap: Bitmap ->
                                val isStudent = currentUser?.role == "student"
                                val std = if (isStudent) currentUser?.name else null
                                val cls = if (isStudent) currentUser?.className else null
                                viewModel.gradeBitmap(bitmap, studentName = std ?: "Học sinh", className = cls ?: "Lớp 3A")
                            },
                            onCaptureWithDetails = { bitmap, cls, std, _ ->
                                val isStudent = currentUser?.role == "student"
                                val finalStd = if (isStudent) (currentUser?.name ?: std) else std
                                val finalCls = if (isStudent) (currentUser?.className ?: cls) else cls
                                viewModel.gradeBitmap(
                                    bitmap = bitmap,
                                    studentName = finalStd,
                                    className = finalCls
                                )
                            },
                            onBatchCapture = { bitmaps, selectedClass ->
                                viewModel.gradeBatchBitmaps(bitmaps, selectedClass)
                            },
                            classList = classList.map { it.name },
                            studentList = studentList,
                            onClose = { activeTab = "home" }
                        )
                    }
                    "grade" -> {
                        val displayResult = currentResult ?: historyList.firstOrNull()
                        if (displayResult != null) {
                            GradingResultScreen(
                                result = displayResult,
                                selectedErrorId = selectedErrorId,
                                onSelectError = { errorId -> viewModel.selectError(errorId) },
                                onBack = { activeTab = "home" },
                                onGradeAnother = { activeTab = "camera" },
                                onSelectSample = { sample -> viewModel.loadSample(sample) },
                                onSaveModifiedGrade = { modified -> viewModel.saveModifiedGrade(modified) },
                                isDarkTheme = isDarkTheme,
                                isTeacher = currentUser?.role != "student",
                                onToggleTheme = onToggleTheme
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.Description,
                                        contentDescription = null,
                                        tint = AppTheme.colors.textMuted,
                                        modifier = Modifier.size(56.dp)
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = "Chưa có bài chấm nào",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = AppTheme.colors.textPrimary
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Vui lòng chụp ảnh hoặc tải lên bài thi học sinh để AI bắt đầu chấm điểm.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = AppTheme.colors.textMuted,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(20.dp))
                                    Button(
                                        onClick = { activeTab = "camera" },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                                    ) {
                                        Icon(imageVector = Icons.Default.CameraAlt, contentDescription = null)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Chấm bài ngay", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                    "dictation" -> {
                        DictationScreen(serverUrl = serverUrl)
                    }
                    "settings" -> {
                        ServerSettingsScreen(
                            serverUrl = serverUrl,
                            pingStatus = pingStatus,
                            isPinging = isPinging,
                            syncStatus = syncStatus,
                            isSyncing = isSyncing,
                            localRecordsCount = historyList.size,
                            photoCacheSizeBytes = photoCacheSizeBytes,
                            schoolName = schoolName,
                            penaltyPerError = penaltyPerError,
                            autoEncouragement = autoEncouragement,
                            autoBoundingBox = autoBoundingBox,
                            ttsVoice = ttsVoice,
                            ttsSpeed = ttsSpeed,
                            onSaveUrl = { newUrl -> viewModel.updateServerUrl(newUrl) },
                            onPing = { viewModel.testConnection() },
                            onSyncGrades = { viewModel.syncAllGradesToServer() },
                            onRefreshClassesAndStudents = { viewModel.fetchClassesAndStudents() },
                            onClearLocalRecords = { viewModel.clearAllRecords() },
                            onClearPhotoCache = { viewModel.clearPhotoCache() },
                            onUpdateSchoolName = { viewModel.updateSchoolName(it) },
                            onUpdatePenaltyPerError = { viewModel.updatePenaltyPerError(it) },
                            onUpdateAutoEncouragement = { viewModel.updateAutoEncouragement(it) },
                            onUpdateAutoBoundingBox = { viewModel.updateAutoBoundingBox(it) },
                            onUpdateTtsVoice = { viewModel.updateTtsVoice(it) },
                            onUpdateTtsSpeed = { viewModel.updateTtsSpeed(it) },
                            currentUser = currentUser,
                            onLogout = { viewModel.logout() },
                            isDarkTheme = isDarkTheme,
                            onToggleTheme = onToggleTheme
                        )
                    }
                    "history" -> {
                        val displayRecords = if (serverGrades.isNotEmpty()) serverGrades else historyList
                        HistoryScreen(
                            records = displayRecords,
                            onSelectRecord = { record ->
                                viewModel.loadSample(record)
                                activeTab = "grade"
                            },
                            onDeleteRecord = { id -> viewModel.deleteHistoryItem(id) },
                            onBack = { activeTab = "home" }
                        )
                    }
                    "reports" -> {
                        ReportsAnalyticsScreen(
                            onNavigateToGrading = { activeTab = "grade" },
                            onBack = { activeTab = "home" }
                        )
                    }
                    else -> {
                        HomeScreen(
                            gradingState = gradingState,
                            historyList = historyList,
                            serverUrl = serverUrl,
                            onOpenScanner = { activeTab = "camera" },
                            onSelectSample = { sample ->
                                viewModel.loadSample(sample)
                                activeTab = "grade"
                            },
                            onOpenHistory = { activeTab = "history" },
                            onOpenSettings = { activeTab = "settings" },
                            onSelectHistoryItem = { record ->
                                viewModel.loadSample(record)
                                activeTab = "grade"
                            },
                            onOpenReports = { activeTab = "reports" }
                        )
                    }
                }
            }

            // Universal Processing Dialog across all tabs
            if (gradingState is GradingUiState.Processing) {
                val state = gradingState as GradingUiState.Processing
                Dialog(onDismissRequest = {}) {
                    Card(
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = AppTheme.colors.card),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AppTheme.colors.border),
                        modifier = Modifier.fillMaxWidth(0.92f)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(44.dp),
                                strokeWidth = 3.5.dp,
                                color = EmeraldPrimary
                            )
                            Spacer(modifier = Modifier.height(18.dp))
                            Text(
                                text = "ViHand AI Đang Chấm Bài",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = AppTheme.colors.textPrimary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = state.stepDescription,
                                style = MaterialTheme.typography.bodySmall,
                                color = AppTheme.colors.textMuted,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            LinearProgressIndicator(
                                progress = { state.progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(3.dp)),
                                color = EmeraldPrimary,
                                trackColor = AppTheme.colors.border
                            )
                        }
                    }
                }
            }

            // Universal Error Dialog when Server connection fails or Server returns error
            if (gradingState is GradingUiState.Error) {
                val errState = gradingState as GradingUiState.Error
                AlertDialog(
                    onDismissRequest = { viewModel.resetGradingState() },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = AccentCoral,
                            modifier = Modifier.size(36.dp)
                        )
                    },
                    title = {
                        Text(
                            text = "Không Thể Kết Nối Máy Chủ AI",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AppTheme.colors.textPrimary
                        )
                    },
                    text = {
                        Column {
                            Text(
                                text = errState.message,
                                style = MaterialTheme.typography.bodyMedium,
                                color = AppTheme.colors.textSecondary
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Surface(
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
                                color = AppTheme.colors.background,
                                border = androidx.compose.foundation.BorderStroke(1.dp, AppTheme.colors.border),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = "📍 Địa chỉ máy chủ hiện tại:",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = AppTheme.colors.textPrimary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = serverUrl,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = EmeraldPrimary,
                                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    },
                    confirmButton = {
                        Row(horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    viewModel.retryOfflineSimulation()
                                    activeTab = "grade"
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                            ) {
                                Text("Chấm Offline 🧪", fontWeight = FontWeight.Bold, color = Color(0xFF064E3B))
                            }
                            Button(
                                onClick = {
                                    viewModel.resetGradingState()
                                    activeTab = "settings"
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = AppTheme.colors.card),
                                border = androidx.compose.foundation.BorderStroke(1.dp, AppTheme.colors.border),
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                            ) {
                                Text("Đến Cài Đặt", color = AppTheme.colors.textPrimary, fontWeight = FontWeight.Bold)
                            }
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { viewModel.resetGradingState() }) {
                            Text("Đóng", color = AppTheme.colors.textSecondary)
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun BottomNavTabItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp)
            .testTag(testTag)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) AppTheme.colors.primary else AppTheme.colors.textMuted,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) AppTheme.colors.primary else AppTheme.colors.textMuted,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 10.sp
        )
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "ViHand Grade $name", modifier = modifier)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplicationTheme { Greeting("Android") }
}
