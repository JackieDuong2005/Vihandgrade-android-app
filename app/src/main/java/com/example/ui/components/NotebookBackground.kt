package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ui.theme.AppTheme

/**
 * Elementary Notebook Paper Background (Nền vở ô ly học sinh tiểu học)
 * Renders an authentic educational notebook ruled texture with subtle margin guide and ambient glow.
 */
@Composable
fun NotebookBackground(
    modifier: Modifier = Modifier,
    showGrid: Boolean = true,
    content: @Composable BoxScope.() -> Unit
) {
    val bgColor = AppTheme.colors.background
    val isDark = bgColor.red < 0.2f

    val gridLineColor = if (isDark) {
        Color(0xFF334155).copy(alpha = 0.16f)
    } else {
        Color(0xFF64748B).copy(alpha = 0.05f) // Ultra-soft subtle ruling in light mode
    }

    val marginLineColor = if (isDark) {
        Color(0xFFEF4444).copy(alpha = 0.12f)
    } else {
        Color(0xFFEF4444).copy(alpha = 0.09f) // Soft warm red notebook margin
    }

    val glowColor = if (isDark) {
        Color(0xFF059669).copy(alpha = 0.08f)
    } else {
        Color(0xFF059669).copy(alpha = 0.03f)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(bgColor)
    ) {
        if (showGrid) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val step = 32.dp.toPx()
                val width = size.width
                val height = size.height

                // Ambient gradient at top
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(glowColor, Color.Transparent),
                        center = Offset(width * 0.5f, 0f),
                        radius = width * 0.8f
                    ),
                    size = size
                )

                // Horizontal ruled lines (Dòng kẻ ngang)
                var currentY = step
                while (currentY < height) {
                    drawLine(
                        color = gridLineColor,
                        start = Offset(0f, currentY),
                        end = Offset(width, currentY),
                        strokeWidth = 1f
                    )
                    currentY += step
                }

                // Vertical ruled lines (Dòng kẻ dọc tạo ô ly)
                var currentX = step
                while (currentX < width) {
                    drawLine(
                        color = gridLineColor,
                        start = Offset(currentX, 0f),
                        end = Offset(currentX, height),
                        strokeWidth = 0.8f
                    )
                    currentX += step
                }

                // Red margin line (Đường lề đỏ vở học sinh)
                val marginX = 24.dp.toPx()
                drawLine(
                    color = marginLineColor,
                    start = Offset(marginX, 0f),
                    end = Offset(marginX, height),
                    strokeWidth = 1.8f
                )
            }
        }

        content()
    }
}
