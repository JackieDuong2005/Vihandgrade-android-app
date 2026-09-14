package com.example.ui.screens

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.PhotoLibrary
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EmeraldPrimary

@Composable
fun CameraScanScreen(
    onCapture: (Bitmap) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isFlashOn by remember { mutableStateOf(false) }

    // Laser scan animation
    val infiniteTransition = rememberInfiniteTransition(label = "laser")
    val laserProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laserProgress"
    )

    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            onCapture(bitmap)
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

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("camera_scan_screen")
    ) {
        // Simulated or Live Notebook paper preview background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0F172A))
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                // Background grid pattern representing notebook
                val spacing = 32f
                var x = 0f
                while (x < size.width) {
                    drawLine(
                        color = Color(0x1538BDF8),
                        start = Offset(x, 0f),
                        end = Offset(x, size.height),
                        strokeWidth = 1f
                    )
                    x += spacing
                }
                var y = 0f
                while (y < size.height) {
                    drawLine(
                        color = Color(0x1538BDF8),
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = 1f
                    )
                    y += spacing
                }
            }
        }

        // Viewfinder Box with 4 corners
        Box(
            modifier = Modifier
                .fillMaxWidth(0.88f)
                .height(420.dp)
                .align(Alignment.Center)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0x221E293B))
                .border(1.dp, Color(0x3334D399), RoundedCornerShape(16.dp))
        ) {
            // Viewfinder Corners
            ViewfinderCorner(Modifier.align(Alignment.TopStart), isTop = true, isLeft = true)
            ViewfinderCorner(Modifier.align(Alignment.TopEnd), isTop = true, isLeft = false)
            ViewfinderCorner(Modifier.align(Alignment.BottomStart), isTop = false, isLeft = true)
            ViewfinderCorner(Modifier.align(Alignment.BottomEnd), isTop = false, isLeft = false)

            // Animated Laser Scanning Line
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.5.dp)
                    .offset(y = (420.dp * laserProgress) - 1.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color.Transparent,
                                EmeraldPrimary.copy(alpha = 0.4f),
                                EmeraldPrimary,
                                EmeraldPrimary.copy(alpha = 0.4f),
                                Color.Transparent
                            )
                        )
                    )
            )

            // Gyroscope Indicator at bottom of viewfinder
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xCC0B1120),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x4434D399)),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
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
                        text = "Góc chụp chuẩn 90° • Không bóng tay",
                        style = MaterialTheme.typography.labelSmall,
                        color = EmeraldPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Top Controls Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .size(42.dp)
                    .background(Color(0x660B1120), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Đóng",
                    tint = Color.White
                )
            }

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xAA0B1120),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x3334D399))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Layers,
                        contentDescription = null,
                        tint = EmeraldPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Tự Động Căn Lề Ô Ly",
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            IconButton(
                onClick = { isFlashOn = !isFlashOn },
                modifier = Modifier
                    .size(42.dp)
                    .background(Color(0x660B1120), CircleShape)
            ) {
                Icon(
                    imageVector = if (isFlashOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                    contentDescription = "Flash",
                    tint = if (isFlashOn) Color(0xFFF59E0B) else Color.White
                )
            }
        }

        // Bottom Shutter & Controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(bottom = 24.dp, start = 32.dp, end = 32.dp)
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Gallery thumbnail button
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(Color(0x881E293B))
                    .border(1.5.dp, Color(0x44FFFFFF), CircleShape)
                    .clickable { galleryLauncher.launch("image/*") }
                    .testTag("gallery_picker_btn"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PhotoLibrary,
                    contentDescription = "Chọn từ thư viện",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Main Shutter Button
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .border(4.dp, EmeraldPrimary, CircleShape)
                    .clickable { cameraLauncher.launch(null) }
                    .testTag("shutter_btn"),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(66.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                )
            }

            // Switch camera button
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(Color(0x881E293B))
                    .border(1.5.dp, Color(0x44FFFFFF), CircleShape)
                    .clickable { cameraLauncher.launch(null) },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Cameraswitch,
                    contentDescription = "Đổi Camera",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun ViewfinderCorner(
    modifier: Modifier = Modifier,
    isTop: Boolean,
    isLeft: Boolean
) {
    Canvas(
        modifier = modifier
            .size(28.dp)
            .padding(4.dp)
    ) {
        val stroke = 3.5f
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
