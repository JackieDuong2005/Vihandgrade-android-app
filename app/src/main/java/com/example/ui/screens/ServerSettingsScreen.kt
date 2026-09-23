package com.example.ui.screens

import android.speech.tts.TextToSpeech
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.components.ArchitectureDiagram
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.AccentCoral
import com.example.ui.theme.AccentSky
import com.example.ui.theme.AppTheme
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import java.util.Locale

@Composable
fun ServerSettingsScreen(
    serverUrl: String,
    pingStatus: Pair<Boolean?, String>,
    isPinging: Boolean,
    syncStatus: Pair<Boolean?, String> = Pair(null, "Chưa đồng bộ"),
    isSyncing: Boolean = false,
    localRecordsCount: Int = 0,
    photoCacheSizeBytes: Long = 0L,
    schoolName: String = "Trường Tiểu Học Kim Đồng",
    penaltyPerError: Float = 0.5f,
    autoEncouragement: Boolean = true,
    autoBoundingBox: Boolean = true,
    ttsVoice: String = "voice_female_north",
    ttsSpeed: Float = 1.0f,
    onSaveUrl: (String) -> Unit,
    onPing: () -> Unit,
    onSyncGrades: () -> Unit = {},
    onRefreshClassesAndStudents: () -> Unit = {},
    onClearLocalRecords: () -> Unit = {},
    onClearPhotoCache: () -> Unit = {},
    onUpdateSchoolName: (String) -> Unit = {},
    onUpdatePenaltyPerError: (Float) -> Unit = {},
    onUpdateAutoEncouragement: (Boolean) -> Unit = {},
    onUpdateAutoBoundingBox: (Boolean) -> Unit = {},
    onUpdateTtsVoice: (String) -> Unit = {},
    onUpdateTtsSpeed: (Float) -> Unit = {},
    currentUser: com.example.data.api.UserData? = null,
    onLogout: (() -> Unit)? = null,
    isDarkTheme: Boolean = true,
    onToggleTheme: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val primaryBrand = if (isDarkTheme) EmeraldPrimary else Color(0xFF059669)
    val darkEmeraldText = if (isDarkTheme) EmeraldPrimary else Color(0xFF047857)

    val context = LocalContext.current
    var inputUrl by remember { mutableStateOf(serverUrl) }
    var currentSchoolName by remember(schoolName) { mutableStateOf(schoolName) }
    var isEditingSchool by remember { mutableStateOf(false) }
    var showTechDialog by remember { mutableStateOf(false) }
    var cacheClearedMessage by remember { mutableStateOf<String?>(null) }

    // TTS instance for demo pronunciation
    var ttsInstance by remember { mutableStateOf<TextToSpeech?>(null) }
    DisposableEffect(context) {
        lateinit var tts: TextToSpeech
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts.language = Locale("vi", "VN")
            }
        }
        ttsInstance = tts
        onDispose {
            tts.stop()
            tts.shutdown()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AppTheme.colors.background)
            .statusBarsPadding()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(2.dp))

        // Screen Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = primaryBrand.copy(alpha = 0.15f),
                    modifier = Modifier.size(38.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null,
                            tint = darkEmeraldText,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Cài Đặt Sư Phạm",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = AppTheme.colors.textPrimary
                    )
                    Text(
                        text = "Cấu hình chuẩn Giáo dục Tiểu học",
                        style = MaterialTheme.typography.labelSmall,
                        color = AppTheme.colors.textMuted
                    )
                }
            }

            // Theme toggle button
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (isDarkTheme) Color(0xFF1E293B) else Color(0xFFE2E8F0),
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { onToggleTheme() }
                    .testTag("theme_switch_toggle")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isDarkTheme) Icons.Default.DarkMode else Icons.Default.LightMode,
                        contentDescription = null,
                        tint = darkEmeraldText,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = if (isDarkTheme) "Tối" else "Sáng",
                        style = MaterialTheme.typography.labelSmall,
                        color = AppTheme.colors.textPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // ==========================================
        // CARD 1: HỒ SƠ GIÁO VIÊN & LỚP PHỤ TRÁCH
        // ==========================================
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = AppTheme.colors.card),
            border = BorderStroke(1.dp, AppTheme.colors.border)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = primaryBrand.copy(alpha = 0.2f),
                        modifier = Modifier.size(46.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = primaryBrand,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = currentUser?.name ?: "Cô Nguyễn Thị Mai Hoa",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AppTheme.colors.textPrimary
                        )
                        Text(
                            text = "Giáo viên Chủ nhiệm • Năm học 2024 - 2025",
                            style = MaterialTheme.typography.bodySmall,
                            color = darkEmeraldText,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    if (onLogout != null) {
                        OutlinedButton(
                            onClick = onLogout,
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444)),
                            border = BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.4f)),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("Đăng xuất", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // School field
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = AppTheme.colors.cardElevated,
                    border = BorderStroke(1.dp, AppTheme.colors.border),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.School,
                                    contentDescription = null,
                                    tint = primaryBrand,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Đơn vị công tác / Trường học:",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = AppTheme.colors.textMuted
                                )
                            }
                            IconButton(
                                onClick = {
                                    if (isEditingSchool) {
                                        onUpdateSchoolName(currentSchoolName)
                                    }
                                    isEditingSchool = !isEditingSchool
                                },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = if (isEditingSchool) Icons.Default.Check else Icons.Default.Edit,
                                    contentDescription = null,
                                    tint = primaryBrand,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        if (isEditingSchool) {
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = currentSchoolName,
                                onValueChange = { currentSchoolName = it },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = primaryBrand,
                                    unfocusedBorderColor = AppTheme.colors.border
                                )
                            )
                        } else {
                            Text(
                                text = currentSchoolName,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = AppTheme.colors.textPrimary,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                }
            }
        }

        // ==========================================
        // CARD 2: CẤU HÌNH CHẤM ĐIỂM (THÔNG TƯ 27)
        // ==========================================
        SettingsGroup(
            title = "Quy Chuẩn Chấm Điểm (Thông Tư 27 Bộ GD&ĐT)",
            icon = Icons.Default.Tune
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                // Circular 27 Badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = primaryBrand.copy(alpha = 0.10f),
                    border = BorderStroke(1.dp, primaryBrand.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = darkEmeraldText,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Áp dụng Thông tư 27/2020/TT-BGDĐT đánh giá học sinh Tiểu học theo 3 mức Tốt, Hoàn thành và Chưa hoàn thành.",
                            style = MaterialTheme.typography.bodySmall,
                            color = darkEmeraldText,
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )
                    }
                }

                // 1. Mức trừ điểm mỗi lỗi chính tả (0.25đ vs 0.50đ)
                Column {
                    Text(
                        text = "Mức trừ điểm mỗi lỗi chính tả:",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = AppTheme.colors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val options = listOf(0.25f, 0.50f)
                        options.forEach { opt ->
                            val isSelected = (penaltyPerError == opt)
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) primaryBrand else AppTheme.colors.cardElevated,
                                border = BorderStroke(
                                    width = if (isSelected) 0.dp else 1.dp,
                                    color = AppTheme.colors.border
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onUpdatePenaltyPerError(opt) }
                                    .testTag("penalty_opt_${(opt * 100).toInt()}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${opt}đ / lỗi",
                                        fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                        color = if (isSelected) Color.White else AppTheme.colors.textPrimary,
                                        fontSize = 13.sp
                                    )
                                    if (isSelected) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(15.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Text(
                        text = if (penaltyPerError == 0.25f) "• Mức 0.25đ/lỗi phù hợp cho Lớp 1 & Lớp 2 (khuyến khích rèn luyện chữ)" else "• Mức 0.50đ/lỗi phù hợp cho Lớp 3, 4, 5 (chuẩn barem chấm thi)",
                        style = MaterialTheme.typography.labelSmall,
                        color = AppTheme.colors.textMuted,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                // 2. Tự động sinh lời nhận xét khích lệ
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Tự động nhận xét khích lệ",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = AppTheme.colors.textPrimary
                        )
                        Text(
                            text = "Gợi ý lời khen động viên theo tinh thần Thông tư 27",
                            style = MaterialTheme.typography.bodySmall,
                            color = AppTheme.colors.textMuted
                        )
                    }
                    Switch(
                        checked = autoEncouragement,
                        onCheckedChange = { onUpdateAutoEncouragement(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = primaryBrand,
                            checkedTrackColor = if (isDarkTheme) Color(0xFF065F46) else Color(0xFFA7F3D0)
                        )
                    )
                }

                // 3. Tự động vẽ Bounding Box
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Tự động vẽ Bounding Box trên ảnh",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = AppTheme.colors.textPrimary
                        )
                        Text(
                            text = "Khoanh màu trực tiếp các từ viết sai trên bài thật của học sinh",
                            style = MaterialTheme.typography.bodySmall,
                            color = AppTheme.colors.textMuted
                        )
                    }
                    Switch(
                        checked = autoBoundingBox,
                        onCheckedChange = { onUpdateAutoBoundingBox(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = primaryBrand,
                            checkedTrackColor = if (isDarkTheme) Color(0xFF065F46) else Color(0xFFA7F3D0)
                        )
                    )
                }

                // 4. Barem 4 Tiêu chí chuẩn MOET
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = AppTheme.colors.cardElevated,
                    border = BorderStroke(1.dp, AppTheme.colors.border),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Barem 4 Tiêu Chí Chấm Điểm Chuẩn (10 điểm):",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = darkEmeraldText
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("1. Chính tả & Chữ viết:", fontSize = 11.5.sp, color = AppTheme.colors.textPrimary)
                            Text("Tối đa 4.0đ", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = AccentCoral)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("2. Hình thức & Quy chuẩn:", fontSize = 11.5.sp, color = AppTheme.colors.textPrimary)
                            Text("Tối đa 3.0đ", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = AccentAmber)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("3. Nội dung & Diễn đạt:", fontSize = 11.5.sp, color = AppTheme.colors.textPrimary)
                            Text("Tối đa 2.0đ", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = AccentSky)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("4. Sáng tạo & Cảm xúc:", fontSize = 11.5.sp, color = AppTheme.colors.textPrimary)
                            Text("Tối đa 1.0đ", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = primaryBrand)
                        }
                    }
                }
            }
        }

        // ==========================================
        // CARD 3: ĐỒNG BỘ SỔ ĐIỂM & BỘ NHỚ ẢNH
        // ==========================================
        SettingsGroup(
            title = "Đồng Bộ Sổ Điểm & Bộ Nhớ Máy",
            icon = Icons.Default.CloudUpload
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Local records count
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Bài chấm đã lưu nội bộ:",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = AppTheme.colors.textPrimary
                        )
                        Text(
                            text = "Lưu an toàn trong bộ nhớ máy, chấm offline 100%",
                            style = MaterialTheme.typography.bodySmall,
                            color = AppTheme.colors.textMuted
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = primaryBrand.copy(alpha = 0.15f),
                        modifier = Modifier.padding(start = 6.dp)
                    ) {
                        Text(
                            text = "$localRecordsCount bài",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = darkEmeraldText,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                // Sync status badge
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = AppTheme.colors.cardElevated,
                    border = BorderStroke(1.dp, AppTheme.colors.border),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(
                                    when (syncStatus.first) {
                                        true -> primaryBrand
                                        false -> Color(0xFFEF4444)
                                        else -> Color(0xFFF59E0B)
                                    }
                                )
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = syncStatus.second,
                            style = MaterialTheme.typography.bodySmall,
                            color = AppTheme.colors.textPrimary
                        )
                    }
                }

                // Sync actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onSyncGrades,
                        enabled = !isSyncing,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryBrand),
                        modifier = Modifier.weight(1f).testTag("sync_grades_btn")
                    ) {
                        if (isSyncing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Đang đồng bộ...", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        } else {
                            Icon(
                                imageVector = Icons.Default.CloudDone,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Đồng bộ sổ điểm", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Button(
                        onClick = onRefreshClassesAndStudents,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AppTheme.colors.cardElevated),
                        border = BorderStroke(1.dp, AppTheme.colors.border)
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

                // Cache size & Cache cleanup
                val cacheMb = photoCacheSizeBytes / (1024f * 1024f)
                val cacheDisplay = if (cacheMb >= 0.1f) {
                    String.format("%.1f MB", cacheMb)
                } else {
                    "${photoCacheSizeBytes / 1024} KB"
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = AppTheme.colors.cardElevated,
                    border = BorderStroke(1.dp, AppTheme.colors.border),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Bộ nhớ đệm ảnh chụp:",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = AppTheme.colors.textPrimary
                            )
                            Text(
                                text = "Dung lượng hiện tại: $cacheDisplay",
                                style = MaterialTheme.typography.labelSmall,
                                color = AppTheme.colors.textMuted
                            )
                        }

                        OutlinedButton(
                            onClick = {
                                onClearPhotoCache()
                                cacheClearedMessage = "Đã dọn dẹp bộ nhớ đệm ảnh thành công!"
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = primaryBrand),
                            border = BorderStroke(1.dp, primaryBrand.copy(alpha = 0.5f)),
                            modifier = Modifier.height(34.dp).testTag("clear_cache_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.CleaningServices,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Dọn đệm", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                if (cacheClearedMessage != null) {
                    Text(
                        text = cacheClearedMessage!!,
                        style = MaterialTheme.typography.labelSmall,
                        color = darkEmeraldText,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Clear offline records (caution)
                if (localRecordsCount > 0) {
                    OutlinedButton(
                        onClick = onClearLocalRecords,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444)),
                        border = BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color(0xFFEF4444)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Xóa toàn bộ $localRecordsCount bài chấm nội bộ",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // ==========================================
        // CARD 4: KẾT NỐI MÁY CHỦ NHÀ TRƯỜNG
        // ==========================================
        SettingsGroup(
            title = "Máy Chủ Nhà Trường & Phòng Lab",
            icon = Icons.Default.Dns
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Địa chỉ máy chủ kết nối:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = AppTheme.colors.textPrimary
                )

                OutlinedTextField(
                    value = inputUrl,
                    onValueChange = { inputUrl = it },
                    singleLine = true,
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp,
                        color = darkEmeraldText
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("settings_server_url_input"),
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

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onSaveUrl(inputUrl) },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryBrand),
                        modifier = Modifier.weight(1f).testTag("save_url_btn")
                    ) {
                        Text(
                            "Lưu địa chỉ",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    Button(
                        onClick = onPing,
                        enabled = !isPinging,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AppTheme.colors.cardElevated),
                        border = BorderStroke(1.dp, AppTheme.colors.border),
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
                            text = if (isPinging) "Đang kiểm tra..." else "Kiểm tra kết nối",
                            color = AppTheme.colors.textPrimary,
                            fontSize = 11.sp
                        )
                    }
                }

                // Ping Status
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = AppTheme.colors.cardElevated,
                    border = BorderStroke(1.dp, AppTheme.colors.border),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Trạng thái:",
                            style = MaterialTheme.typography.labelSmall,
                            color = AppTheme.colors.textMuted
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = pingStatus.second,
                            style = MaterialTheme.typography.labelSmall,
                            color = when (pingStatus.first) {
                                true -> darkEmeraldText
                                false -> Color(0xFFEF4444)
                                else -> AppTheme.colors.textMuted
                            },
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        // ==========================================
        // CARD 5: GIỌNG ĐỌC AI PHÁT ÂM CHUẨN (TTS)
        // ==========================================
        SettingsGroup(
            title = "Giọng Đọc Phát Âm Chuẩn Tiếng Việt",
            icon = Icons.Default.VolumeUp
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Giúp học sinh nghe phát âm đúng từ viết sai khi chạm vào lỗi:",
                    style = MaterialTheme.typography.bodySmall,
                    color = AppTheme.colors.textMuted
                )

                // Voice selection
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val voices = listOf(
                        Pair("voice_female_north", "Cô Hoài My (Bắc)"),
                        Pair("voice_male_south", "Thầy Nam Minh (Nam)")
                    )
                    voices.forEach { (key, label) ->
                        val isSelected = (ttsVoice == key)
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) primaryBrand else AppTheme.colors.cardElevated,
                            border = BorderStroke(
                                width = if (isSelected) 0.dp else 1.dp,
                                color = AppTheme.colors.border
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onUpdateTtsVoice(key) }
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else AppTheme.colors.textPrimary,
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                // Speed selection
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val speeds = listOf(
                        Pair(0.8f, "Chậm (0.8x - Lớp 1, 2)"),
                        Pair(1.0f, "Bình thường (1.0x - Lớp 3-5)")
                    )
                    speeds.forEach { (spd, lbl) ->
                        val isSelected = (ttsSpeed == spd)
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) primaryBrand else AppTheme.colors.cardElevated,
                            border = BorderStroke(
                                width = if (isSelected) 0.dp else 1.dp,
                                color = AppTheme.colors.border
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onUpdateTtsSpeed(spd) }
                        ) {
                            Text(
                                text = lbl,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else AppTheme.colors.textPrimary,
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                // Sample pronunciation test button
                OutlinedButton(
                    onClick = {
                        ttsInstance?.setSpeechRate(ttsSpeed)
                        ttsInstance?.speak(
                            "Con chuồn chuồn bay lượn trên đồng lúa chín vàng",
                            TextToSpeech.QUEUE_FLUSH,
                            null,
                            "demo_tts"
                        )
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = primaryBrand),
                    border = BorderStroke(1.dp, primaryBrand.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Nghe thử phát âm mẫu câu Tiếng Việt",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // ==========================================
        // MỤC PHỤ: THÔNG TIN KỸ THUẬT (GIÁM KHẢO EURÉKA)
        // ==========================================
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = AppTheme.colors.card,
            border = BorderStroke(1.dp, AppTheme.colors.border),
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .clickable { showTechDialog = true }
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = darkEmeraldText,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Thông tin kỹ thuật chuyên sâu",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = AppTheme.colors.textPrimary
                        )
                        Text(
                            text = "Dành cho Hội đồng Giám khảo Giải thưởng Euréka",
                            style = MaterialTheme.typography.labelSmall,
                            color = AppTheme.colors.textMuted
                        )
                    }
                }

                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = primaryBrand,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    // Eureka Technical Details Dialog
    if (showTechDialog) {
        Dialog(onDismissRequest = { showTechDialog = false }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = AppTheme.colors.card,
                border = BorderStroke(1.dp, AppTheme.colors.border),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(18.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Kiến Trúc Kỹ Thuật ViHand",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = darkEmeraldText
                        )
                        IconButton(
                            onClick = { showTechDialog = false },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Text("✕", fontWeight = FontWeight.Bold, color = AppTheme.colors.textMuted)
                        }
                    }

                    // Hardware & AI models
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = AppTheme.colors.cardElevated,
                        border = BorderStroke(1.dp, AppTheme.colors.border),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            TechnicalRow("Thiết bị nhúng:", "Raspberry Pi 4 Model B (4GB RAM, ARM64)")
                            TechnicalRow("Mô hình AI:", "ViT5 INT8 Quantized + YOLOv8 Nano BBox")
                            TechnicalRow("Độ trễ xử lý:", "Khoảng 680ms / trang bài thi")
                            TechnicalRow("Bảo mật dữ liệu:", "Xử lý cục bộ Edge AI, không đưa dữ liệu ra ngoài")
                        }
                    }

                    // Architecture diagram
                    ArchitectureDiagram()

                    Button(
                        onClick = { showTechDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = primaryBrand),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Đóng cửa sổ", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun TechnicalRow(label: String, value: String) {
    Column {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = AppTheme.colors.textMuted)
        Text(text = value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, color = AppTheme.colors.textPrimary)
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
        border = BorderStroke(1.dp, AppTheme.colors.border)
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
