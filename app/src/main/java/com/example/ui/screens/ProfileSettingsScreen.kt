package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.api.UserData
import com.example.ui.theme.AppTheme
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.FontFamilyTieuHoc

/**
 * Màn hình Tài khoản & Cài đặt chuẩn hóa (Đồng bộ với Web ViHand Grade)
 * Loại bỏ toàn bộ thiết lập kỹ thuật SSH, ping, port, debug rườm rà.
 * Dành riêng cho trải nghiệm sư phạm của Giáo viên và Phụ huynh/Học sinh.
 */
@Composable
fun ProfileSettingsScreen(
    currentUser: UserData?,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    onLogout: () -> Unit,
    onBack: () -> Unit,
    serverUrl: String = "https://vihandgrade.click/",
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val isTeacher = currentUser?.role != "student"
    val displayName = currentUser?.name ?: if (isTeacher) "Cô giáo Nguyễn Mai" else "Học sinh"
    val roleLabel = if (isTeacher) "Giáo viên Tiểu học" else "Học sinh"
    val classOrSchool = currentUser?.className ?: if (isTeacher) "Trường Tiểu học Chuẩn Quốc Gia" else "Lớp 3A"

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AppTheme.colors.background)
            .statusBarsPadding()
    ) {
        // Top App Bar
        Surface(
            color = AppTheme.colors.surface,
            border = BorderStroke(1.dp, AppTheme.colors.border),
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Quay lại",
                        tint = AppTheme.colors.textPrimary
                    )
                }
                Text(
                    text = "Tài khoản & Cài đặt",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AppTheme.colors.textPrimary
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Card 1: User Profile Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AppTheme.colors.surface),
                border = BorderStroke(1.dp, AppTheme.colors.border),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Avatar Circle
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(EmeraldPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isTeacher) "GV" else "HS",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = displayName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AppTheme.colors.textPrimary
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = null,
                                tint = AppTheme.colors.textMuted,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "$roleLabel • $classOrSchool",
                                style = MaterialTheme.typography.bodySmall,
                                color = AppTheme.colors.textMuted
                            )
                        }
                    }
                }
            }

            // Card 2: Appearance & Theme
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AppTheme.colors.surface),
                border = BorderStroke(1.dp, AppTheme.colors.border),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Giao diện & Hiển thị",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = AppTheme.colors.textPrimary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onToggleTheme() },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isDarkTheme) Icons.Default.DarkMode else Icons.Default.LightMode,
                                contentDescription = null,
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (isDarkTheme) "Chế độ ban đêm (Dark)" else "Chế độ ban ngày (Light)",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = AppTheme.colors.textPrimary
                                )
                                Text(
                                    text = if (isDarkTheme) "Tông màu trầm dịu mắt" else "Tông be kem ấm áp như giấy vở",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = AppTheme.colors.textMuted
                                )
                            }
                        }

                        Switch(
                            checked = isDarkTheme,
                            onCheckedChange = { onToggleTheme() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = EmeraldPrimary,
                                uncheckedThumbColor = AppTheme.colors.textMuted,
                                uncheckedTrackColor = AppTheme.colors.border
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Font preview
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = AppTheme.colors.background,
                        border = BorderStroke(1.dp, AppTheme.colors.border),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Phông chữ Tiểu học HP001:",
                                style = MaterialTheme.typography.labelSmall,
                                color = AppTheme.colors.textMuted
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Nét chữ là nết người — ViHand Grade 2026",
                                fontFamily = FontFamilyTieuHoc,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = AppTheme.colors.textPrimary
                            )
                        }
                    }
                }
            }

            // Card 3: Cloud Synchronization Status
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AppTheme.colors.surface),
                border = BorderStroke(1.dp, AppTheme.colors.border),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Máy chủ & Dữ liệu đám mây",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = AppTheme.colors.textPrimary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(EmeraldPrimary)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Đang trực tuyến",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = EmeraldPrimary
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "vihandgrade.click (Cloudflare Edge)",
                                style = MaterialTheme.typography.bodySmall,
                                color = AppTheme.colors.textMuted
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Card 4: Logout Button
            Button(
                onClick = onLogout,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFEE2E2),
                    contentColor = Color(0xFFDC2626)
                ),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFFFCA5A5)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("btn_logout")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Đăng xuất tài khoản",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
