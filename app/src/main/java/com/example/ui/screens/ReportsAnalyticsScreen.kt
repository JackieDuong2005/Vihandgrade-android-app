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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import com.example.ui.viewmodel.ErrorCategoryData
import com.example.ui.viewmodel.ReportsViewModel
import com.example.ui.viewmodel.UnderperformingStudentData
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Color palette for error category bars
private val ERROR_BAR_COLORS = listOf(
    Color(0xFFEF4444), Color(0xFFF59E0B), Color(0xFF3B82F6),
    Color(0xFF10B981), Color(0xFF8B5CF6)
)

@Composable
fun ReportsAnalyticsScreen(
    viewModel: ReportsViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    modifier: Modifier = Modifier,
    onNavigateToGrading: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    val isDarkTheme = AppTheme.colors.isDark
    val primaryColor = if (isDarkTheme) EmeraldPrimary else Color(0xFF059669)
    val cardBackground = AppTheme.colors.card
    val borderColor = AppTheme.colors.border

    // Collect ViewModel state
    val selectedClass by viewModel.selectedClass.collectAsState()
    val selectedTimeframe by viewModel.selectedTimeframe.collectAsState()
    val classList by viewModel.classList.collectAsState()
    val stats by viewModel.stats.collectAsState()
    val exportResult by viewModel.exportResult.collectAsState()

    var showExportSuccessMessage by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val context = androidx.compose.ui.platform.LocalContext.current
    val timeframeList = viewModel.timeframeList
    val errorStats = stats.errorCategories
    val focusStudents = stats.underperformingStudents

    // React to export result
    LaunchedEffect(exportResult) {
        exportResult?.let { res ->
            showExportSuccessMessage = true
            android.widget.Toast.makeText(context, res.message, android.widget.Toast.LENGTH_SHORT).show()
            delay(4000)
            showExportSuccessMessage = false
            viewModel.clearExportResult()
        }
    }

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
                        onClick = { viewModel.exportCsv() },
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
                                    .clickable { viewModel.setSelectedClass(item) }
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
                                    .clickable { viewModel.setSelectedTimeframe(tf) }
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
                                value = if (stats.totalGraded > 0) String.format("%.1f", stats.averageScore) else "--",
                                unit = "/10",
                                subtext = if (stats.totalGraded > 0) "Từ ${stats.totalGraded} bài" else "Chưa có dữ liệu",
                                isPositive = stats.averageScore >= 7.0f,
                                modifier = Modifier.weight(1f)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            // Metric 2: Graded Count
                            MetricBox(
                                title = "Đã chấm",
                                value = stats.totalGraded.toString(),
                                unit = " bài",
                                subtext = if (stats.totalGraded > 0) "$selectedClass" else "Chưa có bài nào",
                                isPositive = true,
                                modifier = Modifier.weight(1f)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            // Metric 3: Excellence rate
                            MetricBox(
                                title = "Đạt Xuất sắc & Tốt",
                                value = if (stats.totalGraded > 0) "${stats.excellentGoodPct}%" else "--%",
                                unit = "",
                                subtext = if (stats.totalGraded > 0) "${stats.excellentCount + stats.goodCount} / ${stats.totalGraded} bài" else "Chưa có dữ liệu",
                                isPositive = stats.excellentGoodPct >= 70,
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

                        val total = stats.totalGraded.coerceAtLeast(1)
                        val excPct = stats.excellentCount.toFloat() / total
                        val goodPct = stats.goodCount.toFloat() / total
                        val fairPct = stats.fairCount.toFloat() / total
                        val needPct = stats.needsImprovementCount.toFloat() / total

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(12.dp)
                                .clip(RoundedCornerShape(6.dp))
                        ) {
                            if (excPct > 0) Box(modifier = Modifier.weight(excPct.coerceAtLeast(0.01f)).background(Color(0xFF059669)))
                            if (goodPct > 0) Box(modifier = Modifier.weight(goodPct.coerceAtLeast(0.01f)).background(Color(0xFF3B82F6)))
                            if (fairPct > 0) Box(modifier = Modifier.weight(fairPct.coerceAtLeast(0.01f)).background(Color(0xFFF59E0B)))
                            if (needPct > 0) Box(modifier = Modifier.weight(needPct.coerceAtLeast(0.01f)).background(Color(0xFFEF4444)))
                            if (stats.totalGraded == 0) Box(modifier = Modifier.weight(1f).background(AppTheme.colors.border.copy(alpha = 0.3f)))
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Legend
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            LegendItem(color = Color(0xFF059669), label = "Xuất sắc: ${(excPct * 100).toInt()}%")
                            LegendItem(color = Color(0xFF3B82F6), label = "Tốt: ${(goodPct * 100).toInt()}%")
                            LegendItem(color = Color(0xFFF59E0B), label = "Khá: ${(fairPct * 100).toInt()}%")
                            LegendItem(color = Color(0xFFEF4444), label = "Cần rèn: ${(needPct * 100).toInt()}%")
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
                            text = if (stats.totalErrors > 0) "Tổng hợp ${stats.totalErrors} lỗi được phát hiện qua camera AI" else "Chưa có dữ liệu lỗi",
                            style = MaterialTheme.typography.bodySmall,
                            color = AppTheme.colors.textMuted
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Error bars
                        if (errorStats.isEmpty()) {
                            Text(
                                text = "Chấm thêm bài để xem phân tích lỗi thống kê",
                                style = MaterialTheme.typography.bodyMedium,
                                color = AppTheme.colors.textMuted,
                                modifier = Modifier.padding(vertical = 16.dp)
                            )
                        } else {
                            errorStats.forEachIndexed { idx, stat ->
                                ErrorStatRow(
                                    categoryName = stat.categoryName,
                                    count = stat.count,
                                    percentage = stat.percentage,
                                    barColor = ERROR_BAR_COLORS.getOrElse(idx) { Color.Gray }
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                            }
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
                                    text = if (focusStudents.isNotEmpty()) "${focusStudents.size} em" else "0 em",
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

                        if (focusStudents.isEmpty()) {
                            Text(
                                text = "Tuyệt vời! Không có em nào có điểm dưới 6.5 🎉",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF10B981),
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(vertical = 16.dp)
                            )
                        } else {
                            focusStudents.forEach { student ->
                                StudentFocusItem(student = student)
                                Spacer(modifier = Modifier.height(8.dp))
                            }
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
                                text = exportResult?.message ?: "File CSV đã lưu vào Thư mục Tải về.",
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
private fun ErrorStatRow(
    categoryName: String,
    count: Int,
    percentage: Float,
    barColor: Color
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = categoryName,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppTheme.colors.textPrimary,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "${count} lỗi (${(percentage * 100).toInt()}%)",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = barColor
            )
        }
        Spacer(modifier = Modifier.height(4.dp))

        // Progress line
        LinearProgressIndicator(
            progress = { percentage },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = barColor,
            trackColor = barColor.copy(alpha = 0.15f)
        )
    }
}

@Composable
private fun StudentFocusItem(student: UnderperformingStudentData) {
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
                    text = String.format("%.1f", student.averageScore),
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
                    text = "Điểm TB: ${String.format("%.1f", student.averageScore)} / 10",
                    fontSize = 12.sp,
                    color = Color(0xFFD97706),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
