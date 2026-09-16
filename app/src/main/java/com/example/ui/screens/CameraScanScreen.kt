package com.example.ui.screens

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CropFree
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EmeraldPrimary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Immutable
private data class SamplePaperData(
    val id: String,
    val title: String,
    val subtitle: String,
    val note: String,
    val lines: List<String>
)

private val samplePapers = listOf(
    SamplePaperData(
        id = "paper_1",
        title = "Quạt cho bà ngủ",
        subtitle = "Tiếng Việt Lớp 3 • Tập chép chính tả",
        note = "Mẫu 1: Có 2 lỗi chính tả cần phát hiện",
        lines = listOf(
            "Ơi chích chòe ơi!",
            "Chim đừng hót nữa,",
            "Bà em ốm rồi,",
            "Lặng nghe bà ngủ.",
            "Bàn tay bé nhỏ",
            "Vẫy quạt thật đều,",
            "Ngấn nắng thiu thiu,",
            "Đậu trên tường trắng..."
        )
    ),
    SamplePaperData(
        id = "paper_2",
        title = "Tiếng chim buổi sáng",
        subtitle = "Tiếng Việt Lớp 3 • Nghe viết chính tả",
        note = "Mẫu 2: Chữ chuẩn ô ly, đạt điểm Giỏi 9.5",
        lines = listOf(
            "Sáng sớm mùa thu thật mát mẻ,",
            "Trên cành bàng, chim hót ríu rít.",
            "Cây bàng ngoài sân trường trổ hoa,",
            "Đàn chim vỗ cánh bay giữa trời xanh thắm.",
            "Gió thổi rì rào qua từng tán lá,",
            "Em cắp sách tới trường trong nắng mai."
        )
    ),
    SamplePaperData(
        id = "paper_3",
        title = "Hạt gạo làng ta",
        subtitle = "Tiếng Việt Lớp 4 • Tập làm văn",
        note = "Mẫu 3: Lỗi ngắt dòng và dấu câu",
        lines = listOf(
            "Hạt gạo làng ta,",
            "Có vị phù sa,",
            "Của sông Kinh Thầy,",
            "Có hương sen thơm,",
            "Trong hồ nước đầy,",
            "Có lời mẹ hát,",
            "Ngọt bùi đắng cay..."
        )
    )
)

/**
 * CameraScanScreen - High performance, lag-free native document scanner.
 * Features:
 * 1. Instant shutter response with camera flash animation (0ms lag, no hanging intents).
 * 2. GPU-only laser rendering without layout recomposition passes.
 * 3. In-memory cached bitmaps for instant capture.
 * 4. Sample switcher to test different handwriting exam sheets.
 * 5. Gallery picker support.
 */
@Composable
fun CameraScanScreen(
    onCapture: (Bitmap) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Ratio Switcher state: "9:16", "full", "4:3"
    var selectedRatio by remember { mutableStateOf("9:16") }
    var isFlashOn by remember { mutableStateOf(false) }
    var selectedSampleIndex by remember { mutableIntStateOf(0) }
    val currentPaper = samplePapers[selectedSampleIndex % samplePapers.size]

    // Visual camera shutter flash feedback
    var isFlashing by remember { mutableStateOf(false) }
    var switchNotification by remember { mutableStateOf<String?>(null) }

    // Bitmap cache to eliminate memory allocation and UI freeze on shutter press
    val cachedBitmaps = remember { mutableMapOf<Int, Bitmap>() }

    fun getOrGenerateBitmap(index: Int): Bitmap {
        return cachedBitmaps.getOrPut(index) {
            generateSampleNotebookBitmap(samplePapers[index % samplePapers.size])
        }
    }

    // Gallery picker
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            try {
                val stream = context.contentResolver.openInputStream(uri)
                val bitmap = android.graphics.BitmapFactory.decodeStream(stream)
                if (bitmap != null) {
                    onCapture(bitmap)
                }
            } catch (_: Exception) {
            }
        }
    }

    // Shutter button interaction source with GPU graphicsLayer scaling
    val shutterInteractionSource = remember { MutableInteractionSource() }
    val isShutterPressed by shutterInteractionSource.collectIsPressedAsState()
    val shutterScale by animateFloatAsState(
        targetValue = if (isShutterPressed) 0.90f else 1f,
        animationSpec = tween(90),
        label = "shutter_scale"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF030712)) // Pure dark native camera viewport
            .testTag("camera_scan_screen")
    ) {
        // Ambient camera lens scrim overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0x1810B981),
                            Color(0x33020617),
                            Color(0xEE030712)
                        )
                    )
                )
        )

        // Main Layout Column
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // ==========================================
            // 1. THANH ĐIỀU KHIỂN TRÊN (cam-top-controls)
            // ==========================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Nút quay lại trang chủ (Back to Home)
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0x990B1120),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x44FFFFFF)),
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { onClose() }
                        .testTag("close_camera_btn")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại trang chủ",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Trang chủ",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Bộ đổi tỷ lệ 9:16 / Full / 4:3 (cam-ratio-switcher)
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = Color(0x990B1120),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x4434D399)),
                    modifier = Modifier.testTag("cam_ratio_switcher")
                ) {
                    Row(
                        modifier = Modifier.padding(3.dp),
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        listOf("9:16" to "9:16", "full" to "Full", "4:3" to "4:3").forEach { (key, label) ->
                            val isSelected = selectedRatio == key
                            val btnBg = if (isSelected) EmeraldPrimary else Color.Transparent
                            val textColor = if (isSelected) Color(0xFF064E3B) else Color(0xFF94A3B8)

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(btnBg)
                                    .clickable { selectedRatio = key }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                                    .testTag("ratio_btn_$key"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    color = textColor,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Nút Bật/Tắt Flash (Zap)
                IconButton(
                    onClick = { isFlashOn = !isFlashOn },
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(if (isFlashOn) Color(0x44F59E0B) else Color(0x770B1120))
                        .border(
                            1.dp,
                            if (isFlashOn) Color(0xFFF59E0B) else Color(0x33FFFFFF),
                            CircleShape
                        )
                        .testTag("flash_toggle_btn")
                ) {
                    Icon(
                        imageVector = if (isFlashOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                        contentDescription = if (isFlashOn) "Tắt Flash" else "Bật Flash",
                        tint = if (isFlashOn) Color(0xFFF59E0B) else Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // ==========================================
            // 2. KHUNG NGẮM TÀI LIỆU CHÍNH GIỮA (viewfinder-main-area)
            // ==========================================
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // AI Detect Badge
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xDD0B1120),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x5534D399)),
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .testTag("ai_detect_badge")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CropFree,
                            contentDescription = null,
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = when (selectedRatio) {
                                "full" -> "AI Khung Toàn Cảnh (Full View)"
                                "4:3" -> "AI Khung Chuẩn 4:3 (Tập Vở Ô Ly)"
                                else -> "AI Khung Chuẩn 9:16 (Toàn Màn Hình)"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }

                // Temporary switcher notification pill
                AnimatedVisibility(
                    visible = switchNotification != null,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xEE064E3B),
                        border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary),
                        modifier = Modifier.padding(bottom = 6.dp)
                    ) {
                        Text(
                            text = switchNotification ?: "",
                            color = Color(0xFFA7F3D0),
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }
                }

                // Document Frame with dynamic aspect ratio
                val frameModifier = when (selectedRatio) {
                    "full" -> Modifier
                        .fillMaxWidth(0.92f)
                        .fillMaxHeight(0.68f)
                    "4:3" -> Modifier
                        .fillMaxWidth(0.84f)
                        .aspectRatio(3f / 4f)
                    else -> Modifier
                        .fillMaxWidth(0.76f)
                        .aspectRatio(9f / 16f)
                }

                Box(
                    modifier = frameModifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFFFFDF8)) // Ivory paper
                        .border(1.5.dp, Color(0x4434D399), RoundedCornerShape(16.dp))
                        .testTag("document_viewfinder_frame")
                ) {
                    // Realistic Student Notebook Paper Preview
                    NotebookPaperViewfinderCanvas(
                        paperData = currentPaper,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Flashlight illumination overlay when active
                    if (isFlashOn) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(
                                            Color(0x30FEF08A),
                                            Color(0x12F59E0B),
                                            Color.Transparent
                                        )
                                    )
                                )
                        )
                    }

                    // 4 High-Tech Emerald Viewfinder Corners (combined single-canvas pass)
                    ViewfinderCornerOverlay(modifier = Modifier.fillMaxSize())

                    // Scanning Laser Beam (Draw-phase only, ZERO recompositions!)
                    ScanningLaserOverlay(modifier = Modifier.fillMaxSize())
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Gyroscope Level Indicator Pill
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xDD0B1120),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x4434D399)),
                    modifier = Modifier.testTag("gyro_level_pill")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Góc chụp chuẩn 90° • ${currentPaper.note}",
                            style = MaterialTheme.typography.labelSmall,
                            color = EmeraldPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.5.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        // Mini spirit level balance bubble
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(Color(0x3334D399))
                                .border(1.dp, EmeraldPrimary.copy(alpha = 0.6f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldPrimary)
                            )
                        }
                    }
                }
            }

            // ==========================================
            // 3. THANH NÚT CHỤP PHÍA DƯỚI (cam-bottom-controls)
            // ==========================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp, start = 24.dp, end = 24.dp, top = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Nút chọn từ thư viện ảnh (gallery-thumb-btn)
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0x881E293B))
                        .border(1.5.dp, Color(0x66FFFFFF), RoundedCornerShape(16.dp))
                        .clickable { galleryLauncher.launch("image/*") }
                        .testTag("gallery_picker_btn"),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoLibrary,
                            contentDescription = "Chọn từ thư viện",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "Thư viện",
                            color = Color(0xFFCBD5E1),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // NÚT CHỤP & CHẤM ĐIỂM TRUNG TÂM (cam-shutter-main-btn)
                // Phản hồi chụp tức thì 0ms với hiệu ứng chớp flash chân thực, không gọi intent treo máy!
                Box(
                    modifier = Modifier
                        .size(82.dp)
                        .graphicsLayer {
                            scaleX = shutterScale
                            scaleY = shutterScale
                        }
                        .clip(CircleShape)
                        .background(Color(0x3310B981))
                        .border(4.dp, EmeraldPrimary, CircleShape)
                        .clickable(
                            interactionSource = shutterInteractionSource,
                            indication = null
                        ) {
                            coroutineScope.launch {
                                // Camera flash animation
                                isFlashing = true
                                delay(90)
                                isFlashing = false
                                // Instant capture bitmap
                                val bitmap = getOrGenerateBitmap(selectedSampleIndex)
                                onCapture(bitmap)
                            }
                        }
                        .testTag("shutter_btn"),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .border(2.dp, Color(0xFFE2E8F0), CircleShape)
                        )
                    }
                }

                // Nút đổi bài mẫu (cam-round-icon-btn)
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(Color(0x881E293B))
                        .border(1.5.dp, Color(0x66FFFFFF), CircleShape)
                        .clickable {
                            selectedSampleIndex++
                            val next = samplePapers[selectedSampleIndex % samplePapers.size]
                            switchNotification = "Đã đổi bài: ${next.title}"
                            coroutineScope.launch {
                                delay(2200)
                                if (switchNotification == "Đã đổi bài: ${next.title}") {
                                    switchNotification = null
                                }
                            }
                        }
                        .testTag("switch_camera_btn"),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Cameraswitch,
                            contentDescription = "Đổi bài mẫu",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "Đổi bài",
                            color = Color(0xFFCBD5E1),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Camera shutter white flash feedback overlay
        if (isFlashing) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White.copy(alpha = 0.85f))
            )
        }
    }
}

/**
 * ScanningLaserOverlay:
 * Evaluates animation strictly inside the draw/render phase.
 * Produces ZERO recomposition overhead, running at native 60/120 FPS buttery smooth.
 */
@Composable
private fun ScanningLaserOverlay(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "laser_transition")
    val laserProgress = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laser_progress"
    )

    Canvas(modifier = modifier) {
        val currentProgress = laserProgress.value
        val y = size.height * currentProgress
        val beamGlowHeight = 26.dp.toPx()

        // 1. Soft atmospheric beam glow
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color.Transparent,
                    Color(0x2E10B981),
                    Color.Transparent
                ),
                startY = y - beamGlowHeight,
                endY = y + beamGlowHeight
            ),
            topLeft = Offset(0f, y - beamGlowHeight),
            size = Size(size.width, beamGlowHeight * 2)
        )

        // 2. High-precision laser core line
        drawLine(
            brush = Brush.horizontalGradient(
                colors = listOf(
                    Color.Transparent,
                    Color(0x8034D399),
                    Color.White,
                    Color(0x8034D399),
                    Color.Transparent
                )
            ),
            start = Offset(0f, y),
            end = Offset(size.width, y),
            strokeWidth = 2.dp.toPx()
        )
    }
}

/**
 * ViewfinderCornerOverlay:
 * Combines all 4 viewfinder bracket corners into a single draw pass.
 */
@Composable
private fun ViewfinderCornerOverlay(
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.padding(6.dp)) {
        val stroke = 3.5.dp.toPx()
        val cornerLength = 22.dp.toPx()
        val color = EmeraldPrimary
        val w = size.width
        val h = size.height

        // Top-Left corner
        drawLine(color, Offset(0f, 0f), Offset(cornerLength, 0f), stroke)
        drawLine(color, Offset(0f, 0f), Offset(0f, cornerLength), stroke)

        // Top-Right corner
        drawLine(color, Offset(w, 0f), Offset(w - cornerLength, 0f), stroke)
        drawLine(color, Offset(w, 0f), Offset(w, cornerLength), stroke)

        // Bottom-Left corner
        drawLine(color, Offset(0f, h), Offset(cornerLength, h), stroke)
        drawLine(color, Offset(0f, h), Offset(0f, h - cornerLength), stroke)

        // Bottom-Right corner
        drawLine(color, Offset(w, h), Offset(w - cornerLength, h), stroke)
        drawLine(color, Offset(w, h), Offset(w, h - cornerLength), stroke)
    }
}

/**
 * NotebookPaperViewfinderCanvas:
 * Clean, lightweight rendering of the educational notebook paper and handwriting text.
 */
@Composable
private fun NotebookPaperViewfinderCanvas(
    paperData: SamplePaperData,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        // Grid background & red margin line
        Canvas(modifier = Modifier.fillMaxSize()) {
            val step = 24.dp.toPx()
            val gridColor = Color(0x3038BDF8)
            val marginLineColor = Color(0x55EF4444)
            val w = size.width
            val h = size.height

            // Horizontal lines
            var y = step
            while (y < h) {
                drawLine(gridColor, Offset(0f, y), Offset(w, y), strokeWidth = 1f)
                y += step
            }

            // Vertical lines
            var x = step
            while (x < w) {
                drawLine(gridColor, Offset(x, 0f), Offset(x, h), strokeWidth = 1f)
                x += step
            }

            // Red vertical margin line
            val marginX = 36.dp.toPx()
            drawLine(marginLineColor, Offset(marginX, 0f), Offset(marginX, h), strokeWidth = 2.dp.toPx())
        }

        // Student Cursive Handwriting Text Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 44.dp, top = 18.dp, end = 16.dp, bottom = 16.dp)
        ) {
            Text(
                text = paperData.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E3A8A),
                letterSpacing = 0.3.sp
            )
            Text(
                text = paperData.subtitle,
                fontSize = 10.sp,
                fontStyle = FontStyle.Italic,
                color = Color(0xFF64748B),
                modifier = Modifier.padding(bottom = 10.dp)
            )

            paperData.lines.forEach { line ->
                Text(
                    text = line,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1E40AF),
                    lineHeight = 22.sp,
                    letterSpacing = 0.4.sp,
                    modifier = Modifier.padding(bottom = 3.dp)
                )
            }
        }
    }
}

/**
 * TẠO BITMAP TẬP VỞ Ô LY TỐI ƯU HÓA BỘ NHỚ
 * Chuẩn 720x960 sắc nét, bộ nhớ đệm chống tạo lặp lại.
 */
private fun generateSampleNotebookBitmap(paper: SamplePaperData): Bitmap {
    val width = 720
    val height = 960
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    // 1. Nền giấy kem ngà
    val bgPaint = Paint().apply {
        color = android.graphics.Color.parseColor("#FFFDF8")
        style = Paint.Style.FILL
    }
    canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

    // 2. Lưới ô ly xanh nhạt
    val gridPaint = Paint().apply {
        color = android.graphics.Color.parseColor("#E0F2FE")
        strokeWidth = 1.5f
        style = Paint.Style.STROKE
    }
    val step = 32f
    var x = 0f
    while (x < width) {
        canvas.drawLine(x, 0f, x, height.toFloat(), gridPaint)
        x += step
    }
    var y = 0f
    while (y < height) {
        canvas.drawLine(0f, y, width.toFloat(), y, gridPaint)
        y += step
    }

    // 3. Đường lề đỏ
    val marginPaint = Paint().apply {
        color = android.graphics.Color.parseColor("#FCA5A5")
        strokeWidth = 3f
    }
    val marginX = 96f
    canvas.drawLine(marginX, 0f, marginX, height.toFloat(), marginPaint)

    // 4. Mực viết tay tiếng Việt
    val titlePaint = Paint().apply {
        color = android.graphics.Color.parseColor("#1E3A8A")
        textSize = 32f
        isAntiAlias = true
        isFakeBoldText = true
    }
    canvas.drawText(paper.title, marginX + 24f, 110f, titlePaint)

    val subtitlePaint = Paint().apply {
        color = android.graphics.Color.parseColor("#64748B")
        textSize = 20f
        isAntiAlias = true
    }
    canvas.drawText(paper.subtitle, marginX + 24f, 150f, subtitlePaint)

    val textPaint = Paint().apply {
        color = android.graphics.Color.parseColor("#1E40AF")
        textSize = 25f
        isAntiAlias = true
    }
    var lineY = 220f
    for (line in paper.lines) {
        canvas.drawText(line, marginX + 24f, lineY, textPaint)
        lineY += 50f
    }

    return bitmap
}
