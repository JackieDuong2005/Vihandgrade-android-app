package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.FormatShapes
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Spellcheck
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GradeCriteria

@Composable
fun CriteriaScoreCard(
    criteria: GradeCriteria,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("criteria_score_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header: Total Score & Rating Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "BẢNG ĐIỂM TIÊU CHÍ (4 TIÊU CHÍ)",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = criteria.ratingLevel,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = when {
                            criteria.totalScore >= 8.5f -> Color(0xFF047857)
                            criteria.totalScore >= 7.0f -> Color(0xFFD97706)
                            else -> Color(0xFFDC2626)
                        }
                    )
                }

                // Total Score Badge
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = when {
                        criteria.totalScore >= 8.5f -> Color(0xFF059669)
                        criteria.totalScore >= 7.0f -> Color(0xFFD97706)
                        else -> Color(0xFFDC2626)
                    },
                    modifier = Modifier.testTag("total_score_badge")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = "%.1f".format(criteria.totalScore),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Text(
                            text = " /10",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.85f),
                            modifier = Modifier.padding(bottom = 3.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 4 Criteria Breakdown
            CriteriaProgressItem(
                icon = Icons.Default.Spellcheck,
                title = "Chính tả tiếng Việt",
                score = criteria.spellingScore,
                maxScore = 4.0f,
                accentColor = Color(0xFF2563EB)
            )

            Spacer(modifier = Modifier.height(12.dp))

            CriteriaProgressItem(
                icon = Icons.Default.Create,
                title = "Hình thức & Vở sạch chữ đẹp",
                score = criteria.formatScore,
                maxScore = 3.0f,
                accentColor = Color(0xFF0D9488)
            )

            Spacer(modifier = Modifier.height(12.dp))

            CriteriaProgressItem(
                icon = Icons.Default.MenuBook,
                title = "Nội dung & Ngữ pháp",
                score = criteria.contentScore,
                maxScore = 2.0f,
                accentColor = Color(0xFF7C3AED)
            )

            Spacer(modifier = Modifier.height(12.dp))

            CriteriaProgressItem(
                icon = Icons.Default.AutoAwesome,
                title = "Sáng tạo & Cảm xúc",
                score = criteria.creativityScore,
                maxScore = 1.0f,
                accentColor = Color(0xFFEA580C)
            )
        }
    }
}

@Composable
private fun CriteriaProgressItem(
    icon: ImageVector,
    title: String,
    score: Float,
    maxScore: Float,
    accentColor: Color
) {
    val progress = (score / maxScore).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 800),
        label = "progressAnim"
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(accentColor.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }

            Text(
                text = "${"%.1f".format(score)} / ${"%.0f".format(maxScore)}đ",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = accentColor
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = accentColor,
            trackColor = accentColor.copy(alpha = 0.12f)
        )
    }
}
