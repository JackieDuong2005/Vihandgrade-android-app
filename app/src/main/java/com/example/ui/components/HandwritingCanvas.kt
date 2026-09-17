package com.example.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ErrorBox
import com.example.data.model.GradeResult

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

    // Magnifier Loupe state
    var isLoupeActive by remember { mutableStateOf(false) }
    var loupeOffset by remember { mutableStateOf(Offset(220f, 180f)) }

    val transformState = rememberTransformableState { zoomChange, offsetChange, _ ->
        scale = (scale * zoomChange).coerceIn(0.85f, 3.0f)
        offset += offsetChange
    }

    // Pulse animation for selected error box
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_handwriting")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(900),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    // Cleanly parse lines of essay text
    val textLines = remember(result.extractedText) {
        if (result.extractedText.contains("\n")) {
            result.extractedText.lines().filter { it.isNotBlank() }
        } else {
            val words = result.extractedText.split(" ")
            val list = mutableListOf<String>()
            var cur = StringBuilder()
            for (w in words) {
                if (cur.isNotEmpty() && cur.length + 1 + w.length > 28) {
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

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(430.dp)
            .shadow(6.dp, RoundedCornerShape(18.dp))
            .clip(RoundedCornerShape(18.dp))
            .testTag("handwriting_canvas_card"),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFFDF8) // Warm Vietnamese student notebook ivory paper
        ),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .transformable(state = transformState)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onDoubleTap = {
                            scale = 1f
                            offset = Offset.Zero
                        }
                    )
                }
        ) {
            // 1. Vietnamese Primary School Notebook Grid Canvas (Vở ô ly 4 ly)
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        translationX = offset.x,
                        translationY = offset.y
                    )
            ) {
                val w = size.width
                val h = size.height
                val step = 26.dp.toPx()
                val subGridColor = Color(0x2038BDF8) // Faint sky blue subgrid
                val mainGridColor = Color(0x400284C7) // Standard ruling line
                val marginLineColor = Color(0x99EF4444) // Red primary school margin line

                // Vertical grid lines
                var x = step
                var colIdx = 1
                while (x < w) {
                    val color = if (colIdx % 4 == 0) mainGridColor else subGridColor
                    val stroke = if (colIdx % 4 == 0) 1.2f else 0.6f
                    drawLine(color, Offset(x, 0f), Offset(x, h), strokeWidth = stroke)
                    x += step
                    colIdx++
                }

                // Horizontal grid lines
                var y = step
                var rowIdx = 1
                while (y < h) {
                    val color = if (rowIdx % 4 == 0) mainGridColor else subGridColor
                    val stroke = if (rowIdx % 4 == 0) 1.4f else 0.6f
                    drawLine(color, Offset(0f, y), Offset(w, y), strokeWidth = stroke)
                    y += step
                    rowIdx++
                }

                // Left red vertical margin line at 44.dp
                val marginX = 44.dp.toPx()
                drawLine(marginLineColor, Offset(marginX, 0f), Offset(marginX, h), strokeWidth = 2.dp.toPx())
            }

            // 2. Notebook Page Content Layer (Zooms and pans in sync)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        translationX = offset.x,
                        translationY = offset.y
                    )
                    .padding(start = 50.dp, top = 14.dp, end = 16.dp, bottom = 14.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Top Date & Title Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Thứ ... ngày ... tháng ... năm ...",
                                fontSize = 11.sp,
                                fontStyle = FontStyle.Italic,
                                fontFamily = FontFamily.Serif,
                                color = Color(0xFF64748B)
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = result.essayTitle,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Serif,
                                color = Color(0xFF1E3A8A),
                                letterSpacing = 0.3.sp
                            )
                            Text(
                                text = "${result.studentName} — ${result.className}",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Serif,
                                color = Color(0xFF475569)
                            )
                        }

                        // Official Red Teacher Grade Stamp (Dấu điểm tròn mực đỏ)
                        TeacherGradeStamp(
                            score = result.criteria.totalScore,
                            modifier = Modifier
                                .offset(x = (-4).dp, y = (-2).dp)
                                .rotate(-6f)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Essay Text Lines with Interactive Error Highlights
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        textLines.forEachIndexed { lineIdx, lineStr ->
                            val currentLineNumber = lineIdx + 1

                            // Check if any error is located on this line
                            val errorOnLine = result.errors.firstOrNull { err ->
                                lineStr.contains(err.originalWord, ignoreCase = true) || err.lineNumber == currentLineNumber
                            }

                            if (errorOnLine != null && lineStr.contains(errorOnLine.originalWord, ignoreCase = true)) {
                                val errIndexInFullList = result.errors.indexOf(errorOnLine)
                                val isSelected = errorOnLine.id == selectedErrorId

                                // Split line around the error word
                                val word = errorOnLine.originalWord
                                val splitIndex = lineStr.indexOf(word, ignoreCase = true)
                                val before = lineStr.substring(0, splitIndex)
                                val after = lineStr.substring(splitIndex + word.length)

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    if (before.isNotEmpty()) {
                                        Text(
                                            text = before,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            fontFamily = FontFamily.Serif,
                                            color = Color(0xFF1E40AF),
                                            letterSpacing = 0.3.sp
                                        )
                                    }

                                    // Interactive Error Box
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (isSelected) Color(0xFFFEF3C7).copy(alpha = pulseAlpha) else Color(0xFFFEE2E2).copy(alpha = 0.85f),
                                        border = BorderStroke(
                                            if (isSelected) 2.dp else 1.2.dp,
                                            if (isSelected) Color(0xFFD97706) else Color(0xFFDC2626)
                                        ),
                                        modifier = Modifier
                                            .padding(horizontal = 3.dp)
                                            .clickable { onSelectError(errorOnLine) }
                                            .testTag("error_box_${errorOnLine.id}")
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                // Number badge [1], [2]
                                                Surface(
                                                    shape = CircleShape,
                                                    color = if (isSelected) Color(0xFFD97706) else Color(0xFFDC2626),
                                                    modifier = Modifier.size(16.dp)
                                                ) {
                                                    Box(contentAlignment = Alignment.Center) {
                                                        Text(
                                                            text = "${errIndexInFullList + 1}",
                                                            color = Color.White,
                                                            fontSize = 9.sp,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                    }
                                                }
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = word,
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    fontFamily = FontFamily.Serif,
                                                    color = if (isSelected) Color(0xFF92400E) else Color(0xFFB91C1C),
                                                    letterSpacing = 0.2.sp
                                                )
                                            }

                                            // Teacher red wavy underline canvas
                                            TeacherWavyUnderline(
                                                color = if (isSelected) Color(0xFFD97706) else Color(0xFFDC2626),
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(3.dp)
                                            )
                                        }
                                    }

                                    if (after.isNotEmpty()) {
                                        Text(
                                            text = after,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            fontFamily = FontFamily.Serif,
                                            color = Color(0xFF1E40AF),
                                            letterSpacing = 0.3.sp
                                        )
                                    }
                                }
                            } else {
                                // Normal neat student handwritten line
                                Text(
                                    text = lineStr,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    fontFamily = FontFamily.Serif,
                                    color = Color(0xFF1E40AF),
                                    lineHeight = 24.sp,
                                    letterSpacing = 0.3.sp
                                )
                            }
                        }
                    }
                }
            }

            // 3. Floating Interactive Zoom Controls Bar (bottom right)
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xEEFFFFFF),
                border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                shadowElevation = 4.dp,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    IconButton(
                        onClick = { scale = (scale + 0.25f).coerceAtMost(3.0f) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Phóng to",
                            tint = Color(0xFF047857),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Text(
                        text = "${(scale * 100).toInt()}%",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF334155),
                        modifier = Modifier.padding(horizontal = 2.dp)
                    )

                    IconButton(
                        onClick = { scale = (scale - 0.25f).coerceAtLeast(0.85f) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Thu nhỏ",
                            tint = Color(0xFF047857),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = {
                            scale = 1f
                            offset = Offset.Zero
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Đặt lại tỉ lệ",
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Magnifier Loupe Toggle Button
                    IconButton(
                        onClick = { isLoupeActive = !isLoupeActive },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = if (isLoupeActive) "Tắt kính lúp" else "Bật kính lúp soi nét chữ",
                            tint = if (isLoupeActive) Color(0xFF059669) else Color(0xFF64748B),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Interactive Magnifier Loupe Lens Layer (x2.2 Magnification)
            if (isLoupeActive) {
                Box(
                    modifier = Modifier
                        .offset { androidx.compose.ui.unit.IntOffset(loupeOffset.x.toInt() - 62, loupeOffset.y.toInt() - 62) }
                        .size(124.dp)
                        .shadow(12.dp, CircleShape)
                        .clip(CircleShape)
                        .background(Color(0xFFFFFDF8))
                        .border(3.5.dp, Color(0xFF059669), CircleShape)
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                loupeOffset += dragAmount
                            }
                        }
                        .testTag("magnifier_loupe")
                ) {
                    // Magnified canvas content with 2.2x scale centered at loupe position
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer(
                                scaleX = 2.2f,
                                scaleY = 2.2f,
                                translationX = -(loupeOffset.x * 1.2f),
                                translationY = -(loupeOffset.y * 1.2f)
                            )
                    ) {
                        val w = size.width * 2f
                        val h = size.height * 2f
                        val step = 26.dp.toPx()
                        var x = step
                        while (x < w) {
                            drawLine(Color(0x300284C7), Offset(x, 0f), Offset(x, h), strokeWidth = 1f)
                            x += step
                        }
                        var y = step
                        while (y < h) {
                            drawLine(Color(0x300284C7), Offset(0f, y), Offset(w, y), strokeWidth = 1f)
                            y += step
                        }
                    }

                    // Centered Loupe Crosshair Reticle
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val cx = size.width / 2f
                        val cy = size.height / 2f
                        drawLine(Color(0x88DC2626), Offset(cx - 14f, cy), Offset(cx + 14f, cy), strokeWidth = 1.5f)
                        drawLine(Color(0x88DC2626), Offset(cx, cy - 14f), Offset(cx, cy + 14f), strokeWidth = 1.5f)
                        drawCircle(Color(0x33DC2626), radius = 10f, center = Offset(cx, cy))
                    }

                    // Floating text label on Loupe
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xDD064E3B),
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 6.dp)
                    ) {
                        Text(
                            text = "Kính lúp x2.2",
                            color = Color(0xFFA7F3D0),
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    // Close loupe button
                    IconButton(
                        onClick = { isLoupeActive = false },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(26.dp)
                            .padding(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Tắt kính lúp",
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            // 4. Subtle Hint Pill at Bottom Left
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xDDF8FAFC),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(10.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.TouchApp,
                        contentDescription = null,
                        tint = Color(0xFFD97706),
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Chạm từ đỏ để xem sửa",
                        fontSize = 10.sp,
                        color = Color(0xFF475569),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

/**
 * TeacherGradeStamp:
 * Traditional red circular grade stamp with double border.
 */
@Composable
private fun TeacherGradeStamp(
    score: Float,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(72.dp)
            .border(2.5.dp, Color(0xFFDC2626), CircleShape)
            .padding(3.dp)
            .border(1.2.dp, Color(0xFFDC2626), CircleShape)
            .background(Color(0x0FDC2626), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "ĐIỂM",
                color = Color(0xFFDC2626),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Text(
                text = "%.1f".format(score),
                color = Color(0xFFDC2626),
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Serif
            )
            Text(
                text = "ViHand AI",
                color = Color(0xFFDC2626),
                fontSize = 8.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

/**
 * TeacherWavyUnderline:
 * Red squiggly wavy underline under misspelled words.
 */
@Composable
private fun TeacherWavyUnderline(
    color: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val path = Path()
        var curX = 0f
        val step = 4.dp.toPx()
        var up = true
        path.moveTo(0f, size.height / 2)
        while (curX < w) {
            curX += step
            val yOffset = if (up) 0f else size.height
            path.lineTo(curX, yOffset)
            up = !up
        }
        drawPath(path, color = color, style = Stroke(width = 1.8f))
    }
}
