package com.example.ui.screens

import android.speech.tts.TextToSpeech
import androidx.compose.animation.core.LinearEasing
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.NotebookBackground
import com.example.ui.theme.AppTheme
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import kotlinx.coroutines.delay
import java.util.Locale

@Composable
fun DictationScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isDarkTheme = AppTheme.colors.isDark
    val primaryBrand = if (isDarkTheme) EmeraldPrimary else Color(0xFF059669)
    val darkEmeraldText = if (isDarkTheme) EmeraldPrimary else Color(0xFF047857)

    var isPlaying by remember { mutableStateOf(false) }
    var currentSentenceIndex by remember { mutableIntStateOf(0) }
    var pauseCountdown by remember { mutableIntStateOf(0) }
    var speed by remember { mutableFloatStateOf(0.85f) }

    val sentences = remember {
        listOf(
            "Ơi chích chòe ơi! Chim đừng hót nữa,",
            "Bà em ốm rồi, Lặng nghe bà ngủ.",
            "Bàn tay bé nhỏ, Vẫy quạt thật đều,",
            "Ngấn nắng thiu thiu, Đậu trên tường trắng.",
            "Căn nhà đã vắng, Cốc chén nằm im,",
            "Đôi mắt lim dim, Ngủ ngon bà nhé!"
        )
    }

    // TTS engine initialization
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    DisposableEffect(Unit) {
        var textToSpeech: TextToSpeech? = null
        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val viLocale = Locale("vi", "VN")
                val res = textToSpeech?.setLanguage(viLocale)
                if (res == TextToSpeech.LANG_MISSING_DATA || res == TextToSpeech.LANG_NOT_SUPPORTED) {
                    textToSpeech?.setLanguage(Locale.getDefault())
                }
            }
        }
        tts = textToSpeech
        onDispose {
            textToSpeech?.stop()
            textToSpeech?.shutdown()
        }
    }

    // Countdown timer effect for 15s pause
    LaunchedEffect(pauseCountdown) {
        if (pauseCountdown > 0) {
            delay(1000)
            pauseCountdown -= 1
        }
    }

    // Auto progression effect when playing
    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            val textToRead = sentences.getOrNull(currentSentenceIndex) ?: break
            try {
                tts?.setSpeechRate(speed)
                tts?.speak(textToRead, TextToSpeech.QUEUE_FLUSH, null, "dictation_${currentSentenceIndex}")
            } catch (_: Exception) {}

            // Wait based on sentence length + speed
            delay(3800)
            if (currentSentenceIndex < sentences.size - 1) {
                currentSentenceIndex += 1
            } else {
                isPlaying = false
                currentSentenceIndex = 0
            }
        }
    }

    // Disc rotation animation
    val infiniteTransition = rememberInfiniteTransition(label = "disc")
    val discRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "discRotation"
    )

    NotebookBackground(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Hero Audio Disc Section
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                if (isDarkTheme) Color(0xFF065F46) else Color(0xFFA7F3D0),
                                AppTheme.colors.cardElevated
                            )
                        )
                    )
                    .border(3.dp, primaryBrand.copy(alpha = 0.6f), CircleShape)
                    .rotate(if (isPlaying) discRotation else 0f),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(AppTheme.colors.card)
                        .border(2.dp, primaryBrand, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = null,
                        tint = primaryBrand,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Quạt Cho Bà Ngủ",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                color = AppTheme.colors.textPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Tiếng Việt Lớp 3 • Tốc độ: ${speed}x (Chuẩn -15%)",
                style = MaterialTheme.typography.bodySmall,
                color = AppTheme.colors.textMuted
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Soundwave Visualizer Bars
            Row(
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.height(28.dp)
            ) {
                val waveHeights = if (isPlaying) listOf(24.dp, 12.dp, 28.dp, 18.dp, 10.dp, 22.dp) else listOf(8.dp, 8.dp, 8.dp, 8.dp, 8.dp, 8.dp)
                waveHeights.forEach { h ->
                    Box(
                        modifier = Modifier
                            .width(4.dp)
                            .height(h)
                            .clip(RoundedCornerShape(2.dp))
                            .background(primaryBrand)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Passage Text Box with active sentence highlight
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("dictation_passage_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AppTheme.colors.card),
                border = androidx.compose.foundation.BorderStroke(1.dp, AppTheme.colors.border)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    sentences.forEachIndexed { index, sentence ->
                        val isCurrent = index == currentSentenceIndex
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isCurrent) primaryBrand.copy(alpha = if (isDarkTheme) 0.15f else 0.12f) else Color.Transparent)
                                .clickable {
                                    currentSentenceIndex = index
                                    isPlaying = true
                                }
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = sentence,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                                color = if (isCurrent) darkEmeraldText else AppTheme.colors.textPrimary,
                                lineHeight = 24.sp
                            )
                        }
                        if (index == 1 || index == 3) {
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Countdown alert badge if pausing
            if (pauseCountdown > 0) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFEF3C7),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.HourglassBottom,
                            contentDescription = null,
                            tint = Color(0xFFD97706),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Tạm dừng $pauseCountdown giây để học sinh nắn nót viết vào vở ô ly...",
                            color = Color(0xFF92400E),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Audio Controls Row (Matching Mockup)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // "Dừng 15s để viết" button
                Button(
                    onClick = {
                        isPlaying = false
                        pauseCountdown = 15
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppTheme.colors.card
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AppTheme.colors.border),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("dictation_pause_15s_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.HourglassBottom,
                        contentDescription = null,
                        tint = darkEmeraldText,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Dừng 15s", color = AppTheme.colors.textPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Play/Pause Main Button
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(primaryBrand)
                        .clickable { isPlaying = !isPlaying }
                        .testTag("play_dictation_btn"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Tạm dừng" else "Phát",
                        tint = if (isDarkTheme) Color(0xFF064E3B) else Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                // "Đọc lại câu" button
                Button(
                    onClick = {
                        val textToRead = sentences.getOrNull(currentSentenceIndex) ?: return@Button
                        try {
                            tts?.setSpeechRate(speed)
                            tts?.speak(textToRead, TextToSpeech.QUEUE_FLUSH, null, "repeat")
                        } catch (_: Exception) {}
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppTheme.colors.card
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AppTheme.colors.border),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("dictation_repeat_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Replay,
                        contentDescription = null,
                        tint = darkEmeraldText,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Đọc lại", color = AppTheme.colors.textPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Speed selector row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tốc độ đọc chính tả:",
                    style = MaterialTheme.typography.bodySmall,
                    color = AppTheme.colors.textMuted
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(0.75f, 0.85f, 1.0f).forEach { spd ->
                        val isSelected = speed == spd
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) primaryBrand else AppTheme.colors.card,
                            border = androidx.compose.foundation.BorderStroke(1.dp, AppTheme.colors.border),
                            modifier = Modifier.clickable { speed = spd }
                        ) {
                            Text(
                                text = "${spd}x",
                                color = if (isSelected) (if (isDarkTheme) Color(0xFF064E3B) else Color.White) else AppTheme.colors.textMuted,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
