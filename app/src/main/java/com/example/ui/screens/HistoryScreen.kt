package com.example.ui.screens

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GradeResult
import com.example.ui.theme.AppTheme
import com.example.ui.theme.EmeraldPrimary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    records: List<GradeResult>,
    onSelectRecord: (GradeResult) -> Unit,
    onDeleteRecord: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy • HH:mm", Locale.getDefault())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lịch Sử Chấm Bài", fontWeight = FontWeight.Bold, color = AppTheme.colors.textPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("history_back_button")) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = AppTheme.colors.textPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppTheme.colors.card
                )
            )
        },
        containerColor = AppTheme.colors.background,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        if (records.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = AppTheme.colors.textMuted
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Chưa có bài thi nào được lưu",
                        style = MaterialTheme.typography.titleMedium,
                        color = AppTheme.colors.textPrimary
                    )
                    Text(
                        text = "Hãy chụp hoặc chọn một bài thi để bắt đầu chấm điểm",
                        style = MaterialTheme.typography.bodySmall,
                        color = AppTheme.colors.textMuted
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Tổng số ${records.size} bài thi đã lưu trong CSDL Room",
                        style = MaterialTheme.typography.labelMedium,
                        color = EmeraldPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                items(records, key = { it.id }) { record ->
                    HistoryItemCard(
                        record = record,
                        formattedDate = dateFormat.format(Date(record.timestamp)),
                        onClick = { onSelectRecord(record) },
                        onDelete = { onDeleteRecord(record.id) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun HistoryItemCard(
    record: GradeResult,
    formattedDate: String,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("history_item_${record.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AppTheme.colors.card),
        border = androidx.compose.foundation.BorderStroke(1.dp, AppTheme.colors.border)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = record.essayTitle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AppTheme.colors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${record.studentName} • ${record.className}",
                        style = MaterialTheme.typography.bodySmall,
                        color = AppTheme.colors.textMuted
                    )
                    Text(
                        text = formattedDate,
                        style = MaterialTheme.typography.labelSmall,
                        color = AppTheme.colors.textMuted,
                        fontSize = 11.sp
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = when {
                        record.criteria.totalScore >= 8.5f -> Color(0xFF059669)
                        record.criteria.totalScore >= 7.0f -> Color(0xFFD97706)
                        else -> Color(0xFFDC2626)
                    }
                ) {
                    Text(
                        text = "${"%.1f".format(record.criteria.totalScore)}đ",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Score Chips Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ScoreChip(label = "Chính tả", score = "${record.criteria.spellingScore}/4")
                ScoreChip(label = "Hình thức", score = "${record.criteria.formatScore}/3")
                ScoreChip(
                    label = "Lỗi sai",
                    score = "${record.errors.size}",
                    color = if (record.errors.isEmpty()) Color(0xFF059669) else Color(0xFFDC2626)
                )

                Spacer(modifier = Modifier.weight(1f))

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Xóa",
                        tint = AppTheme.colors.textMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ScoreChip(
    label: String,
    score: String,
    color: Color = EmeraldPrimary
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.12f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$label: ",
                style = MaterialTheme.typography.labelSmall,
                color = AppTheme.colors.textMuted
            )
            Text(
                text = score,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}
