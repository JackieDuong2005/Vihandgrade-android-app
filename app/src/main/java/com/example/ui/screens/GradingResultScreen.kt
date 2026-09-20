package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DeviceHub
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.ErrorBox
import com.example.data.model.GradeResult
import com.example.ui.components.HandwritingCanvas
import com.example.ui.components.NotebookBackground
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.AccentCoral
import com.example.ui.theme.AccentIndigo
import com.example.ui.theme.AccentSky
import com.example.ui.theme.AppTheme
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GradingResultScreen(
    result: GradeResult,
    selectedErrorId: String?,
    onSelectError: (String?) -> Unit,
    onBack: (() -> Unit)? = null,
    onGradeAnother: () -> Unit,
    onToggleTheme: () -> Unit = {},
    isDarkTheme: Boolean = true,
    onSelectSample: ((GradeResult) -> Unit)? = null,
    onSaveModifiedGrade: ((GradeResult) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current
    val clipboardManager = LocalClipboardManager.current

    var activeTabMode by remember { mutableStateOf("canvas") } // "canvas", "diff", "skills"
    var canvasSubMode by remember { mutableStateOf("notebook") } // "notebook", "photo"
    var showCertificateDialog by remember { mutableStateOf(false) }
    var showAddErrorDialog by remember { mutableStateOf(false) }

    // Dynamic Errors state (supports deleting and adding manual errors)
    var currentErrors by remember(result) { mutableStateOf(result.errors) }

    // Teacher Override Score State
    var isOverrideOpen by remember { mutableStateOf(false) }
    var overrideSpelling by remember(result) { mutableFloatStateOf(result.criteria.spellingScore) }
    var overrideFormat by remember(result) { mutableFloatStateOf(result.criteria.formatScore) }
    var overrideContent by remember(result) { mutableFloatStateOf(result.criteria.contentScore) }
    var overrideCreativity by remember(result) { mutableFloatStateOf(result.criteria.creativityScore) }

    val currentTotalScore = (overrideSpelling + overrideFormat + overrideContent + overrideCreativity).coerceIn(0f, 10f)
    val currentRatingLevel = when {
        currentTotalScore >= 9.0f -> "Xuất sắc"
        currentTotalScore >= 8.0f -> "Giỏi"
        currentTotalScore >= 6.5f -> "Khá"
        currentTotalScore >= 5.0f -> "Trung bình"
        else -> "Cần cố gắng"
    }

    // Pedagogical Comments State
    var currentComment by remember(result) { mutableStateOf(result.pedagogicalComment) }
    var isEditingComment by remember { mutableStateOf(false) }

    val hasModifications = currentErrors != result.errors ||
            overrideSpelling != result.criteria.spellingScore ||
            overrideFormat != result.criteria.formatScore ||
            overrideContent != result.criteria.contentScore ||
            overrideCreativity != result.criteria.creativityScore ||
            currentComment != result.pedagogicalComment

    val updatedResult = remember(result, currentErrors, overrideSpelling, overrideFormat, overrideContent, overrideCreativity, currentComment, currentTotalScore) {
        result.copy(
            criteria = result.criteria.copy(
                spellingScore = overrideSpelling,
                formatScore = overrideFormat,
                contentScore = overrideContent,
                creativityScore = overrideCreativity,
                totalScore = currentTotalScore
            ),
            pedagogicalComment = currentComment,
            errors = currentErrors
        )
    }

    val selectedError = currentErrors.find { it.id == selectedErrorId } ?: currentErrors.firstOrNull()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.BarChart,
                            contentDescription = null,
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Kết Quả Chấm Bài AI",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AppTheme.colors.textPrimary
                        )
                    }
                },
                navigationIcon = {
                    if (onBack != null) {
                        IconButton(onClick = onBack, modifier = Modifier.testTag("result_back_btn")) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Quay lại",
                                tint = AppTheme.colors.textPrimary
                            )
                        }
                    }
                },
                actions = {
                    // Quick Save Button if modifications exist
                    if (hasModifications && onSaveModifiedGrade != null) {
                        Button(
                            onClick = {
                                onSaveModifiedGrade(updatedResult)
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                scope.launch {
                                    snackbarHostState.showSnackbar("Đã lưu các sửa đổi sư phạm lên máy chủ!")
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentAmber),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.padding(end = 6.dp)
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null, tint = Color.Black, modifier = Modifier.size(13.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Lưu sửa", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Theme Switcher Button (Sun / Moon)
                    IconButton(
                        onClick = onToggleTheme,
                        modifier = Modifier.testTag("theme_toggle_btn")
                    ) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = if (isDarkTheme) "Chuyển giao diện sáng" else "Chuyển giao diện tối",
                            tint = if (isDarkTheme) AccentAmber else EmeraldPrimary
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = if (hasModifications) AccentAmber else EmeraldPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (hasModifications) "Có chỉnh sửa" else "Đã lưu",
                            color = if (hasModifications) AccentAmber else EmeraldPrimary,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppTheme.colors.background
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        NotebookBackground(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {


            // Processing server badge
            item {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = AppTheme.colors.cardElevated,
                    border = androidx.compose.foundation.BorderStroke(1.dp, AppTheme.colors.border),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeviceHub,
                            contentDescription = null,
                            tint = if (isDarkTheme) EmeraldPrimary else Color(0xFF047857),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${result.serverSource} • ${result.processingTimeMs}ms",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isDarkTheme) EmeraldLight else Color(0xFF047857),
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // 3-Mode Inspection Segmented Bar: "Vở Ô Ly", "So Sánh Sửa", "4 Năng Lực"
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = AppTheme.colors.card,
                    border = BorderStroke(1.dp, AppTheme.colors.border),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        val tabs = listOf(
                            Triple("canvas", "Vở Ô Ly", Icons.Default.Edit),
                            Triple("diff", "So Sánh Sửa", Icons.Default.MenuBook),
                            Triple("skills", "4 Năng Lực", Icons.Default.School)
                        )
                        tabs.forEach { (tabKey, title, icon) ->
                            val isSelected = activeTabMode == tabKey
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) (if (isDarkTheme) EmeraldPrimary else Color(0xFF059669)) else Color.Transparent,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        activeTabMode = tabKey
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    }
                                    .testTag("tab_mode_$tabKey")
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 9.dp, horizontal = 6.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = null,
                                        tint = if (isSelected) (if (isDarkTheme) Color(0xFF064E3B) else Color.White) else AppTheme.colors.textMuted,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = title,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                                        color = if (isSelected) (if (isDarkTheme) Color(0xFF064E3B) else Color.White) else AppTheme.colors.textSecondary,
                                        fontSize = 11.5.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // MODE 1: Interactive Canvas & Error Popover
            if (activeTabMode == "canvas") {
                item {
                    Column {
                        // Sub-mode segmented switcher: Vở ô ly vs Ảnh chụp gốc
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = AppTheme.colors.cardElevated,
                            border = BorderStroke(1.dp, AppTheme.colors.border),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(3.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (canvasSubMode == "notebook") (if (isDarkTheme) EmeraldPrimary else Color(0xFF059669)) else Color.Transparent,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable {
                                            canvasSubMode = "notebook"
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        }
                                        .testTag("submode_notebook_btn")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(vertical = 6.dp),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.MenuBook,
                                            contentDescription = null,
                                            tint = if (canvasSubMode == "notebook") (if (isDarkTheme) Color(0xFF064E3B) else Color.White) else AppTheme.colors.textMuted,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Vở Ô Ly 4 Ly (Kính Lúp)",
                                            fontSize = 11.sp,
                                            fontWeight = if (canvasSubMode == "notebook") FontWeight.Bold else FontWeight.Medium,
                                            color = if (canvasSubMode == "notebook") (if (isDarkTheme) Color(0xFF064E3B) else Color.White) else AppTheme.colors.textSecondary
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (canvasSubMode == "photo") (if (isDarkTheme) EmeraldPrimary else Color(0xFF059669)) else Color.Transparent,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable {
                                            canvasSubMode = "photo"
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        }
                                        .testTag("submode_photo_btn")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(vertical = 6.dp),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CameraAlt,
                                            contentDescription = null,
                                            tint = if (canvasSubMode == "photo") (if (isDarkTheme) Color(0xFF064E3B) else Color.White) else AppTheme.colors.textMuted,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Ảnh Chụp Bài (YOLOv8)",
                                            fontSize = 11.sp,
                                            fontWeight = if (canvasSubMode == "photo") FontWeight.Bold else FontWeight.Medium,
                                            color = if (canvasSubMode == "photo") (if (isDarkTheme) Color(0xFF064E3B) else Color.White) else AppTheme.colors.textSecondary
                                        )
                                    }
                                }
                            }
                        }

                        if (canvasSubMode == "notebook") {
                            HandwritingCanvas(
                                result = updatedResult,
                                selectedErrorId = selectedErrorId,
                                onSelectError = { err ->
                                    onSelectError(err.id)
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                }
                            )
                        } else {
                            OriginalPhotoBBoxView(
                                result = updatedResult,
                                selectedErrorId = selectedErrorId,
                                onSelectError = { err: com.example.data.model.ErrorBox ->
                                    onSelectError(err.id)
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                },
                                isDarkTheme = isDarkTheme
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Canvas Hint
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = if (isDarkTheme) EmeraldLight else Color(0xFF047857),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Chạm vào khung đỏ để sửa hoặc bấm nút bên dưới",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isDarkTheme) EmeraldLight else Color(0xFF047857),
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // Error selection chips row + Add manual error chip
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Manual Add Error Button
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = if (isDarkTheme) Color(0xFF1E293B) else Color(0xFFEFF6FF),
                                border = BorderStroke(1.dp, if (isDarkTheme) Color(0xFF3B82F6) else Color(0xFF60A5FA)),
                                modifier = Modifier
                                    .clickable {
                                        showAddErrorDialog = true
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    }
                                    .testTag("add_manual_error_btn")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = null,
                                        tint = if (isDarkTheme) Color(0xFF60A5FA) else Color(0xFF2563EB),
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "+ Bắt thêm lỗi",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isDarkTheme) Color(0xFF60A5FA) else Color(0xFF2563EB),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            currentErrors.forEachIndexed { idx, err ->
                                val isSelected = (selectedErrorId == err.id) || (selectedErrorId == null && idx == 0)
                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = if (isSelected) Color(0xFFD97706) else (if (isDarkTheme) Color(0xFF1E293B) else Color(0xFFFEE2E2)),
                                    border = BorderStroke(
                                        1.dp,
                                        if (isSelected) Color(0xFFF59E0B) else (if (isDarkTheme) Color(0xFFEF4444).copy(alpha = 0.5f) else Color(0xFFFCA5A5))
                                    ),
                                    modifier = Modifier
                                        .clickable {
                                            onSelectError(err.id)
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        }
                                        .testTag("error_chip_${idx + 1}")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "[${idx + 1}] ${err.originalWord}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (isSelected) Color.White else (if (isDarkTheme) Color(0xFFFCA5A5) else Color(0xFF991B1B)),
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "${err.penalty}đ",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (isSelected) Color(0xFFFEF3C7) else (if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFFB91C1C)),
                                            fontSize = 10.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Popover: Chi Tiết Lỗi Chính Tả (ViT5 + YOLOv8)
                if (selectedError != null) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("error_popover_card"),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = AppTheme.colors.card),
                            border = BorderStroke(1.5.dp, AppTheme.colors.border)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        modifier = Modifier.weight(1f, fill = false),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Warning,
                                            contentDescription = null,
                                            tint = AccentCoral,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Phát hiện chính tả: ${selectedError.errorType}",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = AppTheme.colors.textPrimary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(0x336366F1),
                                        border = BorderStroke(1.dp, Color(0x666366F1))
                                    ) {
                                        Text(
                                            text = "ViT5 + YOLOv8",
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = AccentIndigo,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                            fontSize = 10.5.sp,
                                            maxLines = 1,
                                            softWrap = false
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Word diff row
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = AppTheme.colors.cardElevated,
                                    border = BorderStroke(1.dp, AppTheme.colors.border),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = selectedError.originalWord,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = AccentCoral,
                                            fontWeight = FontWeight.Bold,
                                            textDecoration = TextDecoration.LineThrough
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                            contentDescription = null,
                                            tint = AppTheme.colors.textMuted,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = selectedError.correctedWord,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = EmeraldPrimary,
                                            fontWeight = FontWeight.Black
                                        )
                                        Spacer(modifier = Modifier.weight(1f))
                                        Text(
                                            text = "${selectedError.penalty}đ",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = AccentCoral,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = selectedError.explanation,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = AppTheme.colors.textMuted,
                                    lineHeight = 18.sp
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                // Delete false positive error button
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    OutlinedButton(
                                        onClick = {
                                            val deletedError = selectedError
                                            currentErrors = currentErrors.filter { it.id != deletedError.id }
                                            overrideSpelling = (overrideSpelling + deletedError.penalty).coerceAtMost(3.0f)
                                            onSelectError(currentErrors.firstOrNull()?.id)
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            scope.launch {
                                                snackbarHostState.showSnackbar("Đã xóa lỗi '${deletedError.originalWord}' và hoàn ${deletedError.penalty}đ cho bài thi")
                                            }
                                        },
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentCoral),
                                        border = BorderStroke(1.dp, AccentCoral.copy(alpha = 0.5f)),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                        modifier = Modifier.testTag("delete_error_btn")
                                    ) {
                                        Icon(imageVector = Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Xóa lỗi này (AI nhận nhầm)",
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            } else if (activeTabMode == "diff") {
                // MODE 2: Comparative Before & After View
                item {
                    ComparativeDiffSection(
                        result = result,
                        isDarkTheme = isDarkTheme
                    )
                }
            } else {
                // MODE 3: 4 Pedagogical Skills Breakdown
                item {
                    PedagogicalSkillsBreakdownSection(
                        result = result,
                        isDarkTheme = isDarkTheme
                    )
                }
            }

            // Total Score Card (Tổng Điểm Đánh Giá)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("total_score_card"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = AppTheme.colors.card),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AppTheme.colors.border)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "TỔNG ĐIỂM ĐÁNH GIÁ",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = AppTheme.colors.textMuted,
                                        letterSpacing = 1.sp
                                    )
                                    if (isOverrideOpen) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = Color(0xFFFEF3C7)
                                        ) {
                                            Text(
                                                text = "GV Đang Sửa",
                                                color = Color(0xFFB45309),
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = if (isDarkTheme) EmeraldPrimary else Color(0xFF047857),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Xếp loại: $currentRatingLevel",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isDarkTheme) EmeraldPrimary else Color(0xFF047857)
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = "%.1f".format(currentTotalScore),
                                    style = MaterialTheme.typography.displaySmall,
                                    fontWeight = FontWeight.Black,
                                    color = AppTheme.colors.textPrimary
                                )
                                Text(
                                    text = "/10",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = AppTheme.colors.textMuted,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 6.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Toggle button for Teacher Override
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isOverrideOpen) "Kéo thanh trượt để can thiệp điểm:" else "Giáo viên có thể can thiệp điểm theo thực tế",
                                style = MaterialTheme.typography.labelSmall,
                                color = AppTheme.colors.textMuted,
                                fontSize = 11.sp
                            )
                            Button(
                                onClick = { isOverrideOpen = !isOverrideOpen },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isOverrideOpen) (if (isDarkTheme) Color(0xFF334155) else Color(0xFFE2E8F0)) else (if (isDarkTheme) EmeraldPrimary.copy(alpha = 0.2f) else Color(0xFFD1FAE5))
                                ),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier
                                    .height(30.dp)
                                    .testTag("toggle_override_btn")
                            ) {
                                Icon(
                                    imageVector = if (isOverrideOpen) Icons.Default.Check else Icons.Default.Tune,
                                    contentDescription = null,
                                    tint = if (isOverrideOpen) AppTheme.colors.textPrimary else (if (isDarkTheme) EmeraldPrimary else Color(0xFF047857)),
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isOverrideOpen) "Xong" else "Chỉnh điểm",
                                    color = if (isOverrideOpen) AppTheme.colors.textPrimary else (if (isDarkTheme) EmeraldPrimary else Color(0xFF047857)),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Expandable Teacher Override Sliders Panel
                        if (isOverrideOpen) {
                            Spacer(modifier = Modifier.height(14.dp))
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = AppTheme.colors.cardElevated,
                                border = BorderStroke(1.dp, AppTheme.colors.border),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    // Slider 1: Chính tả (0 - 4.0đ)
                                    TeacherScoreSliderRow(
                                        name = "Chính tả",
                                        maxScore = 4.0f,
                                        value = overrideSpelling,
                                        onValueChange = { overrideSpelling = it },
                                        color = AccentCoral
                                    )
                                    // Slider 2: Hình thức (0 - 3.0đ)
                                    TeacherScoreSliderRow(
                                        name = "Hình thức & Chữ viết",
                                        maxScore = 3.0f,
                                        value = overrideFormat,
                                        onValueChange = { overrideFormat = it },
                                        color = if (isDarkTheme) EmeraldPrimary else Color(0xFF047857)
                                    )
                                    // Slider 3: Nội dung (0 - 2.0đ)
                                    TeacherScoreSliderRow(
                                        name = "Nội dung bài viết",
                                        maxScore = 2.0f,
                                        value = overrideContent,
                                        onValueChange = { overrideContent = it },
                                        color = AccentSky
                                    )
                                    // Slider 4: Sáng tạo (0 - 1.0đ)
                                    TeacherScoreSliderRow(
                                        name = "Sáng tạo & Cảm xúc",
                                        maxScore = 1.0f,
                                        value = overrideCreativity,
                                        onValueChange = { overrideCreativity = it },
                                        color = AccentAmber
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))
                                    // Reset to AI scores button
                                    OutlinedButton(
                                        onClick = {
                                            overrideSpelling = result.criteria.spellingScore
                                            overrideFormat = result.criteria.formatScore
                                            overrideContent = result.criteria.contentScore
                                            overrideCreativity = result.criteria.creativityScore
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier
                                            .align(Alignment.End)
                                            .height(30.dp),
                                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Refresh,
                                            contentDescription = null,
                                            modifier = Modifier.size(13.dp),
                                            tint = AppTheme.colors.textMuted
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Đặt lại điểm AI gốc", fontSize = 10.5.sp, color = AppTheme.colors.textMuted)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 4 Breakdown Metric Pills
            item {
                val metricSuccessColor = if (isDarkTheme) EmeraldPrimary else Color(0xFF047857)
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MetricPill(
                            icon = Icons.Default.Edit,
                            name = "Chính tả (4đ)",
                            scoreText = "${"%.1f".format(overrideSpelling)}/4.0",
                            subText = if (overrideSpelling < 4.0f) "-${"%.1f".format(4.0f - overrideSpelling)}" else "Chuẩn",
                            iconColor = AccentCoral,
                            subColor = if (overrideSpelling < 4.0f) AccentCoral else metricSuccessColor,
                            modifier = Modifier.weight(1f)
                        )
                        MetricPill(
                            icon = Icons.Default.Layers,
                            name = "Hình thức (3đ)",
                            scoreText = "${"%.1f".format(overrideFormat)}/3.0",
                            subText = "Thẳng hàng",
                            iconColor = metricSuccessColor,
                            subColor = metricSuccessColor,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MetricPill(
                            icon = Icons.Default.Book,
                            name = "Nội dung (2đ)",
                            scoreText = "${"%.1f".format(overrideContent)}/2.0",
                            subText = "Đủ bài",
                            iconColor = AccentSky,
                            subColor = metricSuccessColor,
                            modifier = Modifier.weight(1f)
                        )
                        MetricPill(
                            icon = Icons.Default.AutoAwesome,
                            name = "Sáng tạo (1đ)",
                            scoreText = "${"%.1f".format(overrideCreativity)}/1.0",
                            subText = "Nét mềm",
                            iconColor = AccentAmber,
                            subColor = metricSuccessColor,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Pedagogical Comment Card (Qwen SLM)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = AppTheme.colors.card),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AppTheme.colors.border)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = if (isDarkTheme) EmeraldPrimary else Color(0xFF047857),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Lời nhận xét sư phạm (Qwen SLM):",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDarkTheme) EmeraldPrimary else Color(0xFF047857)
                                )
                            }

                            // Edit comment toggle
                            IconButton(
                                onClick = { isEditingComment = !isEditingComment },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = if (isEditingComment) Icons.Default.Check else Icons.Default.Edit,
                                    contentDescription = "Chỉnh sửa lời phê",
                                    tint = AppTheme.colors.textMuted,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        if (isEditingComment) {
                            OutlinedTextField(
                                value = currentComment,
                                onValueChange = { currentComment = it },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                textStyle = MaterialTheme.typography.bodyMedium.copy(color = AppTheme.colors.textPrimary),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = if (isDarkTheme) EmeraldPrimary else Color(0xFF059669),
                                    unfocusedBorderColor = AppTheme.colors.border
                                ),
                                minLines = 3
                            )
                        } else {
                            Text(
                                text = "\"$currentComment\"",
                                style = MaterialTheme.typography.bodyMedium,
                                color = AppTheme.colors.textPrimary,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                lineHeight = 22.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // 3 Qwen SLM Suggestion Chips
                        Text(
                            text = "Gợi ý mẫu nhận xét theo văn cảnh:",
                            style = MaterialTheme.typography.labelSmall,
                            color = AppTheme.colors.textMuted,
                            fontSize = 10.5.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        val commentSuggestions = if (result.pedagogicalComments.isNotEmpty()) {
                            result.pedagogicalComments
                        } else {
                            listOf(
                                "Em có ý thức rèn chữ giữ vở rất tốt, chữ viết đều nét và sạch sẽ.",
                                "Em cần chú ý phân biệt phụ âm s/x và các vần có âm cuối n/ng.",
                                "Bài viết giàu cảm xúc, em cố gắng phát huy và rèn thêm dấu thanh hỏi/ngã nhé!"
                            )
                        }
                        val suggestionLabels = listOf("🌟 Khích lệ", "🔍 Chỉ rõ lỗi", "✍️ Khen chữ", "💡 Hướng dẫn")

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            commentSuggestions.forEachIndexed { idx, commentText ->
                                val label = suggestionLabels.getOrElse(idx) { "📝 Gợi ý ${idx + 1}" }
                                val isSelected = currentComment == commentText
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = if (isSelected) (if (isDarkTheme) Color(0xFF065F46) else Color(0xFFD1FAE5)) else AppTheme.colors.cardElevated,
                                    border = BorderStroke(1.dp, if (isSelected) EmeraldPrimary else AppTheme.colors.border),
                                    modifier = Modifier.clickable {
                                        currentComment = commentText
                                        isEditingComment = false
                                    }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AppTheme.colors.textPrimary)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Action Row: "Lưu Điểm & Báo Phụ Huynh", "Lưu Sửa Đổi Lên Máy Chủ", "Chụp Tiếp"
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Highlighted Server Sync button if changes exist
                    if (hasModifications && onSaveModifiedGrade != null) {
                        Button(
                            onClick = {
                                onSaveModifiedGrade(updatedResult)
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                scope.launch {
                                    snackbarHostState.showSnackbar("Đã đồng bộ sửa đổi sư phạm lên máy chủ!")
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("save_server_changes_btn"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AccentAmber
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Save,
                                contentDescription = null,
                                tint = Color.Black
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Lưu Sửa Đổi Lên Máy Chủ 💾",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Button(
                        onClick = {
                            showCertificateDialog = true
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("save_and_notify_btn"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isDarkTheme) EmeraldPrimary else Color(0xFF059669)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = null,
                            tint = if (isDarkTheme) Color(0xFF064E3B) else Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Lưu Điểm & Báo Phụ Huynh",
                            color = if (isDarkTheme) Color(0xFF064E3B) else Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    OutlinedButton(
                        onClick = onGradeAnother,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("scan_next_btn"),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AppTheme.colors.border)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = null,
                            tint = AppTheme.colors.textPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Chụp Tiếp",
                            color = AppTheme.colors.textPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(48.dp))
            }
        }
        }

        // Dialog for adding manual error
        if (showAddErrorDialog) {
            var manualOriginal by remember { mutableStateOf("") }
            var manualCorrected by remember { mutableStateOf("") }
            var manualType by remember { mutableStateOf("Chính tả") }
            val manualPenalty = 0.5f

            Dialog(onDismissRequest = { showAddErrorDialog = false }) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = AppTheme.colors.card,
                    border = BorderStroke(1.dp, AppTheme.colors.border),
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Bắt lỗi thủ công (Giáo viên)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AppTheme.colors.textPrimary
                        )
                        Spacer(Modifier.height(12.dp))
                        OutlinedTextField(
                            value = manualOriginal,
                            onValueChange = { manualOriginal = it },
                            label = { Text("Từ viết sai (ví dụ: si nghĩ)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = manualCorrected,
                            onValueChange = { manualCorrected = it },
                            label = { Text("Từ sửa đúng (ví dụ: suy nghĩ)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = manualType,
                            onValueChange = { manualType = it },
                            label = { Text("Loại lỗi (Chính tả / Dấu câu / Ngữ pháp)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            androidx.compose.material3.TextButton(onClick = { showAddErrorDialog = false }) {
                                Text("Hủy", color = AppTheme.colors.textMuted)
                            }
                            Spacer(Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    if (manualOriginal.isNotBlank() && manualCorrected.isNotBlank()) {
                                        val newId = "manual_${System.currentTimeMillis()}"
                                        val newErr = ErrorBox(
                                            id = newId,
                                            originalWord = manualOriginal.trim(),
                                            correctedWord = manualCorrected.trim(),
                                            errorType = manualType.trim(),
                                            explanation = "Lỗi do giáo viên bổ sung trực tiếp trên bài chấm.",
                                            penalty = manualPenalty,
                                            x1 = 0.35f,
                                            y1 = 0.45f,
                                            x2 = 0.55f,
                                            y2 = 0.51f
                                        )
                                        currentErrors = currentErrors + newErr
                                        overrideSpelling = (overrideSpelling - manualPenalty).coerceAtLeast(0f)
                                        onSelectError(newId)
                                        showAddErrorDialog = false
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        scope.launch {
                                            snackbarHostState.showSnackbar("Đã thêm lỗi '$manualOriginal' và trừ ${manualPenalty}đ")
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                            ) {
                                Text("Thêm lỗi", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        if (showCertificateDialog) {
            StudentCertificateDialog(
                result = updatedResult,
                overrideTotalScore = currentTotalScore,
                overrideRatingLevel = currentRatingLevel,
                overrideSpelling = overrideSpelling,
                overrideFormat = overrideFormat,
                overrideContent = overrideContent,
                overrideCreativity = overrideCreativity,
                overrideComment = currentComment,
                isTeacherOverridden = hasModifications,
                isDarkTheme = isDarkTheme,
                onDismiss = { showCertificateDialog = false },
                onCopyZaloMessage = {
                    val msg = """
                        📋 [PHIẾU BÁO ĐIỂM TIỂU HỌC - VIHAND AI]
                        Kính gửi Quý Phụ huynh em ${result.studentName} (${result.className}),
                        • Bài viết: ${result.essayTitle}
                        • Tổng điểm: ${"%.1f".format(currentTotalScore)}/10 ($currentRatingLevel)
                        • Điểm Chính tả: ${"%.1f".format(overrideSpelling)}/4.0 | Nét chữ: ${"%.1f".format(overrideFormat)}/3.0 | Nội dung: ${"%.1f".format(overrideContent)}/2.0
                        • Lỗi chính tả: ${currentErrors.size} lỗi (${currentErrors.joinToString(", ") { "${it.originalWord} -> ${it.correctedWord}" }})
                        • Lời cô giáo dặn dò: "$currentComment"
                        Trân trọng gửi gia đình để cùng phối hợp động viên bé rèn chữ mỗi ngày!
                    """.trimIndent()
                    clipboardManager.setText(AnnotatedString(msg))
                    scope.launch {
                        snackbarHostState.showSnackbar("Đã sao chép tin nhắn Zalo gửi phụ huynh!")
                    }
                }
            )
        }
    }
}

@Composable
private fun ComparativeDiffSection(
    result: GradeResult,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    val totalWords = remember(result.extractedText) {
        result.extractedText.trim().split(Regex("\\s+")).filter { it.isNotBlank() }.size
    }
    val errorCount = result.errors.size
    val accuracyPercent = if (totalWords > 0) {
        (((totalWords - errorCount).toFloat() / totalWords) * 100).toInt().coerceIn(0, 100)
    } else 100

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Stats Banner
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = AppTheme.colors.card,
            border = BorderStroke(1.dp, AppTheme.colors.border),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Tổng số từ",
                        style = MaterialTheme.typography.labelSmall,
                        color = AppTheme.colors.textMuted
                    )
                    Text(
                        text = "$totalWords từ",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AppTheme.colors.textPrimary
                    )
                }
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(28.dp)
                        .background(AppTheme.colors.border)
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Từ sai",
                        style = MaterialTheme.typography.labelSmall,
                        color = AppTheme.colors.textMuted
                    )
                    Text(
                        text = "$errorCount lỗi",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AccentCoral
                    )
                }
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(28.dp)
                        .background(AppTheme.colors.border)
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Độ chuẩn xác",
                        style = MaterialTheme.typography.labelSmall,
                        color = AppTheme.colors.textMuted
                    )
                    Text(
                        text = "$accuracyPercent%",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (accuracyPercent >= 90) (if (isDarkTheme) EmeraldPrimary else Color(0xFF047857)) else AccentAmber
                    )
                }
            }
        }

        // Before: Original Student Handwriting
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isDarkTheme) Color(0xFF1E293B) else Color(0xFFFFFDF8)
            ),
            border = BorderStroke(1.5.dp, if (isDarkTheme) Color(0xFF334155) else Color(0xFFFDE68A)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = AccentCoral.copy(alpha = 0.15f),
                            modifier = Modifier.size(24.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "1",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = AccentCoral,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Bản Viết Tay Học Sinh (Gốc)",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = if (isDarkTheme) Color(0xFFFCA5A5) else Color(0xFF991B1B)
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = AccentCoral.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "Có $errorCount lỗi cần sửa",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = AccentCoral,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.5.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isDarkTheme) Color(0xFF0F172A) else Color(0xFFFEFCE8),
                    border = BorderStroke(1.dp, if (isDarkTheme) Color(0xFF334155) else Color(0xFFFEF08A)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = result.extractedText.trim(),
                        modifier = Modifier.padding(14.dp),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            lineHeight = 24.sp,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Serif
                        ),
                        color = if (isDarkTheme) Color(0xFFE2E8F0) else Color(0xFF1E3A8A)
                    )
                }
            }
        }

        // After: Corrected Model Version
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isDarkTheme) Color(0xFF064E3B).copy(alpha = 0.2f) else Color(0xFFF0FDF4)
            ),
            border = BorderStroke(1.5.dp, if (isDarkTheme) EmeraldPrimary else Color(0xFF86EFAC)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = if (isDarkTheme) EmeraldPrimary else Color(0xFF16A34A),
                            modifier = Modifier.size(24.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Bản Chuẩn AI Đã Sửa (Bộ GD&ĐT)",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = if (isDarkTheme) EmeraldLight else Color(0xFF166534)
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isDarkTheme) Color(0xFF064E3B) else Color(0xFFDCFCE7)
                    ) {
                        Text(
                            text = "Chuẩn ngữ âm",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isDarkTheme) EmeraldLight else Color(0xFF15803D),
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.5.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isDarkTheme) Color(0xFF0F172A) else Color.White,
                    border = BorderStroke(1.dp, if (isDarkTheme) EmeraldPrimary.copy(alpha = 0.3f) else Color(0xFFBBF7D0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = result.correctedFullText.trim(),
                        modifier = Modifier.padding(14.dp),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            lineHeight = 24.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Serif
                        ),
                        color = if (isDarkTheme) Color(0xFFF0FDF4) else Color(0xFF14532D)
                    )
                }
            }
        }

        // Word-by-word diff cards
        if (result.errors.isNotEmpty()) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AppTheme.colors.card),
                border = BorderStroke(1.dp, AppTheme.colors.border),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Chi Tiết Đối Chiếu Từ Sai -> Đúng",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = AppTheme.colors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    result.errors.forEachIndexed { idx, err ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = AppTheme.colors.cardElevated,
                            border = BorderStroke(1.dp, AppTheme.colors.border),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = AccentCoral.copy(alpha = 0.15f),
                                    modifier = Modifier.size(20.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = "${idx + 1}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = AccentCoral,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = err.originalWord,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = AccentCoral,
                                    fontWeight = FontWeight.Bold,
                                    textDecoration = TextDecoration.LineThrough
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = AppTheme.colors.textMuted,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = err.correctedWord,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (isDarkTheme) EmeraldLight else Color(0xFF059669),
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                Text(
                                    text = err.errorType,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = AppTheme.colors.textMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PedagogicalSkillsBreakdownSection(
    result: GradeResult,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    val emeraldColor = if (isDarkTheme) EmeraldPrimary else Color(0xFF047857)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Overall competency header
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = AppTheme.colors.card),
            border = BorderStroke(1.dp, AppTheme.colors.border),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "ĐÁNH GIÁ NĂNG LỰC TIẾNG VIỆT",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = AppTheme.colors.textMuted,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = result.criteria.ratingLevel,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = emeraldColor
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isDarkTheme) Color(0xFF064E3B) else Color(0xFFDCFCE7),
                        border = BorderStroke(1.dp, emeraldColor.copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = "Hạng ${result.criteria.gradeBadge}",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Black,
                            color = emeraldColor
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 1. Chính tả & Ngữ âm (Max 4.0đ)
                SkillBarItem(
                    name = "1. Chính Tả & Ngữ Âm (Trọng số 40%)",
                    score = result.criteria.spellingScore,
                    maxScore = 4.0f,
                    detail = "Quy tắc phụ âm đầu ch/tr, s/x, d/gi; thanh hỏi - ngã",
                    barColor = if (result.criteria.spellingScore >= 3.5f) emeraldColor else AccentCoral
                )

                Spacer(modifier = Modifier.height(14.dp))

                // 2. Hình thức & Nét chữ (Max 3.0đ)
                SkillBarItem(
                    name = "2. Hình Thức & Vở Sạch Chữ Đẹp (Trọng số 30%)",
                    score = result.criteria.formatScore,
                    maxScore = 3.0f,
                    detail = "Độ nghiêng 15°, khoảng cách con chữ 'o', căn lề ô ly",
                    barColor = emeraldColor
                )

                Spacer(modifier = Modifier.height(14.dp))

                // 3. Nội dung & Diễn đạt (Max 2.0đ)
                SkillBarItem(
                    name = "3. Nội Dung & Diễn Đạt (Trọng số 20%)",
                    score = result.criteria.contentScore,
                    maxScore = 2.0f,
                    detail = "Đúng số lượng câu, ngắt câu dấu chấm/phẩy chuẩn xác",
                    barColor = AccentSky
                )

                Spacer(modifier = Modifier.height(14.dp))

                // 4. Sáng tạo & Cảm xúc (Max 1.0đ)
                SkillBarItem(
                    name = "4. Sáng Tạo & Cảm Xúc (Trọng số 10%)",
                    score = result.criteria.creativityScore,
                    maxScore = 1.0f,
                    detail = "Nét chữ mềm mại, vốn từ gợi tả phong phú",
                    barColor = AccentAmber
                )
            }
        }

        // Pedagogical advice for teachers & parents
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isDarkTheme) Color(0xFF1E293B) else Color(0xFFF8FAFC)
            ),
            border = BorderStroke(1.dp, AppTheme.colors.border),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = null,
                        tint = emeraldColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Lời Khuyên Rèn Luyện (Sư Phạm Tiểu Học)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = AppTheme.colors.textPrimary
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                val advicePoints = listOf(
                    "Dành 15 phút mỗi tối luyện đọc to và phát âm rõ cặp phụ âm đầu ch - tr.",
                    "Khi viết dấu hỏi (?) và dấu ngã (~), chú ý đặt đúng ngay trên nguyên âm chính.",
                    "Luyện viết trên vở ô ly 4 ly, giữ khoảng cách đều đặn 1 con chữ 'o' giữa hai tiếng liền kề."
                )
                advicePoints.forEach { point ->
                    Row(
                        modifier = Modifier.padding(vertical = 3.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "•",
                            color = emeraldColor,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(end = 6.dp)
                        )
                        Text(
                            text = point,
                            style = MaterialTheme.typography.bodySmall,
                            color = AppTheme.colors.textSecondary,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SkillBarItem(
    name: String,
    score: Float,
    maxScore: Float,
    detail: String,
    barColor: Color
) {
    val progress = (score / maxScore).coerceIn(0f, 1f)
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = AppTheme.colors.textPrimary
            )
            Text(
                text = "${"%.1f".format(score)}/${"%.1f".format(maxScore)}đ",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.ExtraBold,
                color = barColor
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = barColor,
            trackColor = barColor.copy(alpha = 0.2f)
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = detail,
            style = MaterialTheme.typography.labelSmall,
            color = AppTheme.colors.textMuted,
            fontSize = 10.5.sp
        )
    }
}

@Composable
private fun StudentCertificateDialog(
    result: GradeResult,
    overrideTotalScore: Float = result.criteria.totalScore,
    overrideRatingLevel: String = result.criteria.ratingLevel,
    overrideSpelling: Float = result.criteria.spellingScore,
    overrideFormat: Float = result.criteria.formatScore,
    overrideContent: Float = result.criteria.contentScore,
    overrideCreativity: Float = result.criteria.creativityScore,
    overrideComment: String = result.pedagogicalComment,
    isTeacherOverridden: Boolean = false,
    isDarkTheme: Boolean,
    onDismiss: () -> Unit,
    onCopyZaloMessage: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isDarkTheme) Color(0xFF0F172A) else Color(0xFFFFFDF8)
            ),
            border = BorderStroke(2.dp, if (isDarkTheme) Color(0xFF059669) else Color(0xFFD97706)),
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFD97706),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "PHIẾU BÁO ĐIỂM TIỂU HỌC",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isDarkTheme) Color(0xFFFDE68A) else Color(0xFF92400E),
                            letterSpacing = 1.sp
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Đóng",
                            tint = AppTheme.colors.textMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Student details banner
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isDarkTheme) Color(0xFF1E293B) else Color(0xFFF1F5F9),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = result.studentName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = AppTheme.colors.textPrimary
                        )
                        Text(
                            text = result.className,
                            style = MaterialTheme.typography.labelSmall,
                            color = AppTheme.colors.textMuted
                        )
                        Text(
                            text = "Bài thi: ${result.essayTitle}",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isDarkTheme) EmeraldLight else Color(0xFF047857),
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Teacher Circular Double Red Stamp & Score
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFEF2F2))
                        .border(3.dp, Color(0xFFDC2626), CircleShape)
                        .padding(4.dp)
                        .border(1.dp, Color(0xFFDC2626).copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (isTeacherOverridden) "★ ĐÃ DUYỆT ★" else "★ ĐIỂM SỐ ★",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFDC2626),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 8.5.sp
                        )
                        Text(
                            text = "%.1f".format(overrideTotalScore),
                            style = MaterialTheme.typography.titleLarge,
                            color = Color(0xFFDC2626),
                            fontWeight = FontWeight.Black,
                            fontSize = 25.sp
                        )
                        Text(
                            text = if (isTeacherOverridden) "GV TIỂU HỌC" else "VIHAND AI",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFDC2626),
                            fontWeight = FontWeight.Bold,
                            fontSize = 7.5.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Rating & Commendation
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isDarkTheme) Color(0xFF064E3B) else Color(0xFFDCFCE7)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = if (isDarkTheme) EmeraldLight else Color(0xFF15803D),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "$overrideRatingLevel • Hạng ${result.criteria.gradeBadge}",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isDarkTheme) EmeraldLight else Color(0xFF15803D)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Score breakdown chip summary
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text(text = "Chính tả: ${"%.1f".format(overrideSpelling)}", style = MaterialTheme.typography.labelSmall, color = AppTheme.colors.textMuted, fontSize = 10.sp)
                    Text(text = "Nét chữ: ${"%.1f".format(overrideFormat)}", style = MaterialTheme.typography.labelSmall, color = AppTheme.colors.textMuted, fontSize = 10.sp)
                    Text(text = "Nội dung: ${"%.1f".format(overrideContent)}", style = MaterialTheme.typography.labelSmall, color = AppTheme.colors.textMuted, fontSize = 10.sp)
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Teacher remarks quote
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isDarkTheme) Color(0xFF1E293B) else Color(0xFFF8FAFC),
                    border = BorderStroke(1.dp, AppTheme.colors.border),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Lời cô giáo dặn dò:",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = AppTheme.colors.textMuted
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "\"$overrideComment\"",
                            style = MaterialTheme.typography.bodySmall,
                            color = AppTheme.colors.textPrimary,
                            lineHeight = 18.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Parent Verification & QR Badge
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = AppTheme.colors.cardElevated,
                    border = BorderStroke(1.dp, AppTheme.colors.border),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = if (isDarkTheme) EmeraldPrimary else Color(0xFF047857),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Mã tra cứu: VH-${result.id}-2026",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = AppTheme.colors.textSecondary,
                                fontSize = 10.5.sp
                            )
                        }
                        Text(
                            text = "QR Báo Điểm",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (isDarkTheme) EmeraldLight else Color(0xFF047857),
                            fontSize = 10.5.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Action Buttons
                Button(
                    onClick = onCopyZaloMessage,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isDarkTheme) EmeraldPrimary else Color(0xFF059669)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = null,
                        tint = if (isDarkTheme) Color(0xFF064E3B) else Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Sao Chép Lời Nhắn Gửi Phụ Huynh",
                        color = if (isDarkTheme) Color(0xFF064E3B) else Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.5.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, AppTheme.colors.border)
                ) {
                    Text(
                        text = "Đóng",
                        color = AppTheme.colors.textPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun TeacherScoreSliderRow(
    name: String,
    maxScore: Float,
    value: Float,
    onValueChange: (Float) -> Unit,
    color: Color
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.labelSmall,
                color = AppTheme.colors.textPrimary,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "${"%.1f".format(value)} / ${"%.1f".format(maxScore)}đ",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 0f..maxScore,
            steps = ((maxScore * 10).toInt() - 1).coerceAtLeast(0),
            colors = SliderDefaults.colors(
                thumbColor = color,
                activeTrackColor = color
            ),
            modifier = Modifier.height(28.dp)
        )
    }
}

/**
 * Original photo background with YOLOv8 Bounding Boxes overlay
 */
@Composable
private fun OriginalPhotoBBoxView(
    result: GradeResult,
    selectedErrorId: String?,
    onSelectError: (com.example.data.model.ErrorBox) -> Unit,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(340.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkTheme) Color(0xFF0F172A) else Color(0xFFF8FAFC)
        ),
        border = BorderStroke(1.dp, AppTheme.colors.border)
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val canvasW = maxWidth
            val canvasH = maxHeight

            // Background canvas simulating paper texture with actual lines
            androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                // Paper background tint
                drawRect(
                    color = if (isDarkTheme) Color(0xFF1E293B) else Color(0xFFFEFDF9),
                    size = size
                )

                // Simulated notebook faint grid lines in original photo
                val stepY = h / 16f
                for (i in 1..15) {
                    val y = i * stepY
                    drawLine(
                        color = if (isDarkTheme) Color(0xFF334155).copy(alpha = 0.4f) else Color(0xFFE2E8F0),
                        start = Offset(0f, y),
                        end = Offset(w, y),
                        strokeWidth = 1f
                    )
                }
            }

            // Student handwritten text layout preview
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFF059669).copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "YOLOv8 DETECTED • ${result.errors.size} VÙNG LỖI",
                            color = if (isDarkTheme) EmeraldLight else Color(0xFF047857),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Text(
                        text = "Ảnh thực tế học sinh",
                        style = MaterialTheme.typography.labelSmall,
                        color = AppTheme.colors.textMuted,
                        fontSize = 10.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Render student essay lines in simulated handwriting font/style
                Text(
                    text = result.extractedText,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Serif,
                        letterSpacing = 0.8.sp,
                        lineHeight = 24.sp
                    ),
                    color = if (isDarkTheme) Color(0xFFE2E8F0) else Color(0xFF1E293B)
                )
            }

            // Bounding Box Overlays
            result.errors.forEach { err ->
                val isSelected = err.id == selectedErrorId
                val boxColor = when (err.errorType.lowercase()) {
                    "spelling", "chính tả" -> AccentCoral
                    "diacritic", "dấu thanh", "format" -> AccentAmber
                    "punctuation", "dấu câu" -> AccentSky
                    else -> Color(0xFF8B5CF6)
                }

                val leftDp = canvasW * err.rel_x1
                val topDp = canvasH * err.rel_y1
                val widthDp = (canvasW * err.rel_w).coerceAtLeast(36.dp)
                val heightDp = (canvasH * err.rel_h).coerceAtLeast(24.dp)

                Box(
                    modifier = Modifier
                        .offset(x = leftDp, y = topDp)
                        .size(width = widthDp, height = heightDp)
                        .border(
                            width = if (isSelected) 2.5.dp else 1.5.dp,
                            color = if (isSelected) Color(0xFFDC2626) else boxColor,
                            shape = RoundedCornerShape(4.dp)
                        )
                        .background(
                            (if (isSelected) Color(0xFFDC2626) else boxColor).copy(alpha = if (isSelected) 0.30f else 0.12f),
                            shape = RoundedCornerShape(4.dp)
                        )
                        .clickable { onSelectError(err) }
                ) {
                    // Small floating badge with error word & correction
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = if (isSelected) Color(0xFFDC2626) else boxColor,
                        modifier = Modifier.align(Alignment.TopStart).offset(y = (-14).dp)
                    ) {
                        Text(
                            text = "${err.originalWord} → ${err.correctedWord}",
                            color = Color.White,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 3.dp, vertical = 1.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricPill(
    icon: ImageVector,
    name: String,
    scoreText: String,
    subText: String,
    iconColor: Color,
    subColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = AppTheme.colors.card),
        border = androidx.compose.foundation.BorderStroke(1.dp, AppTheme.colors.border)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = name,
                    style = MaterialTheme.typography.labelSmall,
                    color = AppTheme.colors.textMuted,
                    fontSize = 11.sp
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = scoreText,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = AppTheme.colors.textPrimary
                )
                Text(
                    text = subText,
                    style = MaterialTheme.typography.labelSmall,
                    color = subColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
