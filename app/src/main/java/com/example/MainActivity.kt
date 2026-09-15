package com.example

import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.example.data.model.GradeResult
import com.example.data.repository.SampleEssays
import com.example.ui.screens.CameraScanScreen
import com.example.ui.screens.DictationScreen
import com.example.ui.screens.GradingResultScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ServerSettingsScreen
import com.example.ui.theme.AppTheme
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
            MyApplicationTheme(darkTheme = isDarkTheme) {
                ViHandGradeApp(
                    viewModel = viewModel,
                    isDarkTheme = isDarkTheme,
                    onToggleTheme = { viewModel.toggleDarkTheme() }
                )
            }
        }
    }
}

@Composable
fun ViHandGradeApp(
    viewModel: MainViewModel,
    isDarkTheme: Boolean = true,
    onToggleTheme: () -> Unit = {}
) {
    val gradingState by viewModel.gradingState.collectAsStateWithLifecycle()
    val currentResult by viewModel.currentResult.collectAsStateWithLifecycle()
    val selectedErrorId by viewModel.selectedErrorId.collectAsStateWithLifecycle()
    val historyList by viewModel.historyRecords.collectAsStateWithLifecycle()
    val serverUrl by viewModel.serverUrl.collectAsStateWithLifecycle()
    val pingStatus by viewModel.pingStatus.collectAsStateWithLifecycle()
    val isPinging by viewModel.isPinging.collectAsStateWithLifecycle()

    // 5 Screen tabs matching the HTML Mockup: "home" (default), "grade", "camera", "dictation", "settings"
    var activeTab by remember { mutableStateOf("home") }

    // When grading succeeds, automatically jump to grade tab
    if (currentResult != null && activeTab != "grade" && gradingState is GradingUiState.Success) {
        activeTab = "grade"
    }

    Scaffold(
        bottomBar = {
            if (activeTab != "camera") {
                // 5-Tab Bottom Navigation Bar matching HTML Mockup
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

                        // Tab 5: Trạm Pi
                        BottomNavTabItem(
                            icon = Icons.Default.Dns,
                            label = "Trạm Pi",
                            isSelected = activeTab == "settings",
                            onClick = { activeTab = "settings" },
                            testTag = "nav_settings"
                        )
                    }
                }
            }
        },
        containerColor = AppTheme.colors.background,
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(if (activeTab == "camera") androidx.compose.foundation.layout.PaddingValues(0.dp) else innerPadding)
        ) {
            Crossfade(targetState = activeTab, label = "TabTransition") { tab ->
                when (tab) {
                    "home" -> {
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
                            }
                        )
                    }
                    "camera" -> {
                        CameraScanScreen(
                            onCapture = { bitmap: Bitmap ->
                                viewModel.gradeBitmap(bitmap)
                                activeTab = "grade"
                            },
                            onClose = { activeTab = "home" }
                        )
                    }
                    "grade" -> {
                        // Display active result or default sample
                        val displayResult = currentResult ?: SampleEssays.sample2Good
                        GradingResultScreen(
                            result = displayResult,
                            selectedErrorId = selectedErrorId,
                            onSelectError = { errorId -> viewModel.selectError(errorId) },
                            onBack = { activeTab = "home" },
                            onGradeAnother = { activeTab = "camera" },
                            onSelectSample = { sample -> viewModel.loadSample(sample) },
                            isDarkTheme = isDarkTheme,
                            onToggleTheme = onToggleTheme
                        )
                    }
                    "dictation" -> {
                        DictationScreen()
                    }
                    "settings" -> {
                        ServerSettingsScreen(
                            serverUrl = serverUrl,
                            pingStatus = pingStatus,
                            isPinging = isPinging,
                            onSaveUrl = { newUrl -> viewModel.updateServerUrl(newUrl) },
                            onPing = { viewModel.testConnection() },
                            isDarkTheme = isDarkTheme,
                            onToggleTheme = onToggleTheme
                        )
                    }
                    "history" -> {
                        HistoryScreen(
                            records = historyList,
                            onSelectRecord = { record ->
                                viewModel.loadSample(record)
                                activeTab = "grade"
                            },
                            onDeleteRecord = { id -> viewModel.deleteHistoryItem(id) },
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
                            }
                        )
                    }
                }
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
