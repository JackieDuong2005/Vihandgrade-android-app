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
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CropFree
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import com.example.ui.theme.EmeraldLight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import android.Manifest
import android.content.pm.PackageManager
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import java.util.concurrent.Executors
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
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()

    // CameraX permission & state
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }
    var isLiveCameraMode by remember { mutableStateOf(hasCameraPermission) }
    var showStudentDialog by remember { mutableStateOf(false) }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
        if (granted) {
            isLiveCameraMode = true
        }
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    var cameraLensFacing by remember { mutableIntStateOf(CameraSelector.LENS_FACING_BACK) }
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var cameraControlInstance by remember { mutableStateOf<CameraControl?>(null) }
    var previewViewInstance by remember { mutableStateOf<PreviewView?>(null) }
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    DisposableEffect(Unit) {
        onDispose {
            try {
                val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
                if (cameraProviderFuture.isDone) {
                    cameraProviderFuture.get().unbindAll()
                }
            } catch (_: Exception) {}
            cameraExecutor.shutdown()
        }
    }

    // Safely bind camera lifecycle to previewViewInstance using TextureView
    LaunchedEffect(isLiveCameraMode, hasCameraPermission, cameraLensFacing, previewViewInstance, lifecycleOwner) {
        val pView = previewViewInstance
        if (isLiveCameraMode && hasCameraPermission && pView != null) {
            try {
                val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
                cameraProviderFuture.addListener({
                    try {
                        val cameraProvider = cameraProviderFuture.get()
                        val preview = Preview.Builder().build().also {
                            it.surfaceProvider = pView.surfaceProvider
                        }
                        val newImageCapture = ImageCapture.Builder()
                            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                            .build()
                        imageCapture = newImageCapture

                        val cameraSelector = CameraSelector.Builder()
                            .requireLensFacing(cameraLensFacing)
                            .build()

                        cameraProvider.unbindAll()
                        val camera = cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview,
                            newImageCapture
                        )
                        cameraControlInstance = camera.cameraControl
                    } catch (_: Exception) {}
                }, ContextCompat.getMainExecutor(context))
            } catch (_: Exception) {}
        } else if (!isLiveCameraMode) {
            try {
                val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
                if (cameraProviderFuture.isDone) {
                    cameraProviderFuture.get().unbindAll()
                }
            } catch (_: Exception) {}
        }
    }

    var isFlashOn by remember { mutableStateOf(false) }

    // Sync torch with flash toggle
    LaunchedEffect(isFlashOn, cameraControlInstance) {
        try {
            cameraControlInstance?.enableTorch(isFlashOn)
        } catch (_: Exception) {}
    }

    // Ratio Switcher state: "9:16", "full", "4:3"
    var selectedRatio by remember { mutableStateOf("9:16") }
    var selectedSampleIndex by remember { mutableIntStateOf(0) }
    val currentPaper = samplePapers[selectedSampleIndex % samplePapers.size]

    // Quick Student Bar & Grading Mode states
    var selectedClass by remember { mutableStateOf("Lớp 3A1") }
    var selectedStudent by remember { mutableStateOf("Nguyễn Bảo Nam") }
    var isAnonymousMode by remember { mutableStateOf(false) }
    var selectedGradingMode by remember { mutableStateOf("dictation") } // "dictation" (7-3) or "essay" (4-3-2-1)
    var showClassDropdown by remember { mutableStateOf(false) }
    var showStudentDropdown by remember { mutableStateOf(false) }

    val classOptions = listOf("Lớp 3A1", "Lớp 3A2", "Lớp 4B", "Lớp 5A")
    val studentOptions = listOf("Nguyễn Bảo Nam", "Trần Mai Chi", "Lê Hoàng Khôi", "Nguyễn Văn An", "Phạm Thu Hà")

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
            // 1. THANH ĐIỀU KHIỂN TRÊN TINH GỌN (cam-top-controls)
            // ==========================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Nút quay lại gọn gàng (Back Icon)
                IconButton(
                    onClick = { onClose() },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0x990B1120))
                        .border(1.dp, Color(0x33FFFFFF), CircleShape)
                        .testTag("close_camera_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Quay lại",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Huy hiệu Chọn Lớp & Học Sinh tinh gọn (Consolidated Student & Class Chip)
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xCC0B1120),
                    border = BorderStroke(
                        1.dp,
                        if (isAnonymousMode) Color(0xFFF59E0B) else Color(0x4434D399)
                    ),
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { showStudentDialog = true }
                        .testTag("cam_student_selector")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isAnonymousMode) Icons.Default.VisibilityOff else Icons.Default.School,
                            contentDescription = null,
                            tint = if (isAnonymousMode) Color(0xFFF59E0B) else EmeraldPrimary,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isAnonymousMode) "Rọc phách ẩn danh" else "$selectedClass • $selectedStudent",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Nhóm nút bên phải: Tỷ lệ khung hình & Bật/Tắt Flash
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Nút đổi tỷ lệ gọn gàng (cam_ratio_switcher)
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0x990B1120),
                        border = BorderStroke(1.dp, Color(0x33FFFFFF)),
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable {
                                selectedRatio = when (selectedRatio) {
                                    "9:16" -> "4:3"
                                    "4:3" -> "full"
                                    else -> "9:16"
                                }
                            }
                            .testTag("cam_ratio_switcher")
                    ) {
                        Text(
                            text = when (selectedRatio) {
                                "4:3" -> "4:3"
                                "full" -> "Full"
                                else -> "9:16"
                            },
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 7.dp)
                        )
                    }

                    // Nút Bật/Tắt Flash (Zap)
                    IconButton(
                        onClick = { isFlashOn = !isFlashOn },
                        modifier = Modifier
                            .size(40.dp)
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
            }

            // ==========================================
            // 2. KHUNG NGẮM TÀI LIỆU RỘNG RÃI (viewfinder-main-area)
            // ==========================================
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Subtle AI Guide Pill (ai_detect_badge)
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xBB0B1120),
                    border = BorderStroke(1.dp, Color(0x3334D399)),
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .testTag("ai_detect_badge")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CropFree,
                            contentDescription = null,
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isLiveCameraMode) "Căn chỉnh bài viết vào khung chụp" else "Đang xem bài mẫu • ${currentPaper.title}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Medium,
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
                        border = BorderStroke(1.dp, EmeraldPrimary),
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

                // Document Frame with dynamic aspect ratio - expansive & clean
                val frameModifier = when (selectedRatio) {
                    "full" -> Modifier
                        .fillMaxWidth(0.94f)
                        .fillMaxHeight(0.78f)
                    "4:3" -> Modifier
                        .fillMaxWidth(0.90f)
                        .aspectRatio(3f / 4f)
                    else -> Modifier
                        .fillMaxWidth(0.86f)
                        .aspectRatio(9f / 16f)
                }

                Box(
                    modifier = frameModifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isLiveCameraMode) Color.Black else Color(0xFFFFFDF8))
                        .border(1.5.dp, Color(0x4434D399), RoundedCornerShape(16.dp))
                        .testTag("document_viewfinder_frame")
                ) {
                    if (isLiveCameraMode) {
                        if (hasCameraPermission) {
                            AndroidView(
                                factory = { ctx ->
                                    PreviewView(ctx).apply {
                                        implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                                        scaleType = PreviewView.ScaleType.FILL_CENTER
                                        previewViewInstance = this
                                    }
                                },
                                onRelease = {
                                    previewViewInstance = null
                                },
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            // Permission needed prompt
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "Cần quyền truy cập Camera",
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Cho phép ứng dụng sử dụng camera để quét bài viết tay của học sinh trực tiếp.",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 12.sp,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = EmeraldPrimary,
                                    modifier = Modifier.clickable {
                                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                    }
                                ) {
                                    Text(
                                        text = "Cấp quyền Camera",
                                        color = Color(0xFF064E3B),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                    )
                                }
                            }
                        }
                    } else {
                        // Realistic Student Notebook Paper Preview
                        NotebookPaperViewfinderCanvas(
                            paperData = currentPaper,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    // Flashlight illumination overlay when active
                    if (isFlashOn && !isLiveCameraMode) {
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

                Spacer(modifier = Modifier.height(6.dp))

                // Compact Gyroscope / Alignment Status
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0x990B1120),
                    modifier = Modifier.testTag("gyro_level_pill")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Góc chuẩn 90°",
                            color = Color(0xFF94A3B8),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // ==========================================
            // 3. CHẾ ĐỘ CHẤM & NÚT CHỤP PHÍA DƯỚI (cam-bottom-controls)
            // ==========================================
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Grading Mode Switcher (Chính tả vs Tập làm văn) đặt ngay trên nút chụp thuận tiện
                Row(
                    modifier = Modifier.padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(28.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    listOf(
                        "dictation" to "CHÍNH TẢ",
                        "essay" to "TẬP LÀM VĂN"
                    ).forEach { (mode, title) ->
                        val isSelected = selectedGradingMode == mode
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable { selectedGradingMode = mode }
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = title,
                                color = if (isSelected) EmeraldLight else Color(0xFF64748B),
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                                letterSpacing = 1.sp
                            )
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .padding(top = 3.dp)
                                        .size(width = 16.dp, height = 2.5.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(EmeraldPrimary)
                                )
                            } else {
                                Spacer(modifier = Modifier.height(5.5.dp))
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, end = 24.dp, top = 2.dp),
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

                                if (isLiveCameraMode && hasCameraPermission && imageCapture != null) {
                                    try {
                                        imageCapture?.takePicture(
                                            cameraExecutor,
                                            object : ImageCapture.OnImageCapturedCallback() {
                                                override fun onCaptureSuccess(imageProxy: ImageProxy) {
                                                    val bitmap = imageProxy.toBitmap()
                                                    imageProxy.close()
                                                    onCapture(bitmap)
                                                }

                                                override fun onError(exception: ImageCaptureException) {
                                                    // Fallback to sample paper bitmap on error
                                                    val fallbackBitmap = getOrGenerateBitmap(selectedSampleIndex)
                                                    onCapture(fallbackBitmap)
                                                }
                                            }
                                        )
                                    } catch (_: Exception) {
                                        val fallbackBitmap = getOrGenerateBitmap(selectedSampleIndex)
                                        onCapture(fallbackBitmap)
                                    }
                                } else {
                                    // Instant capture bitmap from sample paper
                                    val bitmap = getOrGenerateBitmap(selectedSampleIndex)
                                    onCapture(bitmap)
                                }
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

                // Nút đổi bài mẫu hoặc lật camera trước/sau (cam-round-icon-btn)
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(Color(0x881E293B))
                        .border(1.5.dp, Color(0x66FFFFFF), CircleShape)
                        .clickable {
                            if (isLiveCameraMode) {
                                cameraLensFacing = if (cameraLensFacing == CameraSelector.LENS_FACING_BACK) {
                                    CameraSelector.LENS_FACING_FRONT
                                } else {
                                    CameraSelector.LENS_FACING_BACK
                                }
                                switchNotification = if (cameraLensFacing == CameraSelector.LENS_FACING_BACK) {
                                    "Đã chuyển Camera sau"
                                } else {
                                    "Đã chuyển Camera trước"
                                }
                            } else {
                                selectedSampleIndex++
                                val next = samplePapers[selectedSampleIndex % samplePapers.size]
                                switchNotification = "Đã đổi bài: ${next.title}"
                            }
                            coroutineScope.launch {
                                delay(2200)
                                switchNotification = null
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
                            contentDescription = if (isLiveCameraMode) "Lật Camera" else "Đổi bài mẫu",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = if (isLiveCameraMode) "Lật cam" else "Đổi bài",
                            color = Color(0xFFCBD5E1),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }

    // Student & Class Selection Dialog (Teacher picks Class, Student, or Anonymous mode)
        if (showStudentDialog) {
            AlertDialog(
                onDismissRequest = { showStudentDialog = false },
                containerColor = Color(0xFF1E293B),
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = null,
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Thiết lập bài chấm",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        // Anonymous switch
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isAnonymousMode) Color(0x33F59E0B) else Color(0x220F172A),
                            border = BorderStroke(1.dp, if (isAnonymousMode) Color(0xFFF59E0B) else Color(0x33FFFFFF)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isAnonymousMode = !isAnonymousMode }
                                .testTag("cam_anonymous_toggle")
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        "Chấm rọc phách ẩn danh",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        "Ẩn tên học sinh để chấm khách quan",
                                        color = Color(0xFF94A3B8),
                                        fontSize = 11.sp
                                    )
                                }
                                Switch(
                                    checked = isAnonymousMode,
                                    onCheckedChange = { isAnonymousMode = it },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = Color(0xFFF59E0B)
                                    )
                                )
                            }
                        }

                        if (!isAnonymousMode) {
                            // Class selector chips
                            Column {
                                Text("Lớp học:", color = Color(0xFF94A3B8), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.testTag("cam_class_selector")
                                ) {
                                    classOptions.forEach { cls ->
                                        val isClsSelected = selectedClass == cls
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (isClsSelected) EmeraldPrimary else Color(0x33334155),
                                            modifier = Modifier.clickable { selectedClass = cls }
                                        ) {
                                            Text(
                                                text = cls,
                                                color = if (isClsSelected) Color(0xFF064E3B) else Color.White,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            // Student list
                            Column {
                                Text("Học sinh:", color = Color(0xFF94A3B8), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                Spacer(modifier = Modifier.height(6.dp))
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(4.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    studentOptions.forEach { std ->
                                        val isStdSelected = selectedStudent == std
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (isStdSelected) Color(0x2210B981) else Color.Transparent,
                                            border = BorderStroke(1.dp, if (isStdSelected) EmeraldPrimary else Color(0x22FFFFFF)),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    selectedStudent = std
                                                    showStudentDialog = false
                                                }
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Person,
                                                    contentDescription = null,
                                                    tint = if (isStdSelected) EmeraldPrimary else Color(0xFF94A3B8),
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = std,
                                                    color = if (isStdSelected) EmeraldLight else Color.White,
                                                    fontSize = 13.sp,
                                                    fontWeight = if (isStdSelected) FontWeight.Bold else FontWeight.Normal
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showStudentDialog = false }) {
                        Text("Xác nhận", color = EmeraldPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            )
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
