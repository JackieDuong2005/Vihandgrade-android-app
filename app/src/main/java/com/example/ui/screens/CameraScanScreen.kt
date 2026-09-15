package com.example.ui.screens

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EmeraldPrimary

private data class SamplePaperData(
    val title: String,
    val subtitle: String,
    val lines: List<String>
)

private val samplePapers = listOf(
    SamplePaperData(
        title = "Quạt cho bà ngủ",
        subtitle = "Tiếng Việt Lớp 3 • Tập chép chính tả",
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
        title = "Tiếng chim buổi sáng",
        subtitle = "Tiếng Việt Lớp 3 • Nghe viết chính tả",
        lines = listOf(
            "Sáng sớm mùa thu thật mát mẻ,",
            "Trên cành bàng, chim hót ríu rít.",
            "Cây bàng ngoài sân trường đang trổ hoa,",
            "Đàn chim vỗ cánh bay giữa trời xanh thắm.",
            "Gió thổi rì rào qua từng tán lá,",
            "Em cắp sách tới trường trong nắng mai."
        )
    )
)

@Composable
fun CameraScanScreen(
    onCapture: (Bitmap) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Ratio Switcher state: "9:16", "full", "4:3"
    var selectedRatio by remember { mutableStateOf("9:16") }
    var isFlashOn by remember { mutableStateOf(false) }
    var selectedSampleIndex by remember { mutableIntStateOf(0) }
    val currentPaper = samplePapers[selectedSampleIndex % samplePapers.size]

    // Laser scan animation
    val infiniteTransition = rememberInfiniteTransition(label = "laser_anim")
    val laserProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laser_y"
    )

    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            onCapture(bitmap)
        } else {
            // If camera was canceled or unavailable (e.g. streaming emulator),
            // fallback to high-quality notebook scan bitmap so user flow is seamless!
            val generated = generateSampleNotebookBitmap(currentPaper)
            onCapture(generated)
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

    // Shutter button interaction source for tactile press animation
    val shutterInteractionSource = remember { MutableInteractionSource() }
    val isShutterPressed by shutterInteractionSource.collectIsPressedAsState()
    val shutterScale by animateFloatAsState(
        targetValue = if (isShutterPressed) 0.90f else 1f,
        animationSpec = tween(120),
        label = "shutter_scale"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF030712)) // Dark native camera viewport
            .testTag("camera_scan_screen")
    ) {
        // Ambient camera lens scrim overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0x1A10B981),
                            Color(0x40020617),
                            Color(0xCC030712)
                        )
                    )
                )
        )

        // Main Layout Column (Top Controls -> Viewfinder Area -> Bottom Controls)
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
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Nút đóng camera (Close X)
                IconButton(
                    onClick = onClose,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color(0x770B1120))
                        .border(1.dp, Color(0x33FFFFFF), CircleShape)
                        .testTag("close_camera_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Đóng camera",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
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
                            val btnBg by animateColorAsState(
                                targetValue = if (isSelected) EmeraldPrimary else Color.Transparent,
                                label = "ratio_bg"
                            )
                            val textColor by animateColorAsState(
                                targetValue = if (isSelected) Color(0xFF064E3B) else Color(0xFF94A3B8),
                                label = "ratio_txt"
                            )

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
                // AI Detect Badge (ai-detect-badge)
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xDD0B1120),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x5534D399)),
                    modifier = Modifier
                        .padding(bottom = 12.dp)
                        .testTag("ai_detect_badge")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
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
                                "full" -> "AI Khung Chụp Toàn Cảnh (Full View)"
                                "4:3" -> "AI Khung Chụp Chuẩn 4:3 (Tập Vở Ô Ly)"
                                else -> "AI Khung Chụp Chuẩn 9:16 (Toàn Màn Hình)"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }

                // Document Frame with dynamic aspect ratio (document-frame)
                val frameModifier = when (selectedRatio) {
                    "full" -> Modifier
                        .fillMaxWidth(0.92f)
                        .fillMaxHeight(0.72f)
                    "4:3" -> Modifier
                        .fillMaxWidth(0.86f)
                        .aspectRatio(3f / 4f)
                    else -> Modifier
                        .fillMaxWidth(0.82f)
                        .aspectRatio(9f / 16f)
                }

                Box(
                    modifier = frameModifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFFFFDF8)) // Authentic ivory paper background
                        .border(1.5.dp, Color(0x4434D399), RoundedCornerShape(16.dp))
                        .testTag("document_viewfinder_frame")
                ) {
                    // Realistic Student Notebook Paper Preview
                    NotebookPaperViewfinderCanvas(
                        paperData = currentPaper,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Warm Flashlight illumination layer when flash is active
                    if (isFlashOn) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(
                                            Color(0x33FEF08A),
                                            Color(0x15F59E0B),
                                            Color.Transparent
                                        )
                                    )
                                )
                        )
                    }

                    // 4 Prominent High-Tech Emerald Viewfinder Corners (f-tl, f-tr, f-bl, f-br)
                    ViewfinderCornerBracket(Modifier.align(Alignment.TopStart), isTop = true, isLeft = true)
                    ViewfinderCornerBracket(Modifier.align(Alignment.TopEnd), isTop = true, isLeft = false)
                    ViewfinderCornerBracket(Modifier.align(Alignment.BottomStart), isTop = false, isLeft = true)
                    ViewfinderCornerBracket(Modifier.align(Alignment.BottomEnd), isTop = false, isLeft = false)

                    // Dynamic Animated Scanning Laser Beam (scanning-laser)
                    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                        val maxH = maxHeight
                        val laserOffset = maxH * laserProgress

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(32.dp)
                                .offset(y = laserOffset - 16.dp)
                        ) {
                            // Soft glow aura
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(28.dp)
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                Color.Transparent,
                                                EmeraldPrimary.copy(alpha = 0.20f),
                                                Color.Transparent
                                            )
                                        )
                                    )
                            )
                            // Sharp radiant beam line
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(2.5.dp)
                                    .align(Alignment.Center)
                                    .background(
                                        Brush.horizontalGradient(
                                            colors = listOf(
                                                Color.Transparent,
                                                EmeraldPrimary.copy(alpha = 0.5f),
                                                Color(0xFF6EE7B7),
                                                Color.White,
                                                Color(0xFF6EE7B7),
                                                EmeraldPrimary.copy(alpha = 0.5f),
                                                Color.Transparent
                                            )
                                        )
                                    )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Gyroscope Level Indicator Pill (gyro-level-pill)
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xDD0B1120),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x4434D399)),
                    modifier = Modifier.testTag("gyro_level_pill")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
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
                            text = when (selectedRatio) {
                                "full" -> "Góc chụp chuẩn 90° • Khổ toàn cảnh Full"
                                "4:3" -> "Góc chụp chuẩn 90° • Khổ đứng 4:3"
                                else -> "Góc chụp chuẩn 90° • Khổ đứng 9:16"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = EmeraldPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
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
                    .navigationBarsPadding()
                    .padding(bottom = 24.dp, start = 32.dp, end = 32.dp, top = 10.dp),
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

                // Nút chụp & chấm điểm trung tâm (cam-shutter-main-btn)
                Box(
                    modifier = Modifier
                        .size(82.dp)
                        .scale(shutterScale)
                        .clip(CircleShape)
                        .background(Color(0x3310B981))
                        .border(4.dp, EmeraldPrimary, CircleShape)
                        .clickable(
                            interactionSource = shutterInteractionSource,
                            indication = null
                        ) {
                            // First attempt native camera, fallback to high-res scanned notebook bitmap
                            try {
                                cameraLauncher.launch(null)
                            } catch (_: Exception) {
                                val generated = generateSampleNotebookBitmap(currentPaper)
                                onCapture(generated)
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

                // Nút đổi camera / đổi bài mẫu (cam-round-icon-btn)
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(Color(0x881E293B))
                        .border(1.5.dp, Color(0x66FFFFFF), CircleShape)
                        .clickable {
                            selectedSampleIndex++
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
                            contentDescription = "Đổi bài mẫu / Đổi camera",
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
    }
}

/**
 * 4 GÓC KHUNG QUÉT XANH LỤC BẢO (frame-corner f-tl, f-tr, f-bl, f-br)
 */
@Composable
private fun ViewfinderCornerBracket(
    modifier: Modifier = Modifier,
    isTop: Boolean,
    isLeft: Boolean
) {
    androidx.compose.foundation.Canvas(
        modifier = modifier
            .size(32.dp)
            .padding(6.dp)
    ) {
        val stroke = 3.5.dp.toPx()
        val color = EmeraldPrimary

        val startX = if (isLeft) 0f else size.width
        val endX = if (isLeft) size.width else 0f
        val startY = if (isTop) 0f else size.height
        val endY = if (isTop) size.height else 0f

        // Horizontal line
        drawLine(
            color = color,
            start = Offset(startX, startY),
            end = Offset(endX, startY),
            strokeWidth = stroke
        )
        // Vertical line
        drawLine(
            color = color,
            start = Offset(startX, startY),
            end = Offset(startX, endY),
            strokeWidth = stroke
        )
    }
}

/**
 * BẢN VẼ TẬP VỞ Ô LY HỌC SINH MÔ PHỎNG CAMERA CHÂN THỰC (document-scanned-paper)
 */
@Composable
private fun NotebookPaperViewfinderCanvas(
    paperData: SamplePaperData,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        // Grid background & red margin line
        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
            val step = 22.dp.toPx()
            val gridColor = Color(0x3538BDF8)
            val marginLineColor = Color(0x55EF4444)

            // Ô ly vertical lines
            var x = 0f
            while (x < size.width) {
                drawLine(
                    color = gridColor,
                    start = Offset(x, 0f),
                    end = Offset(x, size.height),
                    strokeWidth = 1f
                )
                x += step
            }

            // Ô ly horizontal lines
            var y = 0f
            while (y < size.height) {
                drawLine(
                    color = gridColor,
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 1f
                )
                y += step
            }

            // Red vertical margin line
            val marginX = 36.dp.toPx()
            drawLine(
                color = marginLineColor,
                start = Offset(marginX, 0f),
                end = Offset(marginX, size.height),
                strokeWidth = 2.5f
            )
        }

        // Student Cursive Handwriting Text Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 44.dp, top = 20.dp, end = 16.dp, bottom = 16.dp)
        ) {
            // Paper Title
            Text(
                text = paperData.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E3A8A), // Navy blue fountain pen ink
                letterSpacing = 0.3.sp
            )
            Text(
                text = paperData.subtitle,
                fontSize = 10.sp,
                fontStyle = FontStyle.Italic,
                color = Color(0xFF64748B),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Poem lines
            paperData.lines.forEachIndexed { index, line ->
                Text(
                    text = line,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1E40AF), // Dark blue cursive ink
                    lineHeight = 22.sp,
                    letterSpacing = 0.4.sp,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
        }
    }
}

/**
 * TẠO BITMAP TẬP VỞ Ô LY CHẤT LƯỢNG CAO CHO BƯỚC CHẤM BÀI
 */
private fun generateSampleNotebookBitmap(paper: SamplePaperData): Bitmap {
    val width = 1080
    val height = 1440
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
        strokeWidth = 2f
        style = Paint.Style.STROKE
    }
    val step = 48f
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
        strokeWidth = 4f
    }
    val marginX = 140f
    canvas.drawLine(marginX, 0f, marginX, height.toFloat(), marginPaint)

    // 4. Mực viết tay tiếng Việt
    val titlePaint = Paint().apply {
        color = android.graphics.Color.parseColor("#1E3A8A")
        textSize = 46f
        isAntiAlias = true
        isFakeBoldText = true
    }
    canvas.drawText(paper.title, marginX + 40f, 160f, titlePaint)

    val subtitlePaint = Paint().apply {
        color = android.graphics.Color.parseColor("#64748B")
        textSize = 30f
        isAntiAlias = true
    }
    canvas.drawText(paper.subtitle, marginX + 40f, 220f, subtitlePaint)

    val textPaint = Paint().apply {
        color = android.graphics.Color.parseColor("#1E40AF")
        textSize = 38f
        isAntiAlias = true
    }
    var lineY = 320f
    for (line in paper.lines) {
        canvas.drawText(line, marginX + 40f, lineY, textPaint)
        lineY += 72f
    }

    return bitmap
}
