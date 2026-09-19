package com.example.ui.screens

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.speech.tts.TextToSpeech
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.data.api.DictationPassage
import com.example.data.api.NetworkClient
import com.example.data.repository.LocalDictationPassages
import com.example.ui.components.NotebookBackground
import com.example.ui.theme.AppTheme
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URLEncoder
import java.util.Locale

// ============================================================
// DICTATION SCREEN — Kho SGK & Động cơ Edge-TTS Neural Audio
// ============================================================
@Composable
fun DictationScreen(
    serverUrl: String = NetworkClient.DEFAULT_BASE_URL,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val isDarkTheme = AppTheme.colors.isDark
    val primaryBrand = if (isDarkTheme) EmeraldPrimary else Color(0xFF059669)
    val darkEmeraldText = if (isDarkTheme) EmeraldPrimary else Color(0xFF047857)

    // ============ SGK FILTERS ============
    var selectedGrade by remember { mutableIntStateOf(3) }
    var selectedBookSet by remember { mutableStateOf("Tất cả") } // "Tất cả", "KetNoi", "CanhDieu", "ChanTroi"

    // Kho bài đọc từ SGK
    var passages by remember { mutableStateOf(LocalDictationPassages.getPassagesByGrade(3)) }
    var selectedPassage by remember {
        mutableStateOf(passages.firstOrNull() ?: LocalDictationPassages.allPassages[0])
    }

    // Cập nhật danh sách bài đọc khi chọn lớp / bộ sách
    LaunchedEffect(selectedGrade, selectedBookSet) {
        val filterBook = if (selectedBookSet == "Tất cả") null else selectedBookSet
        val localList = LocalDictationPassages.getPassagesByGradeAndBookSet(selectedGrade, filterBook)
        passages = localList
        if (selectedPassage !in localList) {
            selectedPassage = localList.firstOrNull() ?: LocalDictationPassages.allPassages[0]
        }

        // Tự động đồng bộ thêm từ server nếu có mạng
        coroutineScope.launch(Dispatchers.IO) {
            try {
                val service = NetworkClient.createService(serverUrl)
                val response = service.getDictationPassages(selectedGrade, filterBook)
                if (response.isSuccessful && response.body()?.passages != null) {
                    val serverPassages = response.body()!!.passages!!
                    if (serverPassages.isNotEmpty()) {
                        withContext(Dispatchers.Main) {
                            passages = serverPassages
                            if (selectedPassage !in serverPassages) {
                                selectedPassage = serverPassages[0]
                            }
                        }
                    }
                }
            } catch (_: Exception) {
                // Giữ local offline fallback
            }
        }
    }

    // Tách câu từ nội dung bài đọc
    val sentences = remember(selectedPassage) {
        selectedPassage.content
            .split("\n", ".")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
    }

    // ============ VOICE & AUDIO SETTINGS ============
    // Voices: "vi-VN-HoaiMyNeural" (Bắc Bộ), "vi-VN-NamMinhNeural" (Nam Bộ), "google_tts", "local_tts"
    var selectedVoice by remember { mutableStateOf("vi-VN-HoaiMyNeural") }
    var showVoiceDialog by remember { mutableStateOf(false) }
    var speed by remember { mutableFloatStateOf(0.85f) }
    var isPedagogicalMode by remember { mutableStateOf(true) } // Đọc 2 lần + dừng 10s

    // ============ PLAYBACK STATE ============
    var isPlaying by remember { mutableStateOf(false) }
    var currentSentenceIndex by remember { mutableIntStateOf(0) }
    var repeatCount by remember { mutableIntStateOf(0) } // 0 = lần 1, 1 = lần 2
    var pauseCountdown by remember { mutableIntStateOf(0) }
    var currentAudioSource by remember { mutableStateOf("Edge-TTS Neural") }
    var showPassagePicker by remember { mutableStateOf(false) }

    // Media Player cho Audio Stream
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var localTts by remember { mutableStateOf<TextToSpeech?>(null) }

    // Khởi tạo Android TTS nội bộ dự phòng
    DisposableEffect(Unit) {
        var ttsInstance: TextToSpeech? = null
        ttsInstance = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val res = ttsInstance?.setLanguage(Locale("vi", "VN"))
                if (res == TextToSpeech.LANG_MISSING_DATA || res == TextToSpeech.LANG_NOT_SUPPORTED) {
                    ttsInstance?.setLanguage(Locale.getDefault())
                }
            }
        }
        localTts = ttsInstance

        onDispose {
            ttsInstance?.stop()
            ttsInstance?.shutdown()
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    // Hàm phát âm 1 đoạn văn bản (ưu tiên Edge-TTS stream, tự động fallback TTS máy)
    fun playSentenceAudio(text: String, onComplete: () -> Unit) {
        if (selectedVoice == "local_tts") {
            currentAudioSource = "Android TTS"
            try {
                localTts?.setSpeechRate(speed)
                localTts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "dictation_${System.currentTimeMillis()}")
            } catch (_: Exception) {}
            // Ước lượng thời gian phát âm
            val estimatedDurationMs = ((text.length * 90L) / speed).toLong().coerceAtLeast(2500L)
            coroutineScope.launch {
                delay(estimatedDurationMs)
                onComplete()
            }
            return
        }

        // Gọi Edge-TTS hoặc Google TTS từ Next.js server
        coroutineScope.launch(Dispatchers.IO) {
            try {
                val rateStr = when {
                    speed <= 0.75f -> "-25%"
                    speed <= 0.85f -> "-15%"
                    else -> "0%"
                }
                val cleanBaseUrl = serverUrl.trimEnd('/')
                val encodedText = URLEncoder.encode(text, "UTF-8")
                val streamUrl = "$cleanBaseUrl/api/dictation/tts?text=$encodedText&voice=$selectedVoice&rate=$rateStr"

                withContext(Dispatchers.Main) {
                    currentAudioSource = if (selectedVoice == "google_tts") "Google TTS" else "Edge-TTS Neural"
                    mediaPlayer?.release()
                    val player = MediaPlayer().apply {
                        setAudioAttributes(
                            AudioAttributes.Builder()
                                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                                .setUsage(AudioAttributes.USAGE_MEDIA)
                                .build()
                        )
                        setDataSource(streamUrl)
                        setOnPreparedListener { it.start() }
                        setOnCompletionListener {
                            it.release()
                            mediaPlayer = null
                            onComplete()
                        }
                        setOnErrorListener { _, _, _ ->
                            // Fallback sang Android TTS nội bộ
                            currentAudioSource = "Android TTS (Dự phòng)"
                            localTts?.setSpeechRate(speed)
                            localTts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "fallback")
                            coroutineScope.launch {
                                delay(((text.length * 90L) / speed).toLong().coerceAtLeast(2500L))
                                onComplete()
                            }
                            true
                        }
                    }
                    mediaPlayer = player
                    player.prepareAsync()
                }
            } catch (_: Exception) {
                // Lỗi mạng ngay từ đầu -> fallback tức thì
                withContext(Dispatchers.Main) {
                    currentAudioSource = "Android TTS (Offline)"
                    localTts?.setSpeechRate(speed)
                    localTts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "fallback")
                    coroutineScope.launch {
                        delay(((text.length * 90L) / speed).toLong().coerceAtLeast(2500L))
                        onComplete()
                    }
                }
            }
        }
    }

    // Quản lý đếm ngược khi nghỉ viết
    LaunchedEffect(pauseCountdown) {
        if (pauseCountdown > 0) {
            delay(1000)
            pauseCountdown -= 1
        }
    }

    // Vòng lặp chính tả sư phạm
    LaunchedEffect(isPlaying, currentSentenceIndex, repeatCount) {
        if (!isPlaying) {
            mediaPlayer?.stop()
            localTts?.stop()
            return@LaunchedEffect
        }

        val textToRead = sentences.getOrNull(currentSentenceIndex)
        if (textToRead == null) {
            isPlaying = false
            currentSentenceIndex = 0
            repeatCount = 0
            return@LaunchedEffect
        }

        playSentenceAudio(textToRead) {
            if (!isPlaying) return@playSentenceAudio

            if (isPedagogicalMode) {
                if (repeatCount == 0) {
                    // Lần 1 xong -> nghỉ 3 giây rồi đọc lần 2
                    coroutineScope.launch {
                        delay(2800)
                        if (isPlaying) {
                            repeatCount = 1
                        }
                    }
                } else {
                    // Lần 2 xong -> nghỉ 10s cho học sinh nắn nót viết
                    coroutineScope.launch {
                        pauseCountdown = 10
                        while (pauseCountdown > 0 && isPlaying) {
                            delay(1000)
                        }
                        if (isPlaying) {
                            repeatCount = 0
                            if (currentSentenceIndex < sentences.size - 1) {
                                currentSentenceIndex += 1
                            } else {
                                isPlaying = false
                                currentSentenceIndex = 0
                            }
                        }
                    }
                }
            } else {
                // Chế độ liên tục (không lặp)
                coroutineScope.launch {
                    delay(1500)
                    if (isPlaying) {
                        if (currentSentenceIndex < sentences.size - 1) {
                            currentSentenceIndex += 1
                        } else {
                            isPlaying = false
                            currentSentenceIndex = 0
                        }
                    }
                }
            }
        }
    }

    // Hiệu ứng xoay đĩa than vinyl khi đang phát
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

    NotebookBackground(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // ============ HEADER: KHỐI LỚP & BỘ SÁCH SGK ============
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(primaryBrand.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = null,
                            tint = primaryBrand,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Kho Ngữ Liệu SGK",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AppTheme.colors.textPrimary
                    )
                }

                // Audio source badge
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = primaryBrand.copy(alpha = 0.12f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, primaryBrand.copy(alpha = 0.3f)),
                    modifier = Modifier.clickable { showVoiceDialog = true }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = null,
                            tint = primaryBrand,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = currentAudioSource,
                            style = MaterialTheme.typography.labelSmall,
                            color = primaryBrand,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Thanh chọn Khối Lớp 1 - 5
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                (1..5).forEach { gr ->
                    val isSel = selectedGrade == gr
                    FilterChip(
                        selected = isSel,
                        onClick = {
                            selectedGrade = gr
                            isPlaying = false
                            currentSentenceIndex = 0
                            repeatCount = 0
                        },
                        label = { Text("Lớp $gr", fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = primaryBrand,
                            selectedLabelColor = if (isDarkTheme) Color(0xFF064E3B) else Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Thanh chọn Bộ Sách SGK (Kết nối tri thức, Cánh diều, Chân trời sáng tạo)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(
                    "Tất cả" to "Tất cả sách",
                    "KetNoi" to "Kết Nối Tri Thức",
                    "CanhDieu" to "Cánh Diều",
                    "ChanTroi" to "Chân Trời Sáng Tạo"
                ).forEach { (key, label) ->
                    val isSel = selectedBookSet == key
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSel) primaryBrand.copy(alpha = 0.2f) else AppTheme.colors.card,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSel) primaryBrand else AppTheme.colors.border
                        ),
                        modifier = Modifier.clickable {
                            selectedBookSet = key
                            isPlaying = false
                            currentSentenceIndex = 0
                            repeatCount = 0
                        }
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSel) primaryBrand else AppTheme.colors.textMuted,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ============ CHỌN BÀI ĐỌC ============
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = AppTheme.colors.card),
                border = androidx.compose.foundation.BorderStroke(1.dp, AppTheme.colors.border),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showPassagePicker = true }
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = selectedPassage.unit.ifEmpty { "Bài đọc SGK" },
                                style = MaterialTheme.typography.labelSmall,
                                color = primaryBrand,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = " • Bộ sách: ${when (selectedPassage.bookSet) {
                                    "CanhDieu" -> "Cánh Diều"
                                    "ChanTroi" -> "Chân Trời"
                                    else -> "Kết Nối"
                                }}",
                                style = MaterialTheme.typography.labelSmall,
                                color = AppTheme.colors.textMuted
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = selectedPassage.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AppTheme.colors.textPrimary
                        )
                    }
                    Button(
                        onClick = { showPassagePicker = true },
                        colors = ButtonDefaults.buttonColors(containerColor = primaryBrand.copy(alpha = 0.15f)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Đổi bài", color = primaryBrand, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Dropdown Menu chọn bài đọc
            DropdownMenu(
                expanded = showPassagePicker,
                onDismissRequest = { showPassagePicker = false }
            ) {
                passages.forEach { p ->
                    DropdownMenuItem(
                        text = {
                            Column {
                                Text(p.title, fontWeight = FontWeight.Bold)
                                Text("${p.unit} • Lớp ${p.gradeLevel}", style = MaterialTheme.typography.bodySmall)
                            }
                        },
                        onClick = {
                            selectedPassage = p
                            showPassagePicker = false
                            isPlaying = false
                            currentSentenceIndex = 0
                            repeatCount = 0
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ============ HERO AUDIO DISC ============
            Box(
                modifier = Modifier
                    .size(100.dp)
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
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(AppTheme.colors.card)
                        .border(2.dp, primaryBrand, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = null,
                        tint = primaryBrand,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Soundwave Visualizer Bars
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.height(24.dp)
            ) {
                val waveHeights = if (isPlaying) listOf(20.dp, 10.dp, 24.dp, 16.dp, 8.dp, 18.dp) else listOf(6.dp, 6.dp, 6.dp, 6.dp, 6.dp, 6.dp)
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

            // Trạng thái đọc chính tả
            if (isPlaying && isPedagogicalMode) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = primaryBrand.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = if (repeatCount == 0) "🔊 Đọc lần 1: Đọc to, rõ ràng" else "🔁 Đọc lần 2: Đọc chậm dò bài",
                        color = primaryBrand,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ============ BÀI ĐỌC HIỂN THỊ VỚI HIGHLIGHT CÂU ĐANG ĐỌC ============
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("dictation_passage_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AppTheme.colors.card),
                border = androidx.compose.foundation.BorderStroke(1.dp, AppTheme.colors.border)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    sentences.forEachIndexed { index, sentence ->
                        val isCurrent = index == currentSentenceIndex
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (isCurrent && isPlaying) primaryBrand.copy(alpha = if (isDarkTheme) 0.20f else 0.14f)
                                    else if (isCurrent) primaryBrand.copy(alpha = 0.08f)
                                    else Color.Transparent
                                )
                                .clickable {
                                    currentSentenceIndex = index
                                    repeatCount = 0
                                    isPlaying = true
                                }
                                .padding(horizontal = 10.dp, vertical = 7.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (isCurrent) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(primaryBrand)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                }
                                Text(
                                    text = sentence,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isCurrent) darkEmeraldText else AppTheme.colors.textPrimary,
                                    lineHeight = 24.sp
                                )
                            }
                        }
                    }
                }
            }

            // ============ TỪ KHÓ CẦN CHÚ Ý ============
            if (!selectedPassage.difficultWords.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7).copy(alpha = if (isDarkTheme) 0.15f else 0.7f)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF59E0B).copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color(0xFFD97706),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Từ khó cần chú ý trong bài (Barem chính tả):",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isDarkTheme) Color(0xFFFBBF24) else Color(0xFF92400E)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = selectedPassage.difficultWords!!,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isDarkTheme) Color(0xFFFDE68A) else Color(0xFF78350F)
                        )
                    }
                }
            }

            // ============ THÔNG BÁO DỪNG ĐỂ HỌC SINH VIẾT ============
            if (pauseCountdown > 0) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFEF3C7),
                    modifier = Modifier.fillMaxWidth()
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
                            text = "Nghỉ $pauseCountdown giây để học sinh nắn nót viết câu vào vở...",
                            color = Color(0xFF92400E),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ============ ĐIỀU KHIỂN AUDIO (PLAY/PAUSE/REPLAY) ============
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // "Dừng 15s" button
                Button(
                    onClick = {
                        isPlaying = false
                        pauseCountdown = 15
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AppTheme.colors.card),
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

                // Nút Play / Pause tròn nổi bật
                Box(
                    modifier = Modifier
                        .size(62.dp)
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

                // "Đọc lại" button
                Button(
                    onClick = {
                        val textToRead = sentences.getOrNull(currentSentenceIndex) ?: return@Button
                        playSentenceAudio(textToRead) {}
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AppTheme.colors.card),
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

            Spacer(modifier = Modifier.height(14.dp))

            // ============ CÀI ĐẶT SƯ PHẠM & TỐC ĐỘ ============
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = AppTheme.colors.card),
                border = androidx.compose.foundation.BorderStroke(1.dp, AppTheme.colors.border),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // Chế độ chuẩn Bộ GD&ĐT Switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Quy chuẩn sư phạm (Bộ GD&ĐT)",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = AppTheme.colors.textPrimary
                            )
                            Text(
                                text = "Mỗi câu đọc 2 lần kèm khoảng nghỉ 10s cho học sinh chép",
                                style = MaterialTheme.typography.bodySmall,
                                color = AppTheme.colors.textMuted
                            )
                        }
                        Switch(
                            checked = isPedagogicalMode,
                            onCheckedChange = { isPedagogicalMode = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = primaryBrand)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Tốc độ đọc
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Tốc độ đọc:",
                            style = MaterialTheme.typography.bodySmall,
                            color = AppTheme.colors.textMuted
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf(
                                0.75f to "Chậm (0.75x)",
                                0.85f to "Chuẩn (0.85x)",
                                1.0f to "Bình thường (1.0x)"
                            ).forEach { (spd, lbl) ->
                                val isSelected = speed == spd
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) primaryBrand else AppTheme.colors.cardElevated,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, AppTheme.colors.border),
                                    modifier = Modifier.clickable { speed = spd }
                                ) {
                                    Text(
                                        text = lbl,
                                        color = if (isSelected) (if (isDarkTheme) Color(0xFF064E3B) else Color.White) else AppTheme.colors.textMuted,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Chọn giọng đọc
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showVoiceDialog = true },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.RecordVoiceOver,
                                contentDescription = null,
                                tint = primaryBrand,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Giọng đọc chính tả",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = AppTheme.colors.textPrimary
                                )
                                Text(
                                    text = when (selectedVoice) {
                                        "vi-VN-NamMinhNeural" -> "Thầy Nam Minh (Nam Bộ - Edge-TTS)"
                                        "google_tts" -> "Google TTS Tiếng Việt"
                                        "local_tts" -> "Giọng máy Android (Nội bộ)"
                                        else -> "Cô Hoài My (Bắc Bộ - Edge-TTS)"
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = primaryBrand
                                )
                            }
                        }
                        TextButton(onClick = { showVoiceDialog = true }) {
                            Text("Thay đổi", color = primaryBrand, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }

    // Modal chọn giọng đọc Edge-TTS
    if (showVoiceDialog) {
        AlertDialog(
            onDismissRequest = { showVoiceDialog = false },
            title = {
                Text(
                    text = "Chọn Giọng Đọc Chính Tả",
                    fontWeight = FontWeight.Bold,
                    color = AppTheme.colors.textPrimary
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(
                        "vi-VN-HoaiMyNeural" to "Cô Hoài My (Bắc Bộ - Edge-TTS Neural)",
                        "vi-VN-NamMinhNeural" to "Thầy Nam Minh (Nam Bộ - Edge-TTS Neural)",
                        "google_tts" to "Google Dịch TTS (Trực tiếp)",
                        "local_tts" to "Giọng máy Android (Nội bộ offline)"
                    ).forEach { (voiceKey, voiceName) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    selectedVoice = voiceKey
                                    showVoiceDialog = false
                                }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedVoice == voiceKey,
                                onClick = {
                                    selectedVoice = voiceKey
                                    showVoiceDialog = false
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = voiceName,
                                style = MaterialTheme.typography.bodyMedium,
                                color = AppTheme.colors.textPrimary
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showVoiceDialog = false }) {
                    Text("Đóng", color = primaryBrand)
                }
            }
        )
    }
}
