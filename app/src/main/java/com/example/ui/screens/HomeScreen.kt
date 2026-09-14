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
import com.example.data.model.GradeResult
import com.example.data.repository.SampleEssays
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
    onSelectSample: (GradeResult) -> Unit,
    onOpenHistory: () -> Unit,
    onOpenSettings: () -> Unit,
    onSelectHistoryItem: (GradeResult) -> Unit,
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

            // 2. User Greeting Row: ViHand Grade | Xin chào, Thầy Long! — Lớp 3A1, 3A2 | GV Badge
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
                                .background(if (AppTheme.colors.isDark) Color(0xFF065F46) else Color(0xFFD1FAE5)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                tint = if (AppTheme.colors.isDark) EmeraldPrimary else Color(0xFF047857),
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
                        text = "Xin chào, Thầy Long! — Lớp 3A1, 3A2",
                        style = MaterialTheme.typography.bodySmall,
                        color = AppTheme.colors.textMuted
                    )
                }

                // Avatar Badge "GV"
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF059669), Color(0xFF047857))
                            )
                        )
                        .border(1.5.dp, if (AppTheme.colors.isDark) EmeraldPrimary else Color(0xFF059669), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "GV",
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
                                    text = "Tổng bài đã chấm",
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
                                    text = "128",
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
                                    text = "Học sinh",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = AppTheme.colors.textMuted,
                                    fontSize = 11.sp
                                )
                                Icon(
                                    imageVector = Icons.Default.People,
                                    contentDescription = null,
                                    tint = Color(0xFF38BDF8),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = "42",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Black,
                                    color = AppTheme.colors.textPrimary
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "em",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = AppTheme.colors.textMuted,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                            }
                        }
                    }
                }

                // Row 2: Điểm trung bình (8.4 /10) & Loại bài tập (6 dạng)
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
                                    text = "8.4",
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
                                    text = "Loại bài tập",
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
                                    text = "6",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Black,
                                    color = AppTheme.colors.textPrimary
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "dạng",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = AppTheme.colors.textMuted,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                            }
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

        // Recent Papers List Matching HTML Mockup
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Item 1: Nguyễn Văn An — Lớp 3A1 (Quạt cho bà ngủ - 9.2đ)
                RecentPaperCard(
                    student = "Nguyễn Văn An — Lớp 3A1",
                    meta = "Bài thơ: Quạt cho bà ngủ • Hôm nay",
                    score = "9.2",
                    label = "Xuất sắc",
                    scoreColor = EmeraldPrimary,
                    onClick = {
                        onSelectSample(SampleEssays.sample2Good)
                    }
                )

                // Item 2: Trần Mai Hoa — Lớp 3A1 (Người mẹ - 8.5đ)
                RecentPaperCard(
                    student = "Trần Mai Hoa — Lớp 3A1",
                    meta = "Chính tả: Người mẹ • Hôm nay",
                    score = "8.5",
                    label = "Tốt",
                    scoreColor = EmeraldPrimary,
                    onClick = {
                        onSelectSample(SampleEssays.sample3Spelling)
                    }
                )

                // Item 3: Lê Hoàng Nam — Lớp 3A2 (Quê hương - 7.8đ)
                RecentPaperCard(
                    student = "Lê Hoàng Nam — Lớp 3A2",
                    meta = "Tập làm văn: Quê hương • Hôm qua",
                    score = "7.8",
                    label = "Khá",
                    scoreColor = AccentAmber,
                    onClick = {
                        onSelectSample(SampleEssays.sample1Eureka)
                    }
                )
            }
        }

        // Section: Bài thi mẫu cho Ban Giám Khảo Euréka
        item {
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "BÀI THI MẪU CHO GIÁM KHẢO EURÉKA",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = AccentAmber
                )
            }
        }

        items(SampleEssays.allSamples) { sample ->
            EurekaSampleCard(
                sample = sample,
                onSelect = { onSelectSample(sample) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
    }

    // Processing Dialog
    if (gradingState is GradingUiState.Processing) {
        Dialog(onDismissRequest = {}) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = AppTheme.colors.card),
                border = androidx.compose.foundation.BorderStroke(1.dp, AppTheme.colors.border),
                modifier = Modifier.fillMaxWidth(0.92f)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp),
                        strokeWidth = 3.5.dp,
                        color = EmeraldPrimary
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    Text(
                        text = "Trạm Raspberry Pi Đang Xử Lý",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AppTheme.colors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = gradingState.stepDescription,
                        style = MaterialTheme.typography.bodySmall,
                        color = AppTheme.colors.textMuted,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    LinearProgressIndicator(
                        progress = { gradingState.progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = EmeraldPrimary,
                        trackColor = AppTheme.colors.border
                    )
                }
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

@Composable
private fun EurekaSampleCard(
    sample: GradeResult,
    onSelect: () -> Unit
) {
    val isEureka = sample.sampleType == "eureka_demo"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .testTag("eureka_sample_${sample.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = AppTheme.colors.card
        ),
        border = androidx.compose.foundation.BorderStroke(
            if (isEureka) 1.5.dp else 1.dp,
            if (isEureka) Color(0xFFF59E0B) else AppTheme.colors.border
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    if (isEureka) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFB45309)
                        ) {
                            Text(
                                text = "KỊCH BẢN THI ĐẤU EURÉKA",
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                fontSize = 9.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                    Text(
                        text = sample.essayTitle,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = AppTheme.colors.textPrimary
                    )
                    Text(
                        text = "${sample.studentName} • ${sample.className}",
                        style = MaterialTheme.typography.labelSmall,
                        color = AppTheme.colors.textMuted
                    )
                }

                val scoreBadgeBg = when {
                    sample.criteria.totalScore >= 8.5f -> if (AppTheme.colors.isDark) EmeraldPrimary else Color(0xFF059669)
                    sample.criteria.totalScore >= 7.0f -> AccentAmber
                    else -> AccentCoral
                }
                val scoreBadgeTextColor = when {
                    sample.criteria.totalScore >= 8.5f -> if (AppTheme.colors.isDark) Color(0xFF0F172A) else Color.White
                    sample.criteria.totalScore >= 7.0f -> Color(0xFF0F172A)
                    else -> Color.White
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = scoreBadgeBg
                ) {
                    Text(
                        text = "${"%.1f".format(sample.criteria.totalScore)}đ",
                        color = scoreBadgeTextColor,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (sample.errors.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Lỗi: ",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = AccentCoral
                    )
                    sample.errors.take(2).forEach { err ->
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = if (AppTheme.colors.isDark) Color(0x33EF4444) else Color(0xFFFEE2E2),
                            modifier = Modifier.padding(end = 6.dp)
                        ) {
                            Text(
                                text = "${err.originalWord} -> ${err.correctedWord}",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (AppTheme.colors.isDark) Color(0xFFFCA5A5) else Color(0xFFB91C1C),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            } else {
                Text(
                    text = "✓ Vở sạch chữ đẹp, không có lỗi chính tả",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (AppTheme.colors.isDark) EmeraldPrimary else Color(0xFF047857),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
