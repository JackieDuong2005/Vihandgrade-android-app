package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.NotebookBackground
import com.example.ui.theme.AppTheme
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class ErrorCategoryStat(
    val categoryName: String,
    val count: Int,
    val percentage: Float,
    val barColor: Color,
    val examples: String
)

data class UnderperformingStudent(
    val id: String,
    val name: String,
    val className: String,
    val averageScore: Float,
    val primaryMistake: String,
    val recommendedAction: String
)

@Composable
fun ReportsAnalyticsScreen(
    modifier: Modifier = Modifier,
    onNavigateToGrading: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    val isDarkTheme = AppTheme.colors.isDark
    val primaryColor = if (isDarkTheme) EmeraldPrimary else Color(0xFF059669)
    val cardBackground = AppTheme.colors.card
    val borderColor = AppTheme.colors.border

    var selectedClass by remember { mutableStateOf("Tất cả lớp") }
    var selectedTimeframe by remember { mutableStateOf("Tuần này") }
    var showExportSuccessMessage by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val classList = listOf("Tất cả lớp", "Lớp 3A1", "Lớp 3A2", "Lớp 4B", "Lớp 5A")
    val timeframeList = listOf("Tuần này", "Tháng này", "Học kỳ 1", "Cả năm")

    val errorStats = listOf(
        ErrorCategoryStat(
            categoryName = "Phụ âm đầu (s/x, ch/tr, d/gi/r, l/n)",
            count = 32,
            percentage = 0.45f,
            barColor = Color(0xFFEF4444),
            examples = "chổ hoa -> trổ hoa, xớm mai -> sớm mai"
        ),
        ErrorCategoryStat(
            categoryName = "Vần khó (an/ang, uôn/uông, iên/iêng)",
            count = 18,
            percentage = 0.25f,
            barColor = Color(0xFFF59E0B),
            examples = "bàng tay -> bàn tay, muông loài -> muôn loài"
        ),
        ErrorCategoryStat(
            categoryName = "Dấu thanh điệu (Hỏi / Ngã)",
            count = 14,
            percentage = 0.20f,
            barColor = Color(0xFF3B82F6),
            examples = "giửa trời -> giữa trời, ngở ngàng -> ngỡ ngàng"
        ),
        ErrorCategoryStat(
            categoryName = "Viết hoa đầu dòng & quy chuẩn vở ô ly",
            count = 7,
            percentage = 0.10f,
            barColor = Color(0xFF10B981),
            examples = "chữ cái đầu dòng thơ, tên riêng địa danh"
        )
    )

    val focusStudents = listOf(
        UnderperformingStudent(
            id = "s_1",
            name = "Lê Hoàng Khôi",
            className = "Lớp 3A1",
            averageScore = 6.2f,
            primaryMistake = "Phụ âm đầu s/x, d/gi",
            recommendedAction = "Giao phiếu bài tập phân biệt s/x vào thứ 5"
        ),
        UnderperformingStudent(
            id = "s_2",
            name = "Phạm Bảo Nam",
            className = "Lớp 3A2",
            averageScore = 6.5f,
            primaryMistake = "Dấu thanh hỏi / ngã",
            recommendedAction = "Rèn phát âm từ có dấu ngã trong giờ tự học"
        ),
        UnderperformingStudent(
            id = "s_3",
            name = "Ngô Mai Phương",
            className = "Lớp 4B",
            averageScore = 6.4f,
            primaryMistake = "Vần có âm cuối n/ng",
            recommendedAction = "Đọc to đoạn văn trước khi bắt đầu viết"
        )
    )

    NotebookBackground(modifier = modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(modifier = Modifier.height(6.dp))

                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(AppTheme.colors.card)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Quay lại",
                                tint = AppTheme.colors.textPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(primaryColor.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Assessment,
                                        contentDescription = null,
                                        tint = primaryColor,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Báo Cáo & Thống Kê",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = AppTheme.colors.textPrimary
                                )
                            }
                            Text(
                                text = "Phân tích sư phạm & năng lực học sinh GDPT 2018",
                                style = MaterialTheme.typography.bodySmall,
                                color = AppTheme.colors.textMuted
                            )
                        }
                    }

                    // Export button
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                showExportSuccessMessage = true
                                delay(3000)
                                showExportSuccessMessage = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("export_report_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = "Xuất Excel",
                            modifier = Modifier.size(16.dp),
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Xuất CSV", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                    }
                }

                // Filter Bar: Class & Timeframe
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(classList) { item ->
                            val isSelected = selectedClass == item
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = if (isSelected) primaryColor else cardBackground,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) primaryColor else borderColor
                                ),
                                modifier = Modifier
                                    .clickable { selectedClass = item }
                                    .testTag("filter_class_$item")
                            ) {
                                Text(
                                    text = item,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else AppTheme.colors.textSecondary
                                )
                            }
                        }
                    }

                    // Timeframe chips
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        timeframeList.forEach { tf ->
                            val isSelected = selectedTimeframe == tf
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) primaryColor.copy(alpha = 0.15f) else Color.Transparent)
                                    .border(
                                        1.dp,
                                        if (isSelected) primaryColor else borderColor.copy(alpha = 0.5f),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable { selectedTimeframe = tf }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = tf,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) primaryColor else AppTheme.colors.textMuted
                                )
                            }
                        }
                    }
                }

                // Card 1: Metric KPI Cards
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBackground),
                    border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Tổng Quan $selectedClass • $selectedTimeframe",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AppTheme.colors.textPrimary
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Metric 1: Average Score
                            MetricBox(
                                title = "Điểm trung bình",
                                value = "8.2",
                                unit = "/10",
                                subtext = "+0.4 so với tuần trước",
                                isPositive = true,
                                modifier = Modifier.weight(1f)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            // Metric 2: Graded Count
                            MetricBox(
                                title = "Đã chấm",
                                value = "48",
                                unit = " bài",
                                subtext = "100% sĩ số lớp",
                                isPositive = true,
                                modifier = Modifier.weight(1f)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            // Metric 3: Excellence rate
                            MetricBox(
                                title = "Đạt Xuất sắc & Tốt",
                                value = "77%",
                                unit = "",
                                subtext = "37 / 48 học sinh",
                                isPositive = true,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Segmented distribution bar
                        Text(
                            text = "Phân loại kết quả học sinh:",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = AppTheme.colors.textMuted
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(12.dp)
                                .clip(RoundedCornerShape(6.dp))
                        ) {
                            Box(modifier = Modifier.weight(0.42f).background(Color(0xFF059669)))
                            Box(modifier = Modifier.weight(0.35f).background(Color(0xFF3B82F6)))
                            Box(modifier = Modifier.weight(0.18f).background(Color(0xFFF59E0B)))
                            Box(modifier = Modifier.weight(0.05f).background(Color(0xFFEF4444)))
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Legend
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            LegendItem(color = Color(0xFF059669), label = "Xuất sắc: 42%")
                            LegendItem(color = Color(0xFF3B82F6), label = "Tốt: 35%")
                            LegendItem(color = Color(0xFFF59E0B), label = "Khá: 18%")
                            LegendItem(color = Color(0xFFEF4444), label = "Cần rèn: 5%")
                        }
                    }
                }

                // Card 2: Error Distribution Chart
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBackground),
                    border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.PieChart,
                                contentDescription = null,
                                tint = primaryColor,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Phân Tích Dạng Lỗi Hay Sai Nhất",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = AppTheme.colors.textPrimary
                            )
                        }
                        Text(
                            text = "Tổng hợp 71 lỗi được phát hiện qua camera AI",
                            style = MaterialTheme.typography.bodySmall,
                            color = AppTheme.colors.textMuted
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Error bars
                        errorStats.forEach { stat ->
                            ErrorStatRow(stat = stat)
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }
                }

                // Card 3: Underperforming / Focus Students List
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBackground),
                    border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Group,
                                    contentDescription = null,
                                    tint = Color(0xFFEF4444),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Học Sinh Cần Rèn Luyện Thêm",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = AppTheme.colors.textPrimary
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFEF4444).copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = "${focusStudents.size} em",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFDC2626),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Text(
                            text = "Các em có điểm dưới 6.5 cần giáo viên hướng dẫn trọng tâm",
                            style = MaterialTheme.typography.bodySmall,
                            color = AppTheme.colors.textMuted
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        focusStudents.forEach { student ->
                            StudentFocusItem(student = student)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            // Export Success Snackbar Banner
            AnimatedVisibility(
                visible = showExportSuccessMessage,
                enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .statusBarsPadding()
                    .padding(top = 8.dp, start = 16.dp, end = 16.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF065F46),
                    shadowElevation = 8.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = EmeraldLight,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Đã xuất bảng điểm thành công!",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "File: BangDiem_ChinhTa_${selectedClass.replace(" ", "_")}.csv đã lưu vào Thư mục Tải về.",
                                color = EmeraldLight,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricBox(
    title: String,
    value: String,
    unit: String,
    subtext: String,
    isPositive: Boolean,
    modifier: Modifier = Modifier
) {
    val isDark = AppTheme.colors.isDark
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isDark) Color(0xFF1E293B) else Color(0xFFF8FAFC),
        border = androidx.compose.foundation.BorderStroke(1.dp, AppTheme.colors.border),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                text = title,
                fontSize = 11.sp,
                color = AppTheme.colors.textMuted,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = value,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = AppTheme.colors.textPrimary
                )
                Text(
                    text = unit,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AppTheme.colors.textMuted
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtext,
                fontSize = 9.sp,
                color = if (isPositive) Color(0xFF10B981) else Color(0xFFEF4444),
                maxLines = 1
            )
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            fontSize = 9.sp,
            color = AppTheme.colors.textMuted
        )
    }
}

@Composable
private fun ErrorStatRow(stat: ErrorCategoryStat) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stat.categoryName,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppTheme.colors.textPrimary
            )
            Text(
                text = "${stat.count} lỗi (${(stat.percentage * 100).toInt()}%)",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = stat.barColor
            )
        }
        Spacer(modifier = Modifier.height(4.dp))

        // Progress line
        LinearProgressIndicator(
            progress = { stat.percentage },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = stat.barColor,
            trackColor = stat.barColor.copy(alpha = 0.15f)
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = "Ví dụ sai: ${stat.examples}",
            fontSize = 11.sp,
            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
            color = AppTheme.colors.textMuted
        )
    }
}

@Composable
private fun StudentFocusItem(student: UnderperformingStudent) {
    val isDark = AppTheme.colors.isDark
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isDark) Color(0xFF1E293B) else Color(0xFFFFFBEB),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isDark) AppTheme.colors.border else Color(0xFFFDE68A)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEF4444).copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = student.averageScore.toString(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFDC2626)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = student.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppTheme.colors.textPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "• ${student.className}",
                        fontSize = 11.sp,
                        color = AppTheme.colors.textMuted
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Lỗi chính: ${student.primaryMistake}",
                    fontSize = 12.sp,
                    color = Color(0xFFD97706),
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Kế hoạch: ${student.recommendedAction}",
                    fontSize = 11.sp,
                    color = AppTheme.colors.textMuted
                )
            }
        }
    }
}
