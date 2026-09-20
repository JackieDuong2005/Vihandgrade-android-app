package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ArchitectureDiagram
import com.example.ui.components.NotebookBackground
import com.example.ui.theme.AppTheme
import com.example.ui.theme.EmeraldPrimary

import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Sync

@Composable
fun ServerSettingsScreen(
    serverUrl: String,
    pingStatus: Pair<Boolean?, String>,
    isPinging: Boolean,
    syncStatus: Pair<Boolean?, String> = Pair(null, "Chưa đồng bộ"),
    isSyncing: Boolean = false,
    localRecordsCount: Int = 0,
    onSaveUrl: (String) -> Unit,
    onPing: () -> Unit,
    onSyncGrades: () -> Unit = {},
    onRefreshClassesAndStudents: () -> Unit = {},
    currentUser: com.example.data.api.UserData? = null,
    onLogout: (() -> Unit)? = null,
    isDarkTheme: Boolean = true,
    onToggleTheme: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val primaryBrand = if (isDarkTheme) EmeraldPrimary else Color(0xFF059669)
    val darkEmeraldText = if (isDarkTheme) EmeraldPrimary else Color(0xFF047857)

    var inputUrl by remember { mutableStateOf(serverUrl) }
    var autoDrawBbox by remember { mutableStateOf(true) }
    var qwenFeedback by remember { mutableStateOf(true) }

    NotebookBackground(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
        Spacer(modifier = Modifier.height(4.dp))

        // Screen Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    tint = darkEmeraldText,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Cài Đặt Hệ Thống",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = AppTheme.colors.textPrimary
                )
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isDarkTheme) Color(0xFF1E293B) else Color(0xFFE2E8F0),
                modifier = Modifier.padding(2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isDarkTheme) Icons.Default.DarkMode else Icons.Default.LightMode,
                        contentDescription = null,
                        tint = darkEmeraldText,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isDarkTheme) "Tối" else "Sáng",
                        style = MaterialTheme.typography.labelSmall,
                        color = AppTheme.colors.textPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Group 0: Giao diện sáng / tối (Theme switcher)
        SettingsGroup(
            title = "Giao Diện Ứng Dụng",
            icon = Icons.Default.Palette
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Chế độ giao diện Tối (Dark mode)",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = AppTheme.colors.textPrimary
                    )
                    Text(
                        text = if (isDarkTheme) "Đang bật chế độ nền tối (Dark Slate)" else "Đang bật chế độ nền sáng (Light Clean)",
                        style = MaterialTheme.typography.bodySmall,
                        color = AppTheme.colors.textMuted
                    )
                }
                Switch(
                    checked = isDarkTheme,
                    onCheckedChange = { onToggleTheme() },
                    modifier = Modifier.testTag("theme_switch_toggle"),
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = primaryBrand,
                        checkedTrackColor = if (isDarkTheme) Color(0xFF065F46) else Color(0xFFA7F3D0),
                        uncheckedThumbColor = Color(0xFF94A3B8),
                        uncheckedTrackColor = Color(0xFFCBD5E1)
                    )
                )
            }
        }

        // Group 1: Địa chỉ kết nối hệ thống
        SettingsGroup(
            title = "Địa Chỉ Kết Nối Hệ Thống",
            icon = Icons.Default.Dns
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Cloudflare Tunnel URL",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = AppTheme.colors.textPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = inputUrl,
                    onValueChange = { inputUrl = it },
                    singleLine = true,
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp,
                        color = darkEmeraldText
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("settings_server_url_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = primaryBrand,
                        unfocusedBorderColor = AppTheme.colors.border,
                        focusedContainerColor = AppTheme.colors.cardElevated,
                        unfocusedContainerColor = AppTheme.colors.cardElevated
                    ),
                    trailingIcon = {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(primaryBrand, CircleShape)
                        )
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = { onSaveUrl(inputUrl) },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryBrand),
                        modifier = Modifier.testTag("save_url_btn")
                    ) {
                        Text(
                            "Lưu URL",
                            color = if (isDarkTheme) Color(0xFF064E3B) else Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Ping Status Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Độ trễ phản hồi (Ping)",
                            style = MaterialTheme.typography.bodySmall,
                            color = AppTheme.colors.textPrimary,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = pingStatus.second,
                            style = MaterialTheme.typography.labelSmall,
                            color = when (pingStatus.first) {
                                true -> darkEmeraldText
                                false -> Color(0xFFEF4444)
                                else -> AppTheme.colors.textMuted
                            },
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Button(
                        onClick = onPing,
                        enabled = !isPinging,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AppTheme.colors.cardElevated),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AppTheme.colors.border),
                        modifier = Modifier.testTag("ping_test_btn")
                    ) {
                        if (isPinging) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(14.dp),
                                strokeWidth = 2.dp,
                                color = primaryBrand
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                tint = darkEmeraldText,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isPinging) "Đang ping..." else "Test Ping",
                            color = AppTheme.colors.textPrimary,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Group: Đồng bộ sổ điểm về trường (Cloud / Pi Sync - Phase 5)
        SettingsGroup(
            title = "Đồng Bộ Sổ Điểm Về Trường",
            icon = Icons.Default.CloudUpload
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Đẩy các bài chấm offline lưu trên điện thoại lên cơ sở dữ liệu vihand.db của trường.",
                    style = MaterialTheme.typography.bodySmall,
                    color = AppTheme.colors.textMuted
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Số bài chấm nội bộ:",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = AppTheme.colors.textPrimary
                    )
                    Text(
                        text = "$localRecordsCount bài sẵn sàng",
                        style = MaterialTheme.typography.bodyMedium,
                        color = primaryBrand,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Trạng thái đồng bộ
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = AppTheme.colors.cardElevated,
                    border = androidx.compose.foundation.BorderStroke(1.dp, AppTheme.colors.border),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(
                                    when (syncStatus.first) {
                                        true -> primaryBrand
                                        false -> Color(0xFFEF4444)
                                        else -> Color(0xFFF59E0B)
                                    }
                                )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = syncStatus.second,
                            style = MaterialTheme.typography.bodySmall,
                            color = AppTheme.colors.textPrimary
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Nút bắt đầu đồng bộ
                    Button(
                        onClick = onSyncGrades,
                        enabled = !isSyncing,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryBrand),
                        modifier = Modifier.weight(1f)
                    ) {
                        if (isSyncing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = if (isDarkTheme) Color(0xFF064E3B) else Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Đang tải sổ điểm...", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        } else {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Đồng bộ sổ điểm", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Nút cập nhật danh sách lớp / học sinh
                    Button(
                        onClick = onRefreshClassesAndStudents,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AppTheme.colors.cardElevated),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AppTheme.colors.border)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sync,
                            contentDescription = null,
                            tint = primaryBrand,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Cập nhật lớp", color = AppTheme.colors.textPrimary, fontSize = 12.sp)
                    }
                }
            }
        }

        // Group 2: Thông số phần cứng nhúng
        SettingsGroup(
            title = "Thông Số Phần Cứng Nhúng",
            icon = Icons.Default.Layers
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                HardwareParamRow("Thiết bị máy chủ", "Raspberry Pi 4 Model B (4GB ARM64)")
                HardwareParamRow("Đường truyền mạng", "5G Hotspot cá nhân (Gian hàng Euréka)")
                HardwareParamRow("Mô hình AI trên máy", "ViT5 Quantized INT8 + YOLOv8 Nano")
            }
        }

        // Group 3: Tùy chọn chấm điểm
        SettingsGroup(
            title = "Tùy Chọn Chấm Điểm",
            icon = Icons.Default.Tune
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Tự động vẽ Bounding Box",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = AppTheme.colors.textPrimary
                        )
                        Text(
                            text = "Khoanh đỏ các từ sai trên ảnh",
                            style = MaterialTheme.typography.bodySmall,
                            color = AppTheme.colors.textMuted
                        )
                    }
                    Switch(
                        checked = autoDrawBbox,
                        onCheckedChange = { autoDrawBbox = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = primaryBrand,
                            checkedTrackColor = if (isDarkTheme) Color(0xFF065F46) else Color(0xFFA7F3D0)
                        )
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Sinh lời nhận xét Qwen SLM",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = AppTheme.colors.textPrimary
                        )
                        Text(
                            text = "Phân tích sư phạm theo cấp tiểu học",
                            style = MaterialTheme.typography.bodySmall,
                            color = AppTheme.colors.textMuted
                        )
                    }
                    Switch(
                        checked = qwenFeedback,
                        onCheckedChange = { qwenFeedback = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = primaryBrand,
                            checkedTrackColor = if (isDarkTheme) Color(0xFF065F46) else Color(0xFFA7F3D0)
                        )
                    )
                }
            }
        }

        // Account Session Card
        if (currentUser != null || onLogout != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AppTheme.colors.card),
                border = androidx.compose.foundation.BorderStroke(1.dp, AppTheme.colors.border)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "TÀI KHOẢN HIỆN TẠI",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = darkEmeraldText,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = currentUser?.name ?: "Người dùng ViHand",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = AppTheme.colors.textPrimary
                            )
                            Text(
                                text = "Vai trò: ${if (currentUser?.role == "student") "Học sinh" else "Giáo viên"}${if (!currentUser?.className.isNullOrBlank()) " (${currentUser?.className})" else ""}",
                                style = MaterialTheme.typography.bodySmall,
                                color = AppTheme.colors.textMuted
                            )
                        }

                        if (onLogout != null) {
                            androidx.compose.material3.OutlinedButton(
                                onClick = onLogout,
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color(0xFFEF4444)
                                ),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.5f))
                            ) {
                                Text("Đăng xuất", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }

        // Architecture Diagram Card
        ArchitectureDiagram()

        Spacer(modifier = Modifier.height(20.dp))
    }
}
}

@Composable
private fun SettingsGroup(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    val darkEmeraldText = if (AppTheme.colors.isDark) EmeraldPrimary else Color(0xFF047857)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AppTheme.colors.card),
        border = androidx.compose.foundation.BorderStroke(1.dp, AppTheme.colors.border)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = darkEmeraldText,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = darkEmeraldText,
                    letterSpacing = 0.5.sp
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
            content()
        }
    }
}

@Composable
private fun HardwareParamRow(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = AppTheme.colors.textMuted
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = AppTheme.colors.textPrimary
        )
    }
}
