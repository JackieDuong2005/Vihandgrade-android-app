package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.SettingsInputAntenna
import androidx.compose.material.icons.filled.SyncAlt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ArchitectureDiagram(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Memory,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "KIẾN TRÚC TRIỂN KHAI THI ĐẤU EURÉKA",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Mô hình Edge Computing + 5G + Cloudflare",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Step 1: 5G Hotspot
            ArchNode(
                icon = Icons.Default.SettingsInputAntenna,
                title = "1. Điện Thoại Phát 5G Hotspot",
                subtitle = "Bật điểm phát sóng cá nhân nuôi mạng độc lập cho bàn thi",
                badgeColor = Color(0xFFF59E0B)
            )

            ArchConnector(label = "Bắt sóng Wifi 5G không phụ thuộc hạ tầng hội trường")

            // Step 2: Raspberry Pi
            ArchNode(
                icon = Icons.Default.Memory,
                title = "2. Trạm Server Nhúng (Raspberry Pi 4/5 ARM64)",
                subtitle = "• Next.js App Router (Port :3000)\n• Python FastAPI (Port :8000, ViT5 + YOLOv8)\n• SQLite Database (prisma/vihand.db)\n• Cloudflared Tunnel Daemon",
                badgeColor = Color(0xFFDC2626)
            )

            ArchConnector(label = "Đào hầm Cloudflare Tunnel bảo mật HTTPS, vượt CGNAT 5G")

            // Step 3: Cloudflare Domain
            ArchNode(
                icon = Icons.Default.Cloud,
                title = "3. Hệ Thống Cloudflare Toàn Cầu",
                subtitle = "Endpoint cố định: https://vihandgrade.click/api/grade\nKhông cần mở port modem, tự cấp SSL/TLS",
                badgeColor = Color(0xFF2563EB)
            )

            ArchConnector(label = "Gửi ảnh nén 1600px -> Nhận JSON điểm 4 tiêu chí")

            // Step 4: Android App
            ArchNode(
                icon = Icons.Default.PhoneAndroid,
                title = "4. Ứng Dụng Android (ViHand Grade APK)",
                subtitle = "• Do Thí sinh hoặc Ban Giám Khảo trực tiếp cầm\n• Chụp ảnh Camera native hoặc chọn bài mẫu\n• Canvas tương tác viền đỏ từ sai & phản hồi sư phạm",
                badgeColor = Color(0xFF059669)
            )
        }
    }
}

@Composable
private fun ArchNode(
    icon: ImageVector,
    title: String,
    subtitle: String,
    badgeColor: Color
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(badgeColor.copy(alpha = 0.15f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = badgeColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
private fun ArchConnector(label: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.SyncAlt,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
            fontSize = 11.sp
        )
    }
}
