package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DeviceHub
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ErrorBox
import com.example.data.model.GradeResult
import com.example.data.repository.SampleEssays
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
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val selectedError = result.errors.find { it.id == selectedErrorId } ?: result.errors.firstOrNull()

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
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Đã lưu",
                            color = EmeraldPrimary,
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
            // Quick Sample Switcher Chips Row
            if (onSelectSample != null) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val isSample2 = result.studentName.contains("Hà Linh")
                        val isSample1 = result.studentName.contains("Bảo Nam")

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSample2) (if (isDarkTheme) EmeraldPrimary.copy(alpha = 0.2f) else Color(0xFFD1FAE5)) else AppTheme.colors.card,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSample2) (if (isDarkTheme) EmeraldPrimary else Color(0xFF059669)) else AppTheme.colors.border
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onSelectSample(SampleEssays.sample2Good) }
                                .testTag("sample_chip_1")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Description,
                                    contentDescription = null,
                                    tint = if (isSample2) (if (isDarkTheme) EmeraldPrimary else Color(0xFF047857)) else AppTheme.colors.textMuted,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        text = "Mẫu 1: Bà ngủ",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSample2) (if (isDarkTheme) EmeraldPrimary else Color(0xFF047857)) else AppTheme.colors.textPrimary
                                    )
                                    Text(
                                        text = "9.2đ • 1 lỗi",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = AppTheme.colors.textMuted,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSample1) (if (isDarkTheme) EmeraldPrimary.copy(alpha = 0.2f) else Color(0xFFD1FAE5)) else AppTheme.colors.card,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSample1) (if (isDarkTheme) EmeraldPrimary else Color(0xFF059669)) else AppTheme.colors.border
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onSelectSample(SampleEssays.sample1Eureka) }
                                .testTag("sample_chip_2")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Description,
                                    contentDescription = null,
                                    tint = if (isSample1) (if (isDarkTheme) EmeraldPrimary else Color(0xFF047857)) else AppTheme.colors.textMuted,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        text = "Mẫu 2: Euréka",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSample1) (if (isDarkTheme) EmeraldPrimary else Color(0xFF047857)) else AppTheme.colors.textPrimary
                                    )
                                    Text(
                                        text = "7.0đ • 2 lỗi",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = AppTheme.colors.textMuted,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

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

            // Interactive Canvas
            item {
                Column {
                    HandwritingCanvas(
                        result = result,
                        selectedErrorId = selectedErrorId,
                        onSelectError = { err -> onSelectError(err.id) }
                    )

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
                            text = "Chạm vào khung đỏ hoặc nút bên dưới để xem gợi ý sửa",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isDarkTheme) EmeraldLight else Color(0xFF047857),
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Error selection chips row
                    if (result.errors.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            result.errors.forEachIndexed { idx, err ->
                                val isSelected = (selectedErrorId == err.id) || (selectedErrorId == null && idx == 0)
                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = if (isSelected) Color(0xFFD97706) else (if (isDarkTheme) Color(0xFF1E293B) else Color(0xFFFEE2E2)),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isSelected) Color(0xFFF59E0B) else (if (isDarkTheme) Color(0xFFEF4444).copy(alpha = 0.5f) else Color(0xFFFCA5A5))
                                    ),
                                    modifier = Modifier
                                        .clickable { onSelectError(err.id) }
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
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, AppTheme.colors.border)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
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
                                        color = AppTheme.colors.textPrimary
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0x336366F1),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x666366F1))
                                ) {
                                    Text(
                                        text = "ViT5 + YOLOv8",
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = AccentIndigo,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                        fontSize = 10.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Word diff row
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = AppTheme.colors.cardElevated,
                                border = androidx.compose.foundation.BorderStroke(1.dp, AppTheme.colors.border),
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
                                        imageVector = Icons.Default.ArrowForward,
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
                        }
                    }
                }
            }

            // Total Score Card (Tổng Điểm Đánh Giá - 9.2/10 - Xuất Sắc)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("total_score_card"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = AppTheme.colors.card),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AppTheme.colors.border)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "TỔNG ĐIỂM ĐÁNH GIÁ",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = AppTheme.colors.textMuted,
                                letterSpacing = 1.sp
                            )
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
                                    text = "Xếp loại: ${result.criteria.ratingLevel}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDarkTheme) EmeraldPrimary else Color(0xFF047857)
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "%.1f".format(result.criteria.totalScore),
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
                            scoreText = "${"%.1f".format(result.criteria.spellingScore)}/4.0",
                            subText = "-${"%.1f".format(4.0f - result.criteria.spellingScore)}",
                            iconColor = AccentCoral,
                            subColor = AccentCoral,
                            modifier = Modifier.weight(1f)
                        )
                        MetricPill(
                            icon = Icons.Default.Layers,
                            name = "Hình thức (3đ)",
                            scoreText = "${"%.1f".format(result.criteria.formatScore)}/3.0",
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
                            scoreText = "${"%.1f".format(result.criteria.contentScore)}/2.0",
                            subText = "Đủ bài",
                            iconColor = AccentSky,
                            subColor = metricSuccessColor,
                            modifier = Modifier.weight(1f)
                        )
                        MetricPill(
                            icon = Icons.Default.AutoAwesome,
                            name = "Sáng tạo (1đ)",
                            scoreText = "${"%.1f".format(result.criteria.creativityScore)}/1.0",
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
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "\"${result.pedagogicalComment}\"",
                            style = MaterialTheme.typography.bodyMedium,
                            color = AppTheme.colors.textPrimary,
                            lineHeight = 22.sp
                        )
                    }
                }
            }

            // Action Row: "Lưu Điểm & Báo Phụ Huynh", "Chụp Tiếp"
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = {
                            scope.launch {
                                snackbarHostState.showSnackbar("Đã đồng bộ điểm bài thi lên trạm Raspberry Pi và gửi tin nhắn phụ huynh!")
                            }
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
                Spacer(modifier = Modifier.height(24.dp))
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
