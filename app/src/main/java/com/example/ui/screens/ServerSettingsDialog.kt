package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.api.NetworkClient

@Composable
fun ServerSettingsDialog(
    currentUrl: String,
    pingStatus: Pair<Boolean?, String>,
    isPinging: Boolean,
    onSaveUrl: (String) -> Unit,
    onPing: () -> Unit,
    onDismiss: () -> Unit
) {
    var inputUrl by remember { mutableStateOf(currentUrl) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Dns,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text("Cấu Hình Trạm Raspberry Pi", style = MaterialTheme.typography.titleMedium)
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Mặc định sử dụng Cloudflare Tunnel cố định theo kịch bản thi đấu Euréka:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = inputUrl,
                    onValueChange = { inputUrl = it },
                    label = { Text("Server API Endpoint") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("server_url_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Quick buttons for defaults
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(
                        onClick = { inputUrl = NetworkClient.DEFAULT_BASE_URL },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cloudflare URL", fontSize = 12.sp)
                    }
                    TextButton(
                        onClick = { inputUrl = "http://192.168.43.100:3000" },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("IP Local Hotspot", fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Ping Status Box
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = when (pingStatus.first) {
                        true -> Color(0xFFECFDF5)
                        false -> Color(0xFFFEF2F2)
                        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isPinging) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        } else {
                            Icon(
                                imageVector = when (pingStatus.first) {
                                    true -> Icons.Default.CheckCircle
                                    false -> Icons.Default.Error
                                    else -> Icons.Default.Cloud
                                },
                                contentDescription = null,
                                tint = when (pingStatus.first) {
                                    true -> Color(0xFF059669)
                                    false -> Color(0xFFDC2626)
                                    else -> MaterialTheme.colorScheme.primary
                                },
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = pingStatus.second,
                            style = MaterialTheme.typography.bodySmall,
                            color = when (pingStatus.first) {
                                true -> Color(0xFF065F46)
                                false -> Color(0xFF991B1B)
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = onPing,
                    enabled = !isPinging,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("ping_test_button"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.NetworkCheck, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (isPinging) "Đang kiểm tra..." else "Kiểm tra Ping đến Raspberry Pi")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSaveUrl(inputUrl)
                    onDismiss()
                },
                modifier = Modifier.testTag("save_server_button")
            ) {
                Text("Lưu cấu hình")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Đóng")
            }
        }
    )
}
