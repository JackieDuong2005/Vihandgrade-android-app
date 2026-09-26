package com.example.ui.components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.graphics.Typeface
import android.media.ExifInterface
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CropFree
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.example.data.model.ErrorBox
import com.example.data.model.GradeResult
import com.example.ui.theme.AppTheme
import com.example.ui.theme.EmeraldPrimary
import java.io.File

/**
 * 6 loại mã màu sư phạm chuẩn hóa theo ERROR_THEMES của Web ViHand Grade:
 * 1. Phụ âm đầu (ch/tr, s/x, d/gi/r, l/n): Rose-500 (#F43F5E)
 * 2. Dấu thanh (Hỏi/Ngã, Sắc/Nặng): Purple-500 (#A855F7)
 * 3. Vần (an/ang, en/eng, iên/iêng): Orange-500 (#F97316)
 * 4. Nguyên âm / Âm chính (o/ô, u/ư): Emerald-500 (#10B981)
 * 5. Âm cuối (t/c, n/ng): Sky-500 (#0EA5E9)
 * 6. Viết hoa (Đầu câu, Danh từ riêng): Amber-500 (#F59E0B)
 */
fun getErrorCategoryColor(errorType: String): Color {
    val lower = errorType.lowercase()
    return when {
        lower.contains("phụ âm đầu") || lower.contains("âm đầu") || lower.contains("phu_am_dau") ||
        lower.contains("ch/tr") || lower.contains("s/x") || lower.contains("l/n") || lower.contains("d/gi") || lower.contains("c/k") ->
            com.example.ui.theme.ErrorPhuAmDau

        lower.contains("thanh") || lower.contains("dấu thanh") || lower.contains("dau_thanh") ||
        lower.contains("hỏi") || lower.contains("ngã") || lower.contains("sắc") || lower.contains("nặng") ->
            com.example.ui.theme.ErrorDauThanh

        lower.contains("vần") || lower.contains("van") || lower.contains("uôn") ||
        lower.contains("iên") || lower.contains("ay/ey") || lower.contains("ao/au") ->
            com.example.ui.theme.ErrorVan

        lower.contains("nguyên âm") || lower.contains("âm chính") || lower.contains("am_chinh") ->
            com.example.ui.theme.ErrorAmChinh

        lower.contains("âm cuối") || lower.contains("phu_am_cuoi") || lower.contains("t/c") || lower.contains("n/ng") ->
            com.example.ui.theme.ErrorPhuAmCuoi

        lower.contains("hoa") || lower.contains("viết hoa") || lower.contains("viet_hoa") ||
        lower.contains("chữ cái đầu") || lower.contains("tên riêng") ->
            com.example.ui.theme.ErrorVietHoa

        else -> Color(0xFF64748B) // Khác / Dấu câu
    }
}

fun getErrorCategoryName(errorType: String): String {
    val lower = errorType.lowercase()
    return when {
        lower.contains("phụ âm đầu") || lower.contains("âm đầu") || lower.contains("phu_am_dau") ||
        lower.contains("ch/tr") || lower.contains("s/x") || lower.contains("l/n") || lower.contains("d/gi") || lower.contains("c/k") ->
            "Phụ âm đầu"

        lower.contains("thanh") || lower.contains("dấu thanh") || lower.contains("dau_thanh") ||
        lower.contains("hỏi") || lower.contains("ngã") || lower.contains("sắc") || lower.contains("nặng") ->
            "Dấu thanh"

        lower.contains("vần") || lower.contains("van") || lower.contains("uôn") ||
        lower.contains("iên") || lower.contains("ay/ey") || lower.contains("ao/au") ->
            "Vần"

        lower.contains("nguyên âm") || lower.contains("âm chính") || lower.contains("am_chinh") ->
            "Nguyên âm"

        lower.contains("âm cuối") || lower.contains("phu_am_cuoi") || lower.contains("t/c") || lower.contains("n/ng") ->
            "Âm cuối"

        lower.contains("hoa") || lower.contains("viết hoa") || lower.contains("viet_hoa") ||
        lower.contains("chữ cái đầu") || lower.contains("tên riêng") ->
            "Viết hoa"

        else -> "Dấu câu & Khác"
    }
}

@Composable
fun PhotoBoundingBoxViewer(
    result: GradeResult,
    selectedErrorId: String?,
    onSelectError: (ErrorBox) -> Unit,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    val emeraldText = if (isDarkTheme) EmeraldPrimary else Color(0xFF047857)
    var selectedCategoryFilter by remember { mutableStateOf<String?>("Tất cả") }
    var isZoomed by remember { mutableStateOf(false) }
    var showBoundingBoxes by remember { mutableStateOf(true) }

    // 1. Chuẩn hóa URL ảnh đầy đủ (tự động ghép domain nếu là đường dẫn tương đối)
    val effectiveImageUrl = remember(result.imageUrl, result.photoPath) {
        val url = result.imageUrl?.takeIf { it.isNotBlank() }
        when {
            url != null && (url.startsWith("http://") || url.startsWith("https://")) -> url
            url != null && url.startsWith("/") -> "https://vihandgrade.click$url"
            !result.photoPath.isNullOrBlank() && (result.photoPath.startsWith("http://") || result.photoPath.startsWith("https://")) -> result.photoPath
            else -> null
        }
    }

    // 2. Resolve bitmap an toàn với xoay EXIF tự động
    val resolvedBitmap = remember(result.id, result.photoPath, result.photoBitmap) {
        when {
            result.photoBitmap != null -> result.photoBitmap
            !result.photoPath.isNullOrBlank() -> {
                val file = File(result.photoPath)
                if (file.exists() && file.length() > 0) {
                    try {
                        val rawBmp = BitmapFactory.decodeFile(file.absolutePath)
                        if (rawBmp != null) {
                            val exif = ExifInterface(file.absolutePath)
                            val orientation = exif.getAttributeInt(
                                ExifInterface.TAG_ORIENTATION,
                                ExifInterface.ORIENTATION_NORMAL
                            )
                            val rotation = when (orientation) {
                                ExifInterface.ORIENTATION_ROTATE_90 -> 90f
                                ExifInterface.ORIENTATION_ROTATE_180 -> 180f
                                ExifInterface.ORIENTATION_ROTATE_270 -> 270f
                                else -> 0f
                            }
                            if (rotation != 0f) {
                                val matrix = android.graphics.Matrix().apply { postRotate(rotation) }
                                Bitmap.createBitmap(rawBmp, 0, 0, rawBmp.width, rawBmp.height, matrix, true)
                            } else {
                                rawBmp
                            }
                        } else null
                    } catch (_: Exception) {
                        null
                    }
                } else null
            }
            else -> null
        }
    }

    // 3. Tự động lấy kích thước thực tế (Width & Height) của ảnh
    val context = LocalContext.current
    var detectedWidth by remember(result.id) { mutableIntStateOf(0) }
    var detectedHeight by remember(result.id) { mutableIntStateOf(0) }

    LaunchedEffect(resolvedBitmap, result.sampleImageResId, result.photoPath) {
        if (resolvedBitmap != null && resolvedBitmap.width > 0 && resolvedBitmap.height > 0) {
            detectedWidth = resolvedBitmap.width
            detectedHeight = resolvedBitmap.height
        } else if (result.sampleImageResId != null) {
            try {
                val opts = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                BitmapFactory.decodeResource(context.resources, result.sampleImageResId, opts)
                if (opts.outWidth > 0 && opts.outHeight > 0) {
                    detectedWidth = opts.outWidth
                    detectedHeight = opts.outHeight
                }
            } catch (_: Exception) {}
        } else if (!result.photoPath.isNullOrBlank()) {
            val file = File(result.photoPath)
            if (file.exists() && file.length() > 0) {
                try {
                    val opts = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                    BitmapFactory.decodeFile(file.absolutePath, opts)
                    if (opts.outWidth > 0 && opts.outHeight > 0) {
                        detectedWidth = opts.outWidth
                        detectedHeight = opts.outHeight
                    }
                } catch (_: Exception) {}
            }
        }
    }

    // Tỷ lệ khung ảnh thực tế (mặc định 1.333f cho vở 4:3 nếu chưa tải xong)
    val actualAspectRatio = remember(detectedWidth, detectedHeight, resolvedBitmap) {
        when {
            detectedWidth > 0 && detectedHeight > 0 -> detectedWidth.toFloat() / detectedHeight.toFloat()
            resolvedBitmap != null && resolvedBitmap.height > 0 -> resolvedBitmap.width.toFloat() / resolvedBitmap.height.toFloat()
            else -> 1.333f
        }
    }

    // Filter errors according to active chip
    val filteredErrors = remember(result.errors, selectedCategoryFilter) {
        if (selectedCategoryFilter == null || selectedCategoryFilter == "Tất cả") {
            result.errors
        } else {
            result.errors.filter { getErrorCategoryName(it.errorType) == selectedCategoryFilter }
        }
    }

    // Category count statistics for filter chips
    val categoryCounts = remember(result.errors) {
        result.errors.groupBy { getErrorCategoryName(it.errorType) }
            .mapValues { it.value.size }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        // Main Photo Frame Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("photo_bounding_box_viewer"),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isDarkTheme) Color(0xFF0F172A) else Color(0xFFF8FAFC)
            ),
            border = BorderStroke(1.5.dp, if (isDarkTheme) Color(0xFF334155) else Color(0xFFE2E8F0)),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Header Bar above photo
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(if (isDarkTheme) Color(0xFF1E293B) else Color(0xFFF1F5F9))
                        .padding(horizontal = 14.dp, vertical = 9.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFF059669).copy(alpha = 0.15f)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(Color(0xFF10B981), CircleShape)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "YOLOv8 DETECTED • ${result.errors.size} LỖI",
                                    color = emeraldText,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        if (resolvedBitmap != null || !result.photoPath.isNullOrBlank() || !effectiveImageUrl.isNullOrBlank()) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isDarkTheme) Color(0xFF334155) else Color(0xFFE2E8F0)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PhotoCamera,
                                        contentDescription = null,
                                        modifier = Modifier.size(11.dp),
                                        tint = AppTheme.colors.textMuted
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "Ảnh thực tế",
                                        fontSize = 9.5.sp,
                                        color = AppTheme.colors.textMuted,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }

                    // Action Controls: Toggle BBoxes Visibility & Zoom
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Eye Toggle Button: Hide / Show BBoxes
                        IconButton(
                            onClick = { showBoundingBoxes = !showBoundingBoxes },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = if (showBoundingBoxes) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (showBoundingBoxes) "Ẩn khung lỗi" else "Hiện khung lỗi",
                                tint = emeraldText,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        // Zoom Toggle Button
                        IconButton(
                            onClick = { isZoomed = !isZoomed },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = if (isZoomed) Icons.Default.ZoomOut else Icons.Default.ZoomIn,
                                contentDescription = if (isZoomed) "Thu nhỏ" else "Phóng to",
                                tint = emeraldText,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // Photo Display Container with Zero-Letterbox Architecture
                var displayedImgWidthPx by remember { mutableFloatStateOf(0f) }
                var displayedImgHeightPx by remember { mutableFloatStateOf(0f) }
                val density = LocalDensity.current

                val zoomModifier = if (isZoomed) {
                    Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                } else {
                    Modifier.fillMaxWidth()
                }

                Box(
                    modifier = zoomModifier
                        .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
                        .background(if (isDarkTheme) Color(0xFF090D16) else Color(0xFFF1F5F9))
                ) {
                    val innerImageModifier = if (isZoomed) {
                        Modifier
                            .width(540.dp)
                            .wrapContentHeight()
                    } else {
                        Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                    }

                    // LAYER 1: Background Real Handwriting Photo (determines exact layout bounds)
                    Box(
                        modifier = innerImageModifier
                            .onGloballyPositioned { coords ->
                                displayedImgWidthPx = coords.size.width.toFloat()
                                displayedImgHeightPx = coords.size.height.toFloat()
                            }
                    ) {
                        if (resolvedBitmap != null) {
                            Image(
                                bitmap = resolvedBitmap.asImageBitmap(),
                                contentDescription = "Ảnh bài thi học sinh",
                                modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                                contentScale = ContentScale.FillWidth
                            )
                        } else if (result.sampleImageResId != null) {
                            Image(
                                painter = painterResource(id = result.sampleImageResId),
                                contentDescription = "Ảnh bài thi mẫu",
                                modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                                contentScale = ContentScale.FillWidth
                            )
                        } else if (!effectiveImageUrl.isNullOrBlank()) {
                            AsyncImage(
                                model = effectiveImageUrl,
                                contentDescription = "Ảnh bài thi từ máy chủ",
                                modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                                contentScale = ContentScale.FillWidth,
                                onSuccess = { state ->
                                    val intrinsic = state.painter.intrinsicSize
                                    if (intrinsic.width > 0f && intrinsic.height > 0f) {
                                        detectedWidth = intrinsic.width.toInt()
                                        detectedHeight = intrinsic.height.toInt()
                                    }
                                }
                            )
                        } else {
                            // Realistic Vietnamese 4-grid elementary notebook simulation
                            AuthenticNotebookPaperView(
                                extractedText = result.extractedText,
                                isDarkTheme = isDarkTheme,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(260.dp)
                            )
                        }

                        // LAYER 2: Interactive Bounding Boxes Overlay anchored 1:1 on actual image surface
                        if (showBoundingBoxes && displayedImgWidthPx > 0f && displayedImgHeightPx > 0f) {
                            Box(
                                modifier = Modifier.matchParentSize()
                            ) {
                                val displayedWidthDp = with(density) { displayedImgWidthPx.toDp() }
                                val displayedHeightDp = with(density) { displayedImgHeightPx.toDp() }

                                filteredErrors.forEachIndexed { index, err ->
                                    val isSelected = err.id == selectedErrorId
                                    val categoryColor = getErrorCategoryColor(err.errorType)
                                    val boxBorderColor = if (isSelected) Color(0xFFF59E0B) else categoryColor

                                    // 1. Chuẩn hóa relX [0..0.95]
                                    val rawRelX = when {
                                        err.rel_x1 >= 0.001f && err.rel_x1 <= 1.0f -> err.rel_x1
                                        err.x1 >= 0.001f && err.x1 <= 1.0f -> err.x1
                                        err.x1 > 1.0f && detectedWidth > 0 -> err.x1 / detectedWidth.toFloat()
                                        err.rel_x1 > 1.0f && detectedWidth > 0 -> err.rel_x1 / detectedWidth.toFloat()
                                        else -> 0f
                                    }
                                    val safeRelX = rawRelX.coerceIn(0f, 0.95f)

                                    // 2. Chuẩn hóa relY [0..0.95]
                                    val rawRelY = when {
                                        err.rel_y1 >= 0.001f && err.rel_y1 <= 1.0f -> err.rel_y1
                                        err.y1 >= 0.001f && err.y1 <= 1.0f -> err.y1
                                        err.y1 > 1.0f && detectedHeight > 0 -> err.y1 / detectedHeight.toFloat()
                                        err.rel_y1 > 1.0f && detectedHeight > 0 -> err.rel_y1 / detectedHeight.toFloat()
                                        else -> 0f
                                    }
                                    val safeRelY = rawRelY.coerceIn(0f, 0.95f)

                                    // 3. Chuẩn hóa relW [0.035..0.35]
                                    val rawRelW = when {
                                        err.rel_w >= 0.01f && err.rel_w <= 1.0f -> err.rel_w
                                        err.x2 > err.x1 && err.x2 > 1.0f && detectedWidth > 0 -> (err.x2 - err.x1) / detectedWidth.toFloat()
                                        err.x2 > err.x1 && err.x2 <= 1.0f -> (err.x2 - err.x1)
                                        else -> 0.08f
                                    }
                                    val safeRelW = rawRelW.coerceIn(0.035f, 0.35f)

                                    // 4. Chuẩn hóa relH [0.035..0.16] (ngăn kéo dài xuống dòng 2-3)
                                    val rawRelH = when {
                                        err.rel_h >= 0.01f && err.rel_h <= 1.0f -> err.rel_h
                                        err.y2 > err.y1 && err.y2 > 1.0f && detectedHeight > 0 -> (err.y2 - err.y1) / detectedHeight.toFloat()
                                        err.y2 > err.y1 && err.y2 <= 1.0f -> (err.y2 - err.y1)
                                        else -> 0.06f
                                    }
                                    val safeRelH = rawRelH.coerceIn(0.035f, 0.16f)

                                    val leftDp = displayedWidthDp * safeRelX
                                    val topDp = displayedHeightDp * safeRelY
                                    val widthDp = (displayedWidthDp * safeRelW).coerceAtLeast(16.dp)
                                    val heightDp = (displayedHeightDp * safeRelH).coerceAtLeast(14.dp)

                                    val animScale by animateFloatAsState(
                                        targetValue = if (isSelected) 1.05f else 1.0f,
                                        label = "boxScale"
                                    )
                                    val animBgAlpha by animateFloatAsState(
                                        targetValue = if (isSelected) 0.32f else 0.14f,
                                        label = "boxAlpha"
                                    )

                                    // Vị trí badge: nếu cách mép trên < 24dp thì đặt ở DƯỚI ĐÁY box để không bị che
                                    val isNearTop = (topDp.value < 24f) || (safeRelY < 0.08f)
                                    val badgeOffsetY: androidx.compose.ui.unit.Dp = if (isNearTop) (heightDp + 2.dp) else (-18).dp

                                    Box(
                                        modifier = Modifier
                                            .offset(x = leftDp, y = topDp)
                                            .size(width = widthDp, height = heightDp)
                                            .scale(animScale)
                                            .zIndex(if (isSelected) 10f else 1f)
                                            .border(
                                                width = if (isSelected) 3.dp else 1.8.dp,
                                                color = boxBorderColor,
                                                shape = RoundedCornerShape(4.dp)
                                            )
                                            .background(
                                                boxBorderColor.copy(alpha = animBgAlpha),
                                                shape = RoundedCornerShape(4.dp)
                                            )
                                            .clickable { onSelectError(err) }
                                    ) {
                                        // Floating Pill Tag above or below the box
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = if (isSelected) Color(0xFFF59E0B) else categoryColor,
                                            shadowElevation = if (isSelected) 4.dp else 1.dp,
                                            modifier = Modifier
                                                .align(Alignment.TopStart)
                                                .offset(y = badgeOffsetY, x = (-2).dp)
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                            ) {
                                                Text(
                                                    text = "#${index + 1} ✓ ${err.correctedWord}",
                                                    color = if (isSelected) Color(0xFF0F172A) else Color.White,
                                                    fontSize = 8.5.sp,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    maxLines = 1
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Filter Chips Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // "Tất cả" chip
            FilterChipItem(
                label = "Tất cả (${result.errors.size})",
                isSelected = selectedCategoryFilter == "Tất cả",
                color = emeraldText,
                onClick = { selectedCategoryFilter = "Tất cả" }
            )

            // Dynamic Category Filter Chips
            categoryCounts.forEach { (catName, count) ->
                val catColor = getErrorCategoryColor(catName)
                FilterChipItem(
                    label = "$catName ($count)",
                    isSelected = selectedCategoryFilter == catName,
                    color = catColor,
                    onClick = {
                        selectedCategoryFilter = if (selectedCategoryFilter == catName) "Tất cả" else catName
                    }
                )
            }
        }
    }
}

@Composable
private fun FilterChipItem(
    label: String,
    isSelected: Boolean,
    color: Color,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) color.copy(alpha = 0.16f) else AppTheme.colors.cardElevated,
        border = BorderStroke(
            width = if (isSelected) 1.5.dp else 1.dp,
            color = if (isSelected) color else AppTheme.colors.border
        ),
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
            color = if (isSelected) color else AppTheme.colors.textPrimary,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
        )
    }
}

/**
 * Fallback authentic notebook paper representation when physical image isn't available
 */
@Composable
private fun AuthenticNotebookPaperView(
    extractedText: String,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Paper base color
            drawRect(
                color = if (isDarkTheme) Color(0xFF1E293B) else Color(0xFFFFFDF5),
                size = size
            )

            // Red vertical left margin line (lề vở tiểu học)
            val marginX = w * 0.14f
            drawLine(
                color = Color(0xFFEF4444).copy(alpha = 0.5f),
                start = Offset(marginX, 0f),
                end = Offset(marginX, h),
                strokeWidth = 1.5f
            )

            // Horizontal notebook 4-grid lines (dòng kẻ ô ly)
            val numLines = 14
            val stepY = h / (numLines + 1)
            for (i in 1..numLines) {
                val y = i * stepY
                // Main line
                drawLine(
                    color = Color(0xFF93C5FD).copy(alpha = 0.6f),
                    start = Offset(0f, y),
                    end = Offset(w, y),
                    strokeWidth = 1.2f
                )
                // Sub-grid lines (ô ly nhỏ)
                for (sub in 1..3) {
                    val subY = y + (stepY / 4f) * sub
                    if (subY < h) {
                        drawLine(
                            color = Color(0xFFDBEAFE).copy(alpha = 0.35f),
                            start = Offset(0f, subY),
                            end = Offset(w, subY),
                            strokeWidth = 0.6f
                        )
                    }
                }
            }
        }

        // Student cursive handwriting text preview
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 54.dp, top = 20.dp, end = 20.dp, bottom = 16.dp)
        ) {
            Text(
                text = extractedText.ifBlank { "Văn bản bài thi chữ viết tay tiểu học..." },
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Serif,
                    letterSpacing = 0.8.sp,
                    lineHeight = 25.sp
                ),
                color = if (isDarkTheme) Color(0xFF93C5FD) else Color(0xFF1E3A8A) // Student blue ink
            )
        }
    }
}
