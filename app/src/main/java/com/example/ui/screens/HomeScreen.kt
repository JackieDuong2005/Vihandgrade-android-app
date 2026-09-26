package com.example.ui.screens

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.api.UserData
import com.example.data.model.GradeResult

import com.example.ui.components.NotebookBackground
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.AccentCoral
import com.example.ui.theme.AppTheme
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.viewmodel.GradingUiState

@Composable
fun HomeScreen(
    gradingState: GradingUiState,
    historyList: List<GradeResult>,
    serverUrl: String,
    onOpenScanner: () -> Unit,
    onSelectSample: (GradeResult) -> Unit = {},
    onOpenHistory: () -> Unit,
    onOpenSettings: () -> Unit,
    onSelectHistoryItem: (GradeResult) -> Unit,
    onOpenReports: () -> Unit = {},
    currentUser: UserData? = null,
    modifier: Modifier = Modifier
) {
    // Pulse animation for server online dot
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_server")
    val dotAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dotAlpha"
    )

    val isStudent = currentUser?.role == "student"

    // Dynamic metrics calculated from Room history records
    val totalGradedDisplay = historyList.size.toString()
    val studentsCountDisplay = historyList.map { it.studentName }.distinct().size.toString()
    val avgScoreDisplay = if (historyList.isNotEmpty()) {
        String.format(java.util.Locale.US, "%.1f", historyList.map { it.criteria.totalScore }.average())
    } else "0.0"

    NotebookBackground(modifier = modifier) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
            Spacer(modifier = Modifier.height(10.dp))

            // 1. Server Pill: vihandgrade.click • Trạm Pi 4 Online
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = AppTheme.colors.card,
                border = androidx.compose.foundation.BorderStroke(1.dp, AppTheme.colors.border),
                modifier = Modifier.clickable { onOpenSettings() }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(AppTheme.colors.primary.copy(alpha = dotAlpha))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.Dns,
                        contentDescription = null,
                        tint = AppTheme.colors.primary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "vihandgrade.click • Trạm Pi 4 Online",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (AppTheme.colors.isDark) EmeraldLight else Color(0xFF047857),
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 2. User Greeting Row: Dynamic based on currentUser role (Giáo viên / Học sinh)
            val greetingName = if (isStudent) {
                "Xin chào, ${currentUser?.name ?: "Học sinh"}! — ${if (!currentUser?.className.isNullOrBlank()) "Lớp ${currentUser?.className}" else "Em học sinh"}"
            } else {
                val classInfo = currentUser?.classes?.takeIf { it.isNotEmpty() }?.joinToString(", ") ?: "Lớp phụ trách"
                "Xin chào, ${currentUser?.name ?: "Thầy Long"}! — $classInfo"
            }
            val badgeText = if (isStudent) "HS" else "GV"
            val badgeGradient = if (isStudent) {
                listOf(Color(0xFF2563EB), Color(0xFF1D4ED8))
            } else {
                listOf(Color(0xFF059669), Color(0xFF047857))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isStudent) (if (AppTheme.colors.isDark) Color(0xFF1E3A8A) else Color(0xFFDBEAFE)) else (if (AppTheme.colors.isDark) Color(0xFF065F46) else Color(0xFFD1FAE5))),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isStudent) Icons.Default.Description else Icons.Default.Edit,
                                contentDescription = null,
                                tint = if (isStudent) (if (AppTheme.colors.isDark) Color(0xFF60A5FA) else Color(0xFF2563EB)) else (if (AppTheme.colors.isDark) EmeraldPrimary else Color(0xFF047857)),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ViHand Grade",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = AppTheme.colors.textPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = greetingName,
                        style = MaterialTheme.typography.bodySmall,
                        color = AppTheme.colors.textMuted
                    )
                }

                // Avatar Badge "GV" hoặc "HS"
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(badgeGradient))
                        .border(1.5.dp, if (isStudent) Color(0xFF3B82F6) else (if (AppTheme.colors.isDark) EmeraldPrimary else Color(0xFF059669)), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = badgeText,
                        color = Color.White,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }

        // 3. 4 Thẻ Thống Kê 2x2 Cân Đối (Stats Grid)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Row 1: Tổng bài đã chấm (128 bài) & Học sinh (42 em)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Card 1: Tổng bài đã chấm
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = AppTheme.colors.card),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AppTheme.colors.border)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isStudent) "Bài thi của em" else "Tổng bài đã chấm",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = AppTheme.colors.textMuted,
                                    fontSize = 11.sp
                                )
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = AppTheme.colors.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = totalGradedDisplay,
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Black,
                                    color = AppTheme.colors.textPrimary
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "bài",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = AppTheme.colors.textMuted,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                            }
                        }
                    }

                    // Card 2: Học sinh (42 em)
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = AppTheme.colors.card),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AppTheme.colors.border)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isStudent) "Bài đạt 9-10đ" else "Học sinh",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = AppTheme.colors.textMuted,
                                    fontSize = 11.sp
                                )
                                Icon(
                                    imageVector = if (isStudent) Icons.Default.Star else Icons.Default.People,
                                    contentDescription = null,
                                    tint = if (isStudent) AccentAmber else Color(0xFF38BDF8),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = if (isStudent) historyList.count { it.criteria.totalScore >= 9.0f }.toString() else studentsCountDisplay,
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Black,
                                    color = AppTheme.colors.textPrimary
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isStudent) "bài" else "em",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = AppTheme.colors.textMuted,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                            }
                        }
                    }
                }

                // Row 2: Điểm trung bình & Loại bài tập (6 dạng)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Card 3: Điểm trung bình
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = AppTheme.colors.card),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AppTheme.colors.border)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Điểm trung bình",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = AppTheme.colors.textMuted,
                                    fontSize = 11.sp
                                )
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = AccentAmber,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = avgScoreDisplay,
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Black,
                                    color = AppTheme.colors.textPrimary
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "/10",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = AppTheme.colors.textMuted,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                            }
                        }
                    }

                    // Card 4: Loại bài tập
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = AppTheme.colors.card),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AppTheme.colors.border)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isStudent) "Cần rèn thêm" else "Loại bài tập",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = AppTheme.colors.textMuted,
                                    fontSize = 11.sp
                                )
                                Icon(
                                    imageVector = Icons.Default.AccessTime,
                                    contentDescription = null,
                                    tint = AppTheme.colors.textMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = if (isStudent) historyList.count { it.criteria.totalScore < 7.0f }.toString() else "6",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Black,
                                    color = AppTheme.colors.textPrimary
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isStudent) "bài" else "dạng",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = AppTheme.colors.textMuted,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                            }
                        }
                    }
                }

                // Nút mở Báo Cáo & Phân Tích Lớp Học GDPT 2018 (Chỉ dành cho Giáo viên)
                if (!isStudent) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = AppTheme.colors.card,
                        border = androidx.compose.foundation.BorderStroke(1.dp, AppTheme.colors.border),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { onOpenReports() }
                            .testTag("home_open_reports_btn")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 11.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(EmeraldPrimary.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Assessment,
                                        contentDescription = null,
                                        tint = EmeraldPrimary,
                                        modifier = Modifier.size(17.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Báo Cáo Phân Tích & Học Sinh Cần Kèm",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = AppTheme.colors.textPrimary
                                    )
                                    Text(
                                        text = "Phát hiện lỗi s/x, hỏi/ngã theo GDPT 2018",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = AppTheme.colors.textMuted,
                                        fontSize = 10.5.sp
                                    )
                                }
                            }
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = AppTheme.colors.textMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        // 4. Hero Action Card: Chấm bài viết tay ngay
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenScanner() }
                    .testTag("hero_action_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = AppTheme.colors.card
                ),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, AppTheme.colors.primary)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (AppTheme.colors.isDark) Color(0xFF065F46) else Color(0xFFD1FAE5)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = if (AppTheme.colors.isDark) EmeraldLight else Color(0xFF065F46),
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "AI Scanner Mới",
                                color = if (AppTheme.colors.isDark) EmeraldLight else Color(0xFF065F46),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Chấm bài viết tay ngay",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = AppTheme.colors.textPrimary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Tự động căn lề ô ly, bóc tách lỗi chính tả và vẽ khung Bounding Box trong 10s.",
                        style = MaterialTheme.typography.bodySmall,
                        color = AppTheme.colors.textMuted,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onOpenScanner,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("open_scanner_btn"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (AppTheme.colors.isDark) EmeraldPrimary else Color(0xFF059669)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = null,
                            tint = if (AppTheme.colors.isDark) Color(0xFF064E3B) else Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Mở Camera Chụp Bài",
                            color = if (AppTheme.colors.isDark) Color(0xFF064E3B) else Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }


        // 5. Section: 5 bài chấm gần nhất (Xem tất cả)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "5 bài chấm gần nhất",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = AppTheme.colors.textPrimary
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onOpenHistory() }
                ) {
                    Text(
                        text = "Xem tất cả",
                        style = MaterialTheme.typography.labelSmall,
                        color = AppTheme.colors.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = AppTheme.colors.primary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // Danh sách 5 bài chấm gần nhất từ cơ sở dữ liệu thực tế
        if (historyList.isNotEmpty()) {
            items(historyList.take(5)) { record ->
                val scoreVal = record.criteria.totalScore
                val scoreColor = when {
                    scoreVal >= 9.0f -> EmeraldPrimary
                    scoreVal >= 8.0f -> EmeraldPrimary
                    scoreVal >= 6.5f -> AccentAmber
                    else -> Color(0xFFEF4444)
                }
                val label = when {
                    scoreVal >= 9.0f -> "Xuất sắc"
                    scoreVal >= 8.0f -> "Tốt"
                    scoreVal >= 6.5f -> "Khá"
                    else -> "Cần cố gắng"
                }
                val dateStr = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
                    .format(java.util.Date(record.timestamp))

                RecentPaperCard(
                    student = "${record.studentName} — Lớp ${record.className.ifBlank { "3A1" }}",
                    meta = "${record.essayTitle} • $dateStr",
                    score = String.format(java.util.Locale.US, "%.1f", scoreVal),
                    label = label,
                    scoreColor = scoreColor,
                    onClick = {
                        onSelectHistoryItem(record)
                    }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        } else {
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = AppTheme.colors.card,
                    border = androidx.compose.foundation.BorderStroke(1.dp, AppTheme.colors.border),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                            tint = AppTheme.colors.textMuted,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Chưa có bài chấm nào",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = AppTheme.colors.textPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Nhấn biểu tượng camera ở giữa để bắt đầu chấm bài.",
                            style = MaterialTheme.typography.bodySmall,
                            color = AppTheme.colors.textMuted,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(48.dp))
        }
    }
    }
}

@Composable
private fun RecentPaperCard(
    student: String,
    meta: String,
    score: String,
    label: String,
    scoreColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = AppTheme.colors.card),
        border = androidx.compose.foundation.BorderStroke(1.dp, AppTheme.colors.border)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (AppTheme.colors.isDark) Color(0xFF065F46).copy(alpha = 0.25f) else Color(0xFFD1FAE5)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        tint = if (AppTheme.colors.isDark) EmeraldPrimary else Color(0xFF047857),
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = student,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = AppTheme.colors.textPrimary
                    )
                    Text(
                        text = meta,
                        style = MaterialTheme.typography.labelSmall,
                        color = AppTheme.colors.textMuted,
                        fontSize = 11.sp
                    )
                }
            }

            val effectiveScoreColor = if (!AppTheme.colors.isDark && scoreColor == EmeraldPrimary) {
                Color(0xFF047857)
            } else {
                scoreColor
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = score,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = effectiveScoreColor
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = effectiveScoreColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

