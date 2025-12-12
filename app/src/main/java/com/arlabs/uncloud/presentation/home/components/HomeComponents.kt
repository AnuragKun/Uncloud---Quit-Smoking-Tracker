package com.arlabs.uncloud.presentation.home.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.arlabs.uncloud.R
import com.arlabs.uncloud.domain.model.HealthMilestone
import com.arlabs.uncloud.domain.model.Quote
import com.arlabs.uncloud.presentation.home.ProjectedStats
import com.arlabs.uncloud.presentation.home.StatType
import java.util.Locale

// --- Hero Timer ---
@Composable
fun HeroTimer(
        days: Long,
        hours: Long,
        minutes: Long,
        seconds: Long,
        modifier: Modifier = Modifier
) {
        Box(contentAlignment = Alignment.Center, modifier = modifier.size(280.dp)) {
                // Breathing animation for the ring
                val infiniteTransition = rememberInfiniteTransition(label = "breathing")
                val scale by
                        infiniteTransition.animateFloat(
                                initialValue = 1f,
                                targetValue = 1.05f,
                                animationSpec =
                                        infiniteRepeatable(
                                                animation =
                                                        tween(2000, easing = FastOutSlowInEasing),
                                                repeatMode = RepeatMode.Reverse
                                        ),
                                label = "scale"
                        )

                // Circular Progress Background
                Canvas(modifier = Modifier.fillMaxSize()) {
                        val strokeWidth = 15.dp.toPx()
                        drawCircle(color = Color(0xFF1C1C1E), style = Stroke(width = strokeWidth))
                }

                // Animated Gradient Ring
                Canvas(modifier = Modifier.fillMaxSize().padding(10.dp)) { // Padding for the glow
                        val strokeWidth = 15.dp.toPx()
                        // Just a static full circle for now, conceptually could be progress towards
                        // a milestone
                        drawArc(
                                brush =
                                        Brush.sweepGradient(
                                                colors =
                                                        listOf(
                                                                Color(0xFF00E5FF),
                                                                Color(0xFF00FF9D),
                                                                Color(0xFF00E5FF)
                                                        )
                                        ),
                                startAngle = -90f,
                                sweepAngle = 360f, // Full circle loop
                                useCenter = false,
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                }

                // Content
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                                text = "Unclouded for",
                                style = MaterialTheme.typography.labelLarge,
                                color = Color.Gray,
                                modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Text(
                                text = "${days}d",
                                style =
                                        MaterialTheme.typography.displayMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                        )
                        )

                        Row(
                                verticalAlignment = Alignment.Bottom,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                                TimerUnit(value = hours, unit = "h")
                                TimerUnit(value = minutes, unit = "m")
                                TimerUnit(value = seconds, unit = "s")
                        }
                }
        }
}

@Composable
fun TimerUnit(value: Long, unit: String) {
        Row(verticalAlignment = Alignment.Bottom) {
                Text(
                        text = String.format(Locale.getDefault(), "%02d", value),
                        style =
                                MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFE0E0E0)
                                )
                )
                Text(
                        text = unit,
                        style = MaterialTheme.typography.labelMedium.copy(color = Color.Gray),
                        modifier = Modifier.padding(bottom = 4.dp, start = 2.dp)
                )
        }
}

// --- Stats Grid ---
@Composable
fun StatGrid(
        cigarettesAvoided: Long,
        moneySaved: Double,
        scheduleTimeRegained: String,
        biologicalTimeRegained: String,
        completedMilestones: Int,
        totalMilestones: Int,
        currency: String,
        onStatClick: (StatType) -> Unit,
        onHealthClick: () -> Unit,
        modifier: Modifier = Modifier
) {
        Column(
                modifier = modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        StatCard(
                                iconRes = R.drawable.icon_flame,
                                value = "$cigarettesAvoided",
                                label = "Cigarettes\navoided",
                                accentColor = Color(0xFFFF7043),
                                onClick = { onStatClick(StatType.CIGARETTES) },
                                modifier = Modifier.weight(1f)
                        )
                        StatCard(
                                iconRes = R.drawable.icon_money,
                                value =
                                        "${currency}${String.format(Locale.getDefault(), "%.2f", moneySaved)}",
                                label = "Money\nsaved",
                                accentColor = Color(0xFF209A24),
                                onClick = { onStatClick(StatType.MONEY) },
                                modifier = Modifier.weight(1f)
                        )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        TimeReclaimedCard(
                                scheduleTime = scheduleTimeRegained,
                                biologicalTime = biologicalTimeRegained,
                                onOpenDetails = { onStatClick(StatType.TIME) },
                                modifier = Modifier.weight(1f)
                        )
                        StatCard(
                                iconRes = R.drawable.icon_heartbeat,
                                value = "$completedMilestones / $totalMilestones",
                                label = "Milestones",
                                accentColor = Color(0xFFEF5350),
                                onClick = onHealthClick,
                                modifier = Modifier.weight(1f)
                        )
                }
        }
}

@Composable
fun TimeReclaimedCard(
        scheduleTime: String,
        biologicalTime: String,
        onOpenDetails: () -> Unit,
        modifier: Modifier = Modifier
) {
        var showBiological by remember { mutableStateOf(false) }

        val value = if (showBiological) biologicalTime else scheduleTime
        val label = if (showBiological) "Life Expectancy\nRegained" else "Free Time\nReclaimed"

        StatCard(
                iconRes = R.drawable.icon_time_reclaimed,
                value = value,
                label = label,
                accentColor = Color(0xFF42A5F5),
                onClick = { showBiological = !showBiological },
                onLongClick = onOpenDetails,
                modifier = modifier
        )
}

@Composable
@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
fun StatCard(
        iconRes: Int,
        value: String,
        label: String,
        accentColor: Color,
        onClick: (() -> Unit)? = null,
        onLongClick: (() -> Unit)? = null,
        modifier: Modifier = Modifier
) {
        // ... existing StatCard implementation ...
        Card(
                modifier =
                        modifier.aspectRatio(1.4f) // make them slightly wider than tall
                                .clip(RoundedCornerShape(20.dp))
                                .then(
                                        if (onLongClick != null && onClick != null) {
                                                Modifier.combinedClickable(
                                                        onClick = onClick,
                                                        onLongClick = onLongClick
                                                )
                                        } else if (onClick != null) {
                                                Modifier.clickable(onClick = onClick)
                                        } else {
                                                Modifier
                                        }
                                ),
                colors =
                        CardDefaults.cardColors(
                                containerColor = Color(0xFF1C1C1E).copy(alpha = 0.6f)
                        )
        ) {
                Column(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.SpaceBetween
                ) {
                        Box(
                                modifier =
                                        Modifier.size(32.dp)
                                                .background(
                                                        accentColor.copy(alpha = 0.2f),
                                                        CircleShape
                                                ),
                                contentAlignment = Alignment.Center
                        ) {
                                Icon(
                                        painter = painterResource(id = iconRes),
                                        contentDescription = null,
                                        tint = accentColor,
                                        modifier = Modifier.size(18.dp)
                                )
                        }

                        Column {
                                Text(
                                        text = value,
                                        style =
                                                MaterialTheme.typography.titleLarge.copy(
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color.White
                                                )
                                )
                                Text(
                                        text = label,
                                        style =
                                                MaterialTheme.typography.labelSmall.copy(
                                                        color = Color.Gray,
                                                        lineHeight = 12.sp
                                                )
                                )
                        }
                }
        }
}

// --- Motivation / Community Card ---
@Composable
fun MotivationCard(quote: Quote?, onRefresh: () -> Unit, modifier: Modifier = Modifier) {
        if (quote == null) return

        Card(
                modifier = modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2429))
        ) {
                Column(modifier = Modifier.padding(24.dp)) {
                        Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                        ) {
                                Text(
                                        text = "Daily Motivation",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = Color(0xFF8B95A5)
                                )

                                IconButton(onClick = onRefresh, modifier = Modifier.size(24.dp)) {
                                        Icon(
                                                imageVector = Icons.Default.Refresh,
                                                contentDescription = "Refresh Quote",
                                                tint = Color(0xFF8B95A5),
                                                modifier = Modifier.size(16.dp)
                                        )
                                }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                                text = "\"${quote.text}\"",
                                style =
                                        MaterialTheme.typography.bodyLarge.copy(
                                                color = Color.White,
                                                fontWeight = FontWeight.SemiBold,
                                                fontStyle = FontStyle.Italic
                                        )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                                text = "~ ${quote.author}",
                                style =
                                        MaterialTheme.typography.bodyMedium.copy(
                                                color = Color(0xFF4CAF50),
                                                fontWeight = FontWeight.Medium
                                        )
                        )
                }
        }
}

// --- Health List (Preview) ---
@Composable
fun HealthPreviewCard(
        milestone: HealthMilestone?,
        progress: Float,
        onClick: () -> Unit = {},
        modifier: Modifier = Modifier
) {
        if (milestone == null) return

        Column(modifier = modifier.fillMaxWidth()) {
                Text(
                        text = "Health Improvements",
                        style =
                                MaterialTheme.typography.titleMedium.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                ),
                        modifier = Modifier.padding(bottom = 12.dp)
                )

                Card(
                        modifier =
                                Modifier.fillMaxWidth()
                                        .clip(RoundedCornerShape(16.dp))
                                        .clickable(onClick = onClick),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C1E))
                ) {
                        Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                        ) {
                                Icon(
                                        painter = painterResource(id = R.drawable.icon_heartbeat),
                                        contentDescription = null,
                                        tint = Color(0xFFEF5350),
                                        modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                                Text(
                                                        text = milestone.title,
                                                        style =
                                                                MaterialTheme.typography.bodyMedium
                                                                        .copy(
                                                                                color = Color.White,
                                                                                fontWeight =
                                                                                        FontWeight
                                                                                                .SemiBold
                                                                        )
                                                )
                                                Text(
                                                        text = "${(progress * 100).toInt()}%",
                                                        style =
                                                                MaterialTheme.typography.bodySmall
                                                                        .copy(color = Color.Gray)
                                                )
                                        }

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Text(
                                                text = milestone.description,
                                                style =
                                                        MaterialTheme.typography.bodySmall.copy(
                                                                color = Color.Gray
                                                        ),
                                                maxLines = 2
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        androidx.compose.material3.LinearProgressIndicator(
                                                progress = { progress },
                                                modifier = Modifier.fillMaxWidth().height(6.dp),
                                                color = Color(0xFF00E676),
                                                trackColor = Color.DarkGray,
                                        )
                                }
                        }
                }
        }
}

@Composable
fun ProjectionDetailDialog(
        statType: StatType,
        stats: ProjectedStats,
        currency: String,
        onDismiss: () -> Unit
) {
        Dialog(onDismissRequest = onDismiss) {
                Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C1E)),
                        modifier = Modifier.fillMaxWidth().padding(16.dp)
                ) {
                        Column(
                                modifier =
                                        Modifier.padding(24.dp)
                                                .verticalScroll(rememberScrollState()),
                                horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                                val iconRes =
                                        when (statType) {
                                                StatType.CIGARETTES -> R.drawable.icon_flame
                                                StatType.MONEY -> R.drawable.icon_money
                                                StatType.TIME -> R.drawable.icon_time_reclaimed
                                        }
                                val accentColor =
                                        when (statType) {
                                                StatType.CIGARETTES -> Color(0xFFFF7043)
                                                StatType.MONEY -> Color(0xFF209A24)
                                                StatType.TIME -> Color(0xFF42A5F5)
                                        }
                                val title =
                                        when (statType) {
                                                StatType.CIGARETTES -> "Cigarettes Avoided"
                                                StatType.MONEY -> "Money Saved"
                                                StatType.TIME -> "Time Reclaimed"
                                        }

                                Box(
                                        modifier =
                                                Modifier.size(64.dp)
                                                        .background(
                                                                accentColor.copy(alpha = 0.1f),
                                                                RoundedCornerShape(50)
                                                        ),
                                        contentAlignment = Alignment.Center
                                ) {
                                        Icon(
                                                painter = painterResource(id = iconRes),
                                                contentDescription = null,
                                                tint = accentColor,
                                                modifier = Modifier.size(32.dp)
                                        )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                        text = title,
                                        style =
                                                MaterialTheme.typography.headlineSmall.copy(
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color.White
                                                )
                                )

                                Spacer(modifier = Modifier.height(24.dp))

                                if (statType == StatType.TIME) {
                                        Text(
                                                text = "Why two metrics?",
                                                style =
                                                        MaterialTheme.typography.titleMedium.copy(
                                                                color = Color.White,
                                                                fontWeight = FontWeight.Bold
                                                        ),
                                                modifier = Modifier.align(Alignment.Start)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                                text =
                                                        "1. Free Time Reclaimed: Based on your input of how many minutes you take to smoke a cigarette.",
                                                style =
                                                        MaterialTheme.typography.bodyMedium.copy(
                                                                color = Color.Gray
                                                        ),
                                                modifier = Modifier.align(Alignment.Start)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                                text =
                                                        "2. Life Expectancy Regained: Based on scientific research.",
                                                style =
                                                        MaterialTheme.typography.bodyMedium.copy(
                                                                color = Color.Gray
                                                        ),
                                                modifier = Modifier.align(Alignment.Start)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))

                                        Box(
                                                modifier =
                                                        Modifier.fillMaxWidth()
                                                                .background(
                                                                        Color(0xFF2C2C2E),
                                                                        RoundedCornerShape(12.dp)
                                                                )
                                                                .padding(12.dp)
                                        ) {
                                                Column {
                                                        Text(
                                                                text = "Did you know?",
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .labelMedium.copy(
                                                                                color = accentColor,
                                                                                fontWeight =
                                                                                        FontWeight
                                                                                                .Bold
                                                                        )
                                                        )
                                                        Spacer(modifier = Modifier.height(4.dp))
                                                        Text(
                                                                text =
                                                                        "On average, every cigarette shortens your life by 11 minutes.",
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .bodySmall.copy(
                                                                                color =
                                                                                        Color.LightGray
                                                                        )
                                                        )
                                                }
                                        }
                                        Spacer(modifier = Modifier.height(24.dp))
                                }

                                ProjectionRow(
                                        label = "1 Day",
                                        value = stats.oneDay,
                                        statType = statType,
                                        currency = currency
                                )
                                ProjectionRow(
                                        label = "1 Week",
                                        value = stats.oneWeek,
                                        statType = statType,
                                        currency = currency
                                )
                                ProjectionRow(
                                        label = "1 Month",
                                        value = stats.oneMonth,
                                        statType = statType,
                                        currency = currency
                                )
                                ProjectionRow(
                                        label = "1 Year",
                                        value = stats.oneYear,
                                        statType = statType,
                                        currency = currency
                                )

                                Spacer(modifier = Modifier.height(24.dp))

                                Button(
                                        onClick = onDismiss,
                                        colors =
                                                ButtonDefaults.buttonColors(
                                                        containerColor = accentColor
                                                ),
                                        modifier = Modifier.fillMaxWidth()
                                ) { Text("Close", color = Color.White) }
                        }
                }
        }
}

@Composable
fun ProjectionRow(label: String, value: Double, statType: StatType, currency: String) {
        Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
        ) {
                Text(
                        text = label,
                        style = MaterialTheme.typography.bodyLarge.copy(color = Color.Gray)
                )

                val formattedValue =
                        when (statType) {
                                StatType.MONEY ->
                                        "${currency}${String.format(Locale.getDefault(), "%.2f", value)}"
                                StatType.CIGARETTES ->
                                        String.format(Locale.getDefault(), "%.0f", value)
                                StatType.TIME -> {
                                        val totalMinutes = value.toLong()
                                        val days = totalMinutes / (24 * 60)
                                        val hours = (totalMinutes % (24 * 60)) / 60
                                        val minutes = totalMinutes % 60
                                        when {
                                                days > 0 -> "${days}d ${hours}h"
                                                hours > 0 -> "${hours}h ${minutes}m"
                                                else -> "${minutes}m"
                                        }
                                }
                        }

                Text(
                        text = formattedValue,
                        style =
                                MaterialTheme.typography.titleMedium.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                )
                )
        }
}
