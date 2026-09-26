package com.example.ui.screens

import android.speech.tts.TextToSpeech
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.api.UserData
import com.example.data.model.GradeResult
import com.example.ui.theme.AppTheme
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.FontFamilyTieuHoc
import com.example.ui.theme.ScoreHoanThanh
import com.example.ui.theme.ScoreTot
import com.example.ui.theme.ScoreXuatSac
import java.util.Locale

/**
 * Góc học tập & Sổ tay rèn chữ dành cho Học sinh & Phụ huynh
 * Đồng bộ với trang /student trên Web ViHand Grade
 */
@Composable
fun StudentHomeScreen(
    currentUser: UserData?,
    recentRecords: List<GradeResult>,
    onOpenHistory: () -> Unit,
    onSelectRecord: (GradeResult) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val studentName = currentUser?.name ?: "Bé Yêu"
    val className = currentUser?.className ?: "Lớp 3A"
    val latestRecord = recentRecords.firstOrNull()

    // Android TTS for pronouncing difficult words
    val context = LocalContext.current
    val tts = remember {
        var engine: TextToSpeech? = null
        engine = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                engine?.language = Locale("vi", "VN")
            }
        }
        engine
    }

    DisposableEffect(Unit) {
        onDispose {
            tts.stop()
            tts.shutdown()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AppTheme.colors.background)
            .statusBarsPadding()
    ) {
        // Top Welcome Header
        Surface(
            color = AppTheme.colors.surface,
            border = BorderStroke(1.dp, AppTheme.colors.border),
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(EmeraldPrimary, Color(0xFF0D9488))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = studentName.take(1).uppercase(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "Góc học tập của $studentName",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = AppTheme.colors.textPrimary
                        )
                        Text(
                            text = "$className • Trường Tiểu học Chuẩn Quốc Gia",
                            style = MaterialTheme.typography.bodySmall,
                            color = AppTheme.colors.textMuted
                        )
                    }
                }

                // Star badge
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFFFEF3C7),
                    border = BorderStroke(1.dp, Color(0xFFFDE68A))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFD97706),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${recentRecords.size} bài",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFB45309)
                        )
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Card 1: Latest Graded Result Hero Card
            if (latestRecord != null) {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = AppTheme.colors.surface),
                    border = BorderStroke(1.dp, AppTheme.colors.border),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.EmojiEvents,
                                    contentDescription = null,
                                    tint = Color(0xFFEAB308),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Bài làm mới nhất",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = AppTheme.colors.textMuted
                                )
                            }

                            // Score Badge
                            val scoreColor = when {
                                latestRecord.criteria.totalScore >= 9.0f -> ScoreXuatSac
                                latestRecord.criteria.totalScore >= 7.0f -> ScoreTot
                                else -> ScoreHoanThanh
                            }
                            val ratingText = when {
                                latestRecord.criteria.totalScore >= 9.0f -> "Xuất sắc"
                                latestRecord.criteria.totalScore >= 7.0f -> "Hoàn thành tốt"
                                else -> "Hoàn thành"
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = scoreColor.copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = "${String.format("%.1f", latestRecord.criteria.totalScore)} điểm • $ratingText",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = scoreColor,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = latestRecord.essayTitle,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AppTheme.colors.textPrimary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Teacher pedagogical comment
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = AppTheme.colors.background,
                            border = BorderStroke(1.dp, AppTheme.colors.border),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "💬 Lời dặn dò của cô giáo:",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldPrimary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = latestRecord.pedagogicalComment.ifBlank { "Con đã rất cố gắng, nỗ lực luyện viết chữ sạch đẹp hơn nữa nhé!" },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = AppTheme.colors.textPrimary,
                                    lineHeight = 18.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = { onSelectRecord(latestRecord) },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Xem chi tiết bài sửa & lỗi chữ viết",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = Color.White
                            )
                        }
                    }
                }
            }

            // Card 2: Difficult Words Notebook (Luyện phát âm & chép lại từ khó)
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = AppTheme.colors.surface),
                border = BorderStroke(1.dp, AppTheme.colors.border),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.MenuBook,
                            contentDescription = null,
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Sổ tay từ khó con cần luyện thêm",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = AppTheme.colors.textPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Bấm vào từng từ để nghe cô đọc mẫu và luyện viết lại vào vở ô ly con nhé:",
                        style = MaterialTheme.typography.bodySmall,
                        color = AppTheme.colors.textMuted
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    val wordsList = listOf(
                        Pair("ru bé ngủ say", "Chú ý r/d và dấu thanh"),
                        Pair("thay cho gió trời", "Chú ý âm đầu gi/d"),
                        Pair("ngọt ngào", "Chú ý vần o-a-t"),
                        Pair("chăm chỉ", "Chú ý âm ch/tr"),
                        Pair("xinh xắn", "Chú ý âm s/x")
                    )

                    wordsList.forEach { (word, tip) ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = AppTheme.colors.background,
                            border = BorderStroke(1.dp, AppTheme.colors.border),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    tts.speak(word, TextToSpeech.QUEUE_FLUSH, null, "student_word")
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = word,
                                        fontFamily = FontFamilyTieuHoc,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 17.sp,
                                        color = AppTheme.colors.textPrimary
                                    )
                                    Text(
                                        text = tip,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = AppTheme.colors.textMuted
                                    )
                                }

                                Surface(
                                    shape = CircleShape,
                                    color = EmeraldPrimary.copy(alpha = 0.15f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.VolumeUp,
                                        contentDescription = "Nghe đọc",
                                        tint = EmeraldPrimary,
                                        modifier = Modifier
                                            .padding(6.dp)
                                            .size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Card 3: Button to View Full History
            Button(
                onClick = onOpenHistory,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppTheme.colors.surface,
                    contentColor = AppTheme.colors.textPrimary
                ),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, AppTheme.colors.border),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = null,
                    tint = EmeraldPrimary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Xem Sổ điểm & Toàn bộ bài làm của con",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
