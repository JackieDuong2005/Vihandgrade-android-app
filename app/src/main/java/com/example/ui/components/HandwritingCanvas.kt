package com.example.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.model.ErrorBox
import com.example.data.model.GradeResult

private data class RenderedBox(
    val error: ErrorBox,
    val rect: Rect,
    val index: Int
)

@Composable
fun HandwritingCanvas(
    result: GradeResult,
    selectedErrorId: String?,
    onSelectError: (ErrorBox) -> Unit,
    modifier: Modifier = Modifier
) {
    // Zoom and Pan state
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    val transformState = rememberTransformableState { zoomChange, offsetChange, _ ->
        scale = (scale * zoomChange).coerceIn(0.9f, 3.5f)
        offset += offsetChange
    }

    // Pulse animation for selected error box
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.65f,
        animationSpec = infiniteRepeatable(
            animation = tween(900),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    // Parse lines of essay text cleanly
    val textLines = remember(result.extractedText) {
        if (result.extractedText.contains("\n")) {
            result.extractedText.lines().filter { it.isNotBlank() }
        } else {
            // Smart word-wrap by words (never truncate words)
            val words = result.extractedText.split(" ")
            val list = mutableListOf<String>()
            var cur = StringBuilder()
            for (w in words) {
                if (cur.isNotEmpty() && cur.length + 1 + w.length > 30) {
                    list.add(cur.toString())
                    cur = StringBuilder(w)
                } else {
                    if (cur.isNotEmpty()) cur.append(" ")
                    cur.append(w)
                }
            }
            if (cur.isNotEmpty()) list.add(cur.toString())
            list
        }
    }

    // Cached rendered boxes for accurate hit testing
    var renderedBoxes by remember { mutableStateOf<List<RenderedBox>>(emptyList()) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(410.dp)
            .shadow(6.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .testTag("handwriting_canvas_card"),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFFDF8) // Warm Vietnamese notebook ivory paper
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .transformable(state = transformState)
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        translationX = offset.x,
                        translationY = offset.y
                    )
                    .pointerInput(renderedBoxes) {
                        detectTapGestures(
                            onDoubleTap = {
                                scale = 1f
                                offset = Offset.Zero
                            },
                            onTap = { tapOffset ->
                                val hit = renderedBoxes.firstOrNull { box ->
                                    box.rect.contains(tapOffset)
                                }
                                if (hit != null) {
                                    onSelectError(hit.error)
                                }
                            }
                        )
                    }
                    .testTag("interactive_canvas")
            ) {
                val canvasWidth = size.width
                val canvasHeight = size.height

                // 1. Draw Primary School Grid (Vở ô ly 4 ly / 5 ly)
                val gridSpacing = 28f
                val subGridColor = Color(0x183B82F6) // very faint cyan/blue subgrid
                val mainGridColor = Color(0x352563EB) // standard ruling line
                val marginColor = Color(0x99EF4444)   // red left margin

                // Vertical grid lines
                var x = 0f
                var colIndex = 0
                while (x < canvasWidth) {
                    val lineColor = if (colIndex % 4 == 0) mainGridColor else subGridColor
                    drawLine(
                        color = lineColor,
                        start = Offset(x, 0f),
                        end = Offset(x, canvasHeight),
                        strokeWidth = if (colIndex % 4 == 0) 1.2f else 0.6f
                    )
                    x += gridSpacing
                    colIndex++
                }

                // Horizontal grid lines
                var y = 0f
                var rowIndex = 0
                while (y < canvasHeight) {
                    val lineColor = if (rowIndex % 4 == 0) mainGridColor else subGridColor
                    drawLine(
                        color = lineColor,
                        start = Offset(0f, y),
                        end = Offset(canvasWidth, y),
                        strokeWidth = if (rowIndex % 4 == 0) 1.4f else 0.6f
                    )
                    y += gridSpacing
                    rowIndex++
                }

                // Red Margin Line (Standard Vietnamese Primary Notebook margin at left ~15% of width)
                val marginX = canvasWidth * 0.15f
                drawLine(
                    color = marginColor,
                    start = Offset(marginX, 0f),
                    end = Offset(marginX, canvasHeight),
                    strokeWidth = 2.5f
                )

                // 2. Native Text / Handwritten content rendering
                val nativePaint = android.graphics.Paint().apply {
                    color = android.graphics.Color.rgb(24, 43, 73) // Traditional blue fountain ink
                    textSize = 32f
                    isAntiAlias = true
                    typeface = android.graphics.Typeface.create(
                        android.graphics.Typeface.SERIF,
                        android.graphics.Typeface.NORMAL
                    )
                }

                val titlePaint = android.graphics.Paint().apply {
                    color = android.graphics.Color.rgb(185, 28, 28) // Red title ink
                    textSize = 34f
                    isAntiAlias = true
                    typeface = android.graphics.Typeface.create(
                        android.graphics.Typeface.SERIF,
                        android.graphics.Typeface.BOLD
                    )
                }

                // Render Essay Header on Paper
                drawContext.canvas.nativeCanvas.drawText(
                    "Thứ ... ngày ... tháng ... năm ...",
                    marginX + 20f,
                    52f,
                    nativePaint.apply { textSize = 24f }
                )
                drawContext.canvas.nativeCanvas.drawText(
                    result.essayTitle,
                    canvasWidth * 0.22f,
                    94f,
                    titlePaint
                )

                // Draw Handwritten Lines
                val textStartX = marginX + 24f
                val lineStartY = 145f
                val lineHeight = 44f

                nativePaint.apply { textSize = 30f }
                textLines.forEachIndexed { idx, lineStr ->
                    val lineY = lineStartY + idx * lineHeight
                    if (lineY < canvasHeight - 15f) {
                        drawContext.canvas.nativeCanvas.drawText(
                            lineStr,
                            textStartX,
                            lineY,
                            nativePaint
                        )
                    }
                }

                // 3. Compute and Render Error Bounding Boxes
                val currentBoxes = mutableListOf<RenderedBox>()

                result.errors.forEachIndexed { index, err ->
                    val isSelected = err.id == selectedErrorId

                    // Find line that matches the error word or use lineNumber
                    var matchedLineIdx = textLines.indexOfFirst { line ->
                        line.contains(err.originalWord, ignoreCase = true)
                    }
                    if (matchedLineIdx == -1 && err.lineNumber in 1..textLines.size) {
                        matchedLineIdx = err.lineNumber - 1
                    }

                    val left: Float
                    val top: Float
                    val right: Float
                    val bottom: Float

                    if (matchedLineIdx in textLines.indices) {
                        val lineStr = textLines[matchedLineIdx]
                        val wordIdx = lineStr.indexOf(err.originalWord, ignoreCase = true)
                        val baselineY = lineStartY + matchedLineIdx * lineHeight

                        if (wordIdx != -1) {
                            val prefix = lineStr.substring(0, wordIdx)
                            val prefixWidth = nativePaint.measureText(prefix)
                            val wordWidth = nativePaint.measureText(err.originalWord)

                            left = textStartX + prefixWidth - 6f
                            right = left + wordWidth + 12f
                            top = baselineY - 28f
                            bottom = baselineY + 10f
                        } else {
                            // Word across line or not found directly in line: use normalized bounds bounded to line
                            left = (err.x1 * canvasWidth).coerceIn(textStartX, canvasWidth - 60f)
                            right = (err.x2 * canvasWidth).coerceIn(left + 50f, canvasWidth - 20f)
                            top = baselineY - 28f
                            bottom = baselineY + 10f
                        }
                    } else {
                        left = err.x1 * canvasWidth
                        top = err.y1 * canvasHeight
                        right = err.x2 * canvasWidth
                        bottom = err.y2 * canvasHeight
                    }

                    val boxW = (right - left).coerceAtLeast(40f)
                    val boxH = (bottom - top).coerceAtLeast(24f)
                    val boxRect = Rect(left, top, right, bottom)
                    currentBoxes.add(RenderedBox(err, boxRect, index))

                    // Fill background of error
                    val fillColor = if (isSelected) {
                        Color(0xFFF59E0B).copy(alpha = pulseAlpha) // glowing amber for selected
                    } else {
                        Color(0xFFEF4444).copy(alpha = 0.12f) // light red
                    }
                    drawRoundRect(
                        color = fillColor,
                        topLeft = Offset(left, top),
                        size = Size(boxW, boxH),
                        cornerRadius = CornerRadius(6f, 6f)
                    )

                    // Outline of bounding box
                    val borderColor = if (isSelected) Color(0xFFD97706) else Color(0xFFDC2626)
                    val strokeWidth = if (isSelected) 3.5f else 2.0f
                    val pathEffect = if (isSelected) null else PathEffect.dashPathEffect(floatArrayOf(10f, 6f), 0f)

                    drawRoundRect(
                        color = borderColor,
                        topLeft = Offset(left, top),
                        size = Size(boxW, boxH),
                        cornerRadius = CornerRadius(6f, 6f),
                        style = Stroke(width = strokeWidth, pathEffect = pathEffect)
                    )

                    // Red teacher squiggly underline
                    val wavePath = Path()
                    var waveX = left + 2f
                    val waveY = bottom - 2f
                    val waveStep = 6f
                    var waveUp = true
                    wavePath.moveTo(waveX, waveY)
                    while (waveX < right - 2f) {
                        waveX += waveStep
                        val yOffset = if (waveUp) -3f else 3f
                        wavePath.lineTo(waveX, waveY + yOffset)
                        waveUp = !waveUp
                    }
                    drawPath(
                        path = wavePath,
                        color = if (isSelected) Color(0xFFD97706) else Color(0xFFDC2626),
                        style = Stroke(width = 2.0f)
                    )

                    // Number badge above bounding box [1], [2], [3]
                    val badgeX = left.coerceAtLeast(marginX + 5f)
                    val badgeY = (top - 20f).coerceAtLeast(10f)

                    drawRoundRect(
                        color = if (isSelected) Color(0xFFD97706) else Color(0xFFDC2626),
                        topLeft = Offset(badgeX, badgeY),
                        size = Size(36f, 20f),
                        cornerRadius = CornerRadius(4f, 4f)
                    )

                    val badgePaint = android.graphics.Paint().apply {
                        color = android.graphics.Color.WHITE
                        textSize = 18f
                        isAntiAlias = true
                        typeface = android.graphics.Typeface.DEFAULT_BOLD
                    }
                    drawContext.canvas.nativeCanvas.drawText(
                        "${index + 1}",
                        badgeX + 12f,
                        badgeY + 15f,
                        badgePaint
                    )
                }

                renderedBoxes = currentBoxes

                // 4. Grade Stamp (Dấu điểm tròn truyền thống)
                val stampCenterX = canvasWidth - 65f
                val stampCenterY = 65f
                val stampRadius = 42f

                // Outer red stamp ring
                drawCircle(
                    color = Color(0xFFDC2626),
                    radius = stampRadius,
                    center = Offset(stampCenterX, stampCenterY),
                    style = Stroke(width = 3.2f)
                )
                drawCircle(
                    color = Color(0x15DC2626),
                    radius = stampRadius,
                    center = Offset(stampCenterX, stampCenterY)
                )

                val stampScorePaint = android.graphics.Paint().apply {
                    color = android.graphics.Color.rgb(220, 38, 38)
                    textSize = 40f
                    isAntiAlias = true
                    textAlign = android.graphics.Paint.Align.CENTER
                    typeface = android.graphics.Typeface.create(
                        android.graphics.Typeface.SERIF,
                        android.graphics.Typeface.BOLD
                    )
                }
                drawContext.canvas.nativeCanvas.drawText(
                    "%.1f".format(result.criteria.totalScore),
                    stampCenterX,
                    stampCenterY + 12f,
                    stampScorePaint
                )
                drawContext.canvas.nativeCanvas.drawText(
                    "ĐIỂM",
                    stampCenterX,
                    stampCenterY - 18f,
                    stampScorePaint.apply { textSize = 16f }
                )
            }
        }
    }
}
