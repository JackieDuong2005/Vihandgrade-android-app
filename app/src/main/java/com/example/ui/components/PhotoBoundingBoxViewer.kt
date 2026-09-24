package com.example.ui.components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.graphics.Typeface
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CropFree
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Visibility
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
import androidx.compose.runtime.getValue
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
 * 6 Vietnamese pedagogical error color categories matching ViHand Grade Web specification:
 * 1. Phụ âm đầu (ch/tr, s/x, d/gi/r, l/n): Coral Red (#EF4444)
 * 2. Vần (uôn/uông, iên/iêng, ao/au): Amber Orange (#F59E0B)
 * 3. Dấu thanh (Hỏi/Ngã, Sắc/Nặng): Sky Blue (#0EA5E9)
 * 4. Viết hoa (Đầu câu, Danh từ riêng): Violet/Indigo (#8B5CF6)
 * 5. Bỏ sót / Thừa chữ: Rose Pink (#F43F5E)
 * 6. Dấu câu / Khoảng cách / Khác: Slate Gray (#64748B)
 */
fun getErrorCategoryColor(errorType: String): Color {
    val lower = errorType.lowercase()
    return when {
        lower.contains("phụ âm") || lower.contains("âm đầu") || lower.contains("ch/tr") ||
        lower.contains("s/x") || lower.contains("l/n") || lower.contains("d/gi") || lower.contains("c/k") ->
            Color(0xFFEF4444) // Coral Red

        lower.contains("vần") || lower.contains("nguyên âm") || lower.contains("uôn") ||
        lower.contains("iên") || lower.contains("ay/ey") || lower.contains("ao/au") ->
            Color(0xFFF59E0B) // Amber Orange

        lower.contains("thanh") || lower.contains("dấu thanh") || lower.contains("hỏi") ||
        lower.contains("ngã") || lower.contains("sắc") || lower.contains("nặng") ->
            Color(0xFF0EA5E9) // Sky Blue

        lower.contains("hoa") || lower.contains("viết hoa") || lower.contains("chữ cái đầu") ||
        lower.contains("tên riêng") ->
            Color(0xFF8B5CF6) // Violet / Indigo

        lower.contains("sót") || lower.contains("bỏ sót") || lower.contains("thiếu") ||
        lower.contains("thừa") || lower.contains("lặp") ->
            Color(0xFFF43F5E) // Rose Pink

        else -> Color(0xFF64748B) // Slate Gray (Dấu câu, khoảng cách, format)
    }
}

fun getErrorCategoryName(errorType: String): String {
    val lower = errorType.lowercase()
    return when {
        lower.contains("phụ âm") || lower.contains("âm đầu") || lower.contains("ch/tr") ||
        lower.contains("s/x") || lower.contains("l/n") || lower.contains("d/gi") || lower.contains("c/k") ->
            "Phụ âm đầu"

        lower.contains("vần") || lower.contains("nguyên âm") || lower.contains("uôn") ||
        lower.contains("iên") || lower.contains("ay/ey") || lower.contains("ao/au") ->
            "Vần & Nguyên âm"

        lower.contains("thanh") || lower.contains("dấu thanh") || lower.contains("hỏi") ||
        lower.contains("ngã") || lower.contains("sắc") || lower.contains("nặng") ->
            "Dấu thanh"

        lower.contains("hoa") || lower.contains("viết hoa") || lower.contains("chữ cái đầu") ||
        lower.contains("tên riêng") ->
            "Viết hoa"

        lower.contains("sót") || lower.contains("bỏ sót") || lower.contains("thiếu") ||
        lower.contains("thừa") || lower.contains("lặp") ->
            "Thiếu / Thừa chữ"

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

    // Resolve bitmap in order: in-memory photoBitmap -> disk photoPath -> sampleImageResId -> null
    val resolvedBitmap = remember(result.id, result.photoPath, result.photoBitmap) {
        when {
            result.photoBitmap != null -> result.photoBitmap
            !result.photoPath.isNullOrBlank() -> {
                val file = File(result.photoPath)
                if (file.exists() && file.length() > 0) {
                    try {
                        BitmapFactory.decodeFile(file.absolutePath)
                    } catch (_: Exception) {
                        null
                    }
                } else null
            }
            else -> null
        }
    }

    val context = LocalContext.current
    val imageIntrinsicRatio = remember(resolvedBitmap, result.sampleImageResId) {
        when {
            resolvedBitmap != null && resolvedBitmap.height > 0 -> {
                resolvedBitmap.width.toFloat() / resolvedBitmap.height.toFloat()
            }
            result.sampleImageResId != null -> {
                try {
                    val opts = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                    BitmapFactory.decodeResource(context.resources, result.sampleImageResId, opts)
                    if (opts.outHeight > 0) opts.outWidth.toFloat() / opts.outHeight.toFloat() else 1.5f
                } catch (_: Exception) {
                    1.5f
                }
            }
            else -> 1.5f // Default 3:2 notebook ratio
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

                        if (resolvedBitmap != null || !result.photoPath.isNullOrBlank()) {
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

                    // Zoom / Inspect Toggle Button
                    Row(verticalAlignment = Alignment.CenterVertically) {
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

                // Photo Display Container with Bounding Boxes
                val containerModifier = if (isZoomed) {
                    Modifier
                        .fillMaxWidth()
                        .height(460.dp)
                } else {
                    Modifier
                        .fillMaxWidth()
                        .aspectRatio(imageIntrinsicRatio.coerceIn(0.75f, 2.0f))
                }

                BoxWithConstraints(
                    modifier = containerModifier
                        .background(if (isDarkTheme) Color(0xFF090D16) else Color(0xFFF1F5F9))
                        .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
                ) {
                    val canvasWidth = maxWidth
                    val canvasHeight = maxHeight

                    // 1. Layer: Background Photo or Authentic Handwriting Canvas
                    if (resolvedBitmap != null) {
                        Image(
                            bitmap = resolvedBitmap.asImageBitmap(),
                            contentDescription = "Ảnh bài thi học sinh",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    } else if (result.sampleImageResId != null) {
                        Image(
                            painter = painterResource(id = result.sampleImageResId),
                            contentDescription = "Ảnh bài thi mẫu",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    } else if (!result.imageUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = result.imageUrl,
                            contentDescription = "Ảnh bài thi từ máy chủ",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    } else {
                        // Realistic Vietnamese 4-grid elementary notebook simulation
                        AuthenticNotebookPaperView(
                            extractedText = result.extractedText,
                            isDarkTheme = isDarkTheme,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    // Calculate precise displayed image dimensions and letterbox offsets under ContentScale.Fit
                    val containerRatio = if (canvasHeight.value > 0f) canvasWidth.value / canvasHeight.value else 1f
                    val displayedWidth: androidx.compose.ui.unit.Dp
                    val displayedHeight: androidx.compose.ui.unit.Dp
                    val offsetX: androidx.compose.ui.unit.Dp
                    val offsetY: androidx.compose.ui.unit.Dp

                    if (imageIntrinsicRatio > containerRatio) {
                        // Wider: image fits width, vertical letterbox bars top & bottom
                        displayedWidth = canvasWidth
                        displayedHeight = canvasWidth / imageIntrinsicRatio
                        offsetX = 0.dp
                        offsetY = ((canvasHeight - displayedHeight) / 2).coerceAtLeast(0.dp)
                    } else {
                        // Taller: image fits height, horizontal letterbox bars left & right
                        displayedHeight = canvasHeight
                        displayedWidth = canvasHeight * imageIntrinsicRatio
                        offsetX = ((canvasWidth - displayedWidth) / 2).coerceAtLeast(0.dp)
                        offsetY = 0.dp
                    }

                    // 2. Layer: Interactive Bounding Boxes Overlay anchored strictly to visible image rect
                    filteredErrors.forEachIndexed { index, err ->
                        val isSelected = err.id == selectedErrorId
                        val categoryColor = getErrorCategoryColor(err.errorType)
                        val boxBorderColor = if (isSelected) Color(0xFFF59E0B) else categoryColor

                        // Coordinates relative to visible photo area
                        val leftDp = offsetX + (displayedWidth * err.rel_x1)
                        val topDp = offsetY + (displayedHeight * err.rel_y1)
                        val widthDp = (displayedWidth * err.rel_w).coerceAtLeast(18.dp)
                        val heightDp = (displayedHeight * err.rel_h).coerceAtLeast(14.dp)

                        val animScale by animateFloatAsState(
                            targetValue = if (isSelected) 1.05f else 1.0f,
                            label = "boxScale"
                        )
                        val animBgAlpha by animateFloatAsState(
                            targetValue = if (isSelected) 0.32f else 0.14f,
                            label = "boxAlpha"
                        )

                        // Avoid pill tag clipping at top edge of container
                        val badgeOffsetY = if (topDp < 22.dp) (heightDp + 2.dp) else (-16).dp

                        Box(
                            modifier = Modifier
                                .offset(x = leftDp, y = topDp)
                                .size(width = widthDp, height = heightDp)
                                .scale(animScale)
                                .zIndex(if (isSelected) 10f else 1f)
                                .border(
                                    width = if (isSelected) 3.dp else 1.8.dp,
                                    color = boxBorderColor,
                                    shape = RoundedCornerShape(5.dp)
                                )
                                .background(
                                    boxBorderColor.copy(alpha = animBgAlpha),
                                    shape = RoundedCornerShape(5.dp)
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
