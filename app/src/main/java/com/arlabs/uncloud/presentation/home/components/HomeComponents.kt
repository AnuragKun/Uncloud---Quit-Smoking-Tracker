package com.arlabs.uncloud.presentation.home.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
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
import kotlin.math.cos
import kotlin.math.sin


@Composable
fun HeroTimer(
        years: Long = 0,
        months: Long = 0,
        days: Long,
        hours: Long,
        minutes: Long,
        seconds: Long,
        modifier: Modifier = Modifier
) {
        // Theme Colors
        val primaryCyan = Color(0xFF00E5FF)
        val secondaryGreen = Color(0xFF00FF9D)
        val trackColor = Color(0xFF1C1C1E)
        val glowColor = primaryCyan.copy(alpha = 0.5f)

        // Calculate Progress (0.0 to 1.0 based on seconds)
        val targetProgress = seconds / 60f
        val smoothProgress by animateFloatAsState(
                targetValue = targetProgress,
                animationSpec = tween(durationMillis = 1000, easing = LinearEasing),
                label = "progress"
        )

        Box(contentAlignment = Alignment.Center, modifier = modifier.size(300.dp)) {

                // --- ANIMATION STATES ---
                val infiniteTransition = rememberInfiniteTransition(label = "reactor")

                // 1. Subtle Pulse (Breathing size) - Keeps the component feeling "alive"
                val pulseScale by infiniteTransition.animateFloat(
                        initialValue = 1f,
                        targetValue = 1.02f,
                        animationSpec = infiniteRepeatable(
                                animation = tween(3000, easing = FastOutSlowInEasing),
                                repeatMode = RepeatMode.Reverse
                        ),
                        label = "pulse"
                )

                // --- LAYER 1: THE STATIC CLOCK FACE (Ticks) ---
                Canvas(modifier = Modifier.fillMaxSize().scale(pulseScale)) {
                        val center = this.center
                        val radius = size.minDimension / 2

                        // A. Dark Background Track
                        drawCircle(
                                color = trackColor,
                                style = Stroke(width = 20.dp.toPx())
                        )

                        // B. Static Tick Marks (The Watch Face)
                        // We removed the `rotate` block here. These are now fixed.
                        val tickCount = 60
                        val tickLength = 10.dp.toPx()
                        val tickRadius = radius - 25.dp.toPx()

                        for (i in 0 until tickCount) {
                                // Calculate angle for each second mark (start at 12 o'clock / -90 degrees)
                                val angle = (360f / tickCount) * i - 90f
                                val angleRad = Math.toRadians(angle.toDouble())

                                // Highlight cardinal points (12, 3, 6, 9)
                                val isCardinal = i % 15 == 0

                                val currentTickLength = if (isCardinal) tickLength * 1.5f else tickLength
                                val tickColor = if (isCardinal) Color.Gray else Color(0xFF2C2C2E)
                                val tickWidth = if (isCardinal) 4.dp.toPx() else 2.dp.toPx()

                                val startX = center.x + (tickRadius * cos(angleRad)).toFloat()
                                val startY = center.y + (tickRadius * sin(angleRad)).toFloat()
                                val endX = center.x + ((tickRadius - currentTickLength) * cos(angleRad)).toFloat()
                                val endY = center.y + ((tickRadius - currentTickLength) * sin(angleRad)).toFloat()

                                drawLine(
                                        color = tickColor,
                                        start = Offset(startX, startY),
                                        end = Offset(endX, endY),
                                        strokeWidth = tickWidth,
                                        cap = StrokeCap.Round
                                )
                        }
                }

                // --- LAYER 2: THE ACTIVE RING (Moves/Fills) ---
                Canvas(modifier = Modifier.fillMaxSize().padding(2.dp).scale(pulseScale)) {
                        val strokeWidth = 16.dp.toPx()

                        // A. The Glow (Follows the fill)
                        drawArc(
                                color = glowColor,
                                startAngle = -90f,
                                sweepAngle = smoothProgress * 360f,
                                useCenter = false,
                                style = Stroke(width = strokeWidth + 20f, cap = StrokeCap.Round)
                        )

                        // B. The Sharp Gradient Ring (The actual fill)
                        drawArc(
                                brush = Brush.sweepGradient(
                                        colors = listOf(primaryCyan, secondaryGreen, primaryCyan)
                                ),
                                startAngle = -90f,
                                sweepAngle = smoothProgress * 360f,
                                useCenter = false,
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                }

                // --- LAYER 3: TEXT CONTENT ---
                Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                ) {
                        Text(
                                text = "SYSTEM UPTIME",
                                style = MaterialTheme.typography.labelSmall.copy(
                                        letterSpacing = 2.sp,
                                        fontWeight = FontWeight.Bold
                                ),
                                color = Color.Gray,
                                modifier = Modifier.padding(bottom = 4.dp)
                        )

                        if (years > 0) {
                                val yearLabel = if (years == 1L) "YEAR" else "YEARS"
                                BigNumberDisplay(value = years, label = yearLabel)
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                        verticalAlignment = Alignment.Bottom,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                        val monthUnit = if (months == 1L) "MO" else "MOS"
                                        val dayUnit = if (days == 1L) "DAY" else "DAYS"
                                        SubUnit(value = months, unit = monthUnit)
                                        SubUnit(value = days, unit = dayUnit)
                                }
                        } else if (months > 0) {
                                val monthLabel = if (months == 1L) "MONTH" else "MONTHS"
                                BigNumberDisplay(value = months, label = monthLabel)
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                        verticalAlignment = Alignment.Bottom,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                        val dayUnit = if (days == 1L) "DAY" else "DAYS"
                                        val hourUnit = if (hours == 1L) "HR" else "HRS"
                                        SubUnit(value = days, unit = dayUnit)
                                        SubUnit(value = hours, unit = hourUnit)
                                }
                        } else {
                                val dayLabel = if (days == 1L) "DAY" else "DAYS"
                                BigNumberDisplay(value = days, label = dayLabel)
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                        verticalAlignment = Alignment.Bottom,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                        val hourUnit = if (hours == 1L) "HR" else "HRS"
                                        val minUnit = if (minutes == 1L) "MIN" else "MINS"
                                        SubUnit(value = hours, unit = hourUnit)
                                        SubUnit(value = minutes, unit = minUnit)
                                        // Seconds are Active (Cyan) to match the moving ring
                                        SubUnit(value = seconds, unit = "SEC", isActive = true)
                                }
                        }
                }
        }
}

// --- SUB-COMPONENTS FOR CLEANER TYPOGRAPHY ---

@Composable
fun BigNumberDisplay(value: Long, label: String) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                        text = "$value",
                        style = MaterialTheme.typography.displayLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = FontFamily.Monospace, // Digital Look
                                fontSize = 72.sp,
                                letterSpacing = (-2).sp
                        ),
                        color = Color.White
                )
                Text(
                        text = label,
                        style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                color = Color(0xFF00E5FF) // Cyan accent
                        ),
                        modifier = Modifier.offset(y = (-8).dp) // Pull closer to number
                )
        }
}

@Composable
fun SubUnit(value: Long, unit: String, isActive: Boolean = false) {
        Row(verticalAlignment = Alignment.Bottom) {
                Text(
                        text = String.format(Locale.getDefault(), "%02d", value),
                        style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = if (isActive) Color.White else Color(0xFFE0E0E0)
                        )
                )
                Text(
                        text = unit,
                        style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 10.sp,
                                color = if (isActive) Color(0xFF00E5FF) else Color.Gray
                        ),
                        modifier = Modifier.padding(bottom = 4.dp, start = 2.dp)
                )
        }
}

// --- 1. THE MAIN GRID ---
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
                // Section Label
                Text(
                        text = "MODULE STATUS",
                        style = MaterialTheme.typography.labelSmall.copy(
                                letterSpacing = 2.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray
                        ),
                        modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        SystemStatModule(
                                iconRes = R.drawable.icon_flame,
                                value = "$cigarettesAvoided",
                                label = "AVOIDED",
                                subLabel = "CIGARETTES",
                                accentColor = Color(0xFFFF7043),
                                onClick = { onStatClick(StatType.CIGARETTES) },
                                modifier = Modifier.weight(1f)
                        )
                        SystemStatModule(
                                iconRes = R.drawable.icon_money,
                                value = "${currency}${String.format(Locale.getDefault(), "%.0f", moneySaved)}", // Removed decimals for cleaner look on grid
                                label = "SAVED",
                                subLabel = "CURRENCY",
                                accentColor = Color(0xFF00FF9D), // Cyber Green
                                onClick = { onStatClick(StatType.MONEY) },
                                modifier = Modifier.weight(1f)
                        )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        TimeReclaimedModule(
                                scheduleTime = scheduleTimeRegained,
                                biologicalTime = biologicalTimeRegained,
                                onOpenDetails = { onStatClick(StatType.TIME) },
                                modifier = Modifier.weight(1f)
                        )
                        SystemStatModule(
                                iconRes = R.drawable.icon_heartbeat,
                                value = "$completedMilestones/$totalMilestones",
                                label = "MILESTONES",
                                subLabel = "COMPLETED",
                                accentColor = Color(0xFFEF5350),
                                onClick = onHealthClick,
                                modifier = Modifier.weight(1f)
                        )
                }
        }
}

// --- 2. SPECIAL TIME MODULE (Handles the toggle logic) ---
@Composable
fun TimeReclaimedModule(
        scheduleTime: String,
        biologicalTime: String,
        onOpenDetails: () -> Unit,
        modifier: Modifier = Modifier
) {
        var showBiological by remember { mutableStateOf(false) }

        val value = if (showBiological) biologicalTime else scheduleTime
        val label = if (showBiological) "REGAINED" else "RECLAIMED"
        val subLabel = if (showBiological) "LIFE EXP." else "FREE TIME"
        val color = if (showBiological) Color(0xFF00E5FF) else Color(0xFF42A5F5)

        SystemStatModule(
                iconRes = R.drawable.icon_time_reclaimed,
                value = value,
                label = label,
                subLabel = subLabel,
                accentColor = color,
                onClick = { showBiological = !showBiological },
                onLongClick = onOpenDetails,
                modifier = modifier
        )
}

// --- 3. THE "SYSTEM MODULE" CARD (Replaces StatCard) ---
@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun SystemStatModule(
        iconRes: Int,
        value: String,
        label: String,
        subLabel: String,
        accentColor: Color,
        onClick: (() -> Unit)? = null,
        onLongClick: (() -> Unit)? = null,
        modifier: Modifier = Modifier
) {
        Surface(
                modifier = modifier
                        .aspectRatio(1.3f) // Slightly wider
                        .clip(RoundedCornerShape(16.dp))
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
                color = Color(0xFF161B22), // Darker panel color
                border = BorderStroke(1.dp, Color(0xFF252A30)), // Subtle tech border
                shape = RoundedCornerShape(16.dp)
        ) {
                Column(
                        modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.SpaceBetween
                ) {
                        // Header: Icon + Label
                        Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                        painter = painterResource(id = iconRes),
                                        contentDescription = null,
                                        tint = accentColor,
                                        modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                letterSpacing = 1.sp,
                                                color = accentColor
                                        )
                                )
                        }

                        // Body: Value + SubLabel
                        Column {
                                Text(
                                        text = value,
                                        style = MaterialTheme.typography.titleLarge.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontFamily = FontFamily.Monospace, // Digital look
                                                color = Color.White
                                        ),
                                        maxLines = 1
                                )
                                Text(
                                        text = subLabel,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                                color = Color.Gray,
                                                fontSize = 10.sp
                                        )
                                )
                        }
                }
        }
}

// --- 4. THE PROJECTION DIALOG (Terminal Style) ---
@Composable
fun ProjectionDetailDialog(
        statType: StatType,
        stats: ProjectedStats,
        currency: String,
        onDismiss: () -> Unit
) {
        val accentColor = when (statType) {
                StatType.CIGARETTES -> Color(0xFFFF7043)
                StatType.MONEY -> Color(0xFF00FF9D)
                StatType.TIME -> Color(0xFF00E5FF)
        }

        val title = when (statType) {
                StatType.CIGARETTES -> "AVOIDANCE LOG"
                StatType.MONEY -> "FINANCIAL PROJECTION"
                StatType.TIME -> "TEMPORAL ANALYSIS"
        }

        val iconRes = when (statType) {
                StatType.CIGARETTES -> R.drawable.icon_flame
                StatType.MONEY -> R.drawable.icon_money
                StatType.TIME -> R.drawable.icon_time_reclaimed
        }

        Dialog(onDismissRequest = onDismiss) {
                Card(
                        shape = RoundedCornerShape(4.dp), // Sharper "Window" corners
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1117)),
                        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.3f)),
                        modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                ) {
                        Column {
                                // TERMINAL HEADER BAR
                                Box(
                                        modifier = Modifier
                                                .fillMaxWidth()
                                                .background(accentColor.copy(alpha = 0.1f))
                                                .padding(16.dp)
                                ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                        painter = painterResource(id = iconRes),
                                                        contentDescription = null,
                                                        tint = accentColor,
                                                        modifier = Modifier.size(20.dp)
                                                )
                                                Spacer(modifier = Modifier.width(12.dp))
                                                Text(
                                                        text = "SYSTEM // $title",
                                                        style = MaterialTheme.typography.titleMedium.copy(
                                                                fontFamily = FontFamily.Monospace,
                                                                fontWeight = FontWeight.Bold,
                                                                color = accentColor
                                                        )
                                                )
                                        }
                                }

                                // CONTENT SCROLLABLE AREA
                                Column(
                                        modifier = Modifier
                                                .padding(24.dp)
                                                .verticalScroll(rememberScrollState())
                                ) {
                                        // Logic explanation for Time
                                        if (statType == StatType.TIME) {
                                                TimeExplanationBlock(accentColor)
                                                Spacer(modifier = Modifier.height(24.dp))
                                        }

                                        Text(
                                                text = "FUTURE TRAJECTORY",
                                                style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray),
                                                modifier = Modifier.padding(bottom = 8.dp)
                                        )

                                        // Data Rows
                                        ProjectionRow("24 Hours", stats.oneDay, statType, currency)
                                        ProjectionRow("1 Week", stats.oneWeek, statType, currency)
                                        ProjectionRow("1 Month", stats.oneMonth, statType, currency)
                                        ProjectionRow("1 Year", stats.oneYear, statType, currency)
                                        ProjectionRow("5 Years", stats.fiveYears, statType, currency) // Assuming you add this later

                                        Spacer(modifier = Modifier.height(32.dp))

                                        // Close Button
                                        Button(
                                                onClick = onDismiss,
                                                colors = ButtonDefaults.buttonColors(
                                                        containerColor = Color(0xFF21262D),
                                                        contentColor = Color.White
                                                ),
                                                shape = RoundedCornerShape(4.dp),
                                                modifier = Modifier.fillMaxWidth()
                                        ) {
                                                Text("CLOSE LOG", fontFamily = FontFamily.Monospace)
                                        }
                                }
                        }
                }
        }
}

// --- 5. HELPER COMPONENTS ---

@Composable
fun TimeExplanationBlock(accentColor: Color) {
        Column(
                modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF161B22), RoundedCornerShape(8.dp))
                        .padding(12.dp)
        ) {
                Text(
                        text = "METRIC DEFINITIONS:",
                        style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = accentColor
                        )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                        text = "• Free Time: Minutes formerly spent smoking.",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                        text = "• Life Exp: +11 mins biological life per cigarette avoided.",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                )
        }
}

@Composable
fun ProjectionRow(label: String, value: Double, statType: StatType, currency: String) {
        val formattedValue = when (statType) {
                StatType.MONEY -> "${currency}${String.format(Locale.getDefault(), "%.2f", value)}"
                StatType.CIGARETTES -> String.format(Locale.getDefault(), "%,.0f", value)
                StatType.TIME -> {
                        val totalMinutes = value.toLong()
                        val days = totalMinutes / (24 * 60)
                        val hours = (totalMinutes % (24 * 60)) / 60
                        if (days > 0) "${days}d ${hours}h" else "${hours}h ${totalMinutes % 60}m"
                }
        }

        Row(
                modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                verticalAlignment = Alignment.Bottom // Align text to the dashed line
        ) {
                Text(
                        text = label,
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                )

                // Dotted Line Spacer
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                        modifier = Modifier
                                .weight(1f)
                                .height(1.dp)
                                .padding(bottom = 4.dp) // Align slightly up
                ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                                drawLine(
                                        color = Color.DarkGray,
                                        start = Offset(0f, 0f),
                                        end = Offset(size.width, 0f),
                                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(5f, 5f), 0f)
                                )
                        }
                }
                Spacer(modifier = Modifier.width(8.dp))

                Text(
                        text = formattedValue,
                        style = MaterialTheme.typography.bodyLarge.copy(
                                color = Color.White,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                        )
                )
        }
}

// MOTIVATION CARD
@Composable
fun MotivationCard(
        quote: Quote?,
        onRefresh: () -> Unit,
        modifier: Modifier = Modifier
) {
        if (quote == null) return

        // Theme Colors for this card
        val cardBgColor = Color(0xFF161B22) // A deep dark background
        val accentCyan = Color(0xFF00E5FF)
        val textPrimary = Color.White
        val textSecondary = Color(0xFF8B9BB4)

        Card(
                modifier = modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp), // Soft, modern corners
                colors = CardDefaults.cardColors(containerColor = cardBgColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                        // --- LAYER 1: BACKGROUND IMAGE DECORATION ---
                        // Place the cyber-quote image in the background.
                        // We make it large, align it to the top-right, and give it a low alpha
                        // so it sits subtly behind the text.
                        Image(
                                painter = painterResource(id = R.drawable.icon_quote), // Ensure this matches your PNG's resource name
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                        .matchParentSize()
                                        .alpha(0.15f) // Subtle, glowing effect
                        )

                        // --- LAYER 2: MAIN CONTENT ---
                        Column(
                                modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp)
                        ) {
                                // 1. HEADER: Label and Refresh Button
                                Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                ) {
                                        Text(
                                                text = "DAILY CLARITY",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                        letterSpacing = 3.sp, // Wide spacing for a "system" look
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 11.sp
                                                ),
                                                color = accentCyan // Cyan accent color
                                        )

                                        IconButton(
                                                onClick = onRefresh,
                                                modifier = Modifier.size(24.dp)
                                        ) {
                                                Icon(
                                                        imageVector = Icons.Rounded.Refresh,
                                                        contentDescription = "Refresh Quote",
                                                        tint = textSecondary,
                                                        modifier = Modifier.size(18.dp)
                                                )
                                        }
                                }

                                Spacer(modifier = Modifier.height(20.dp))

                                // 2. THE QUOTE TEXT
                                // Use a Serif font for a more "classic wisdom" feel against the digital background.
                                Text(
                                        text = "\"${quote.text}\"",
                                        style = MaterialTheme.typography.headlineSmall.copy(
                                                fontFamily = FontFamily.Serif,
                                                fontWeight = FontWeight.Light,
                                                fontStyle = FontStyle.Italic,
                                                lineHeight = 32.sp
                                        ),
                                        color = textPrimary
                                )

                                Spacer(modifier = Modifier.height(24.dp))

                                // 3. THE AUTHOR SIGNATURE
                                // Use a Monospace font to look like a digital readout.
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                        // A small cyan dash before the name
                                        Box(
                                                modifier = Modifier
                                                        .size(24.dp, 2.dp)
                                                        .background(accentCyan)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                                text = quote.author.uppercase(),
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                        fontFamily = FontFamily.Monospace,
                                                        fontWeight = FontWeight.Bold,
                                                        letterSpacing = 1.sp
                                                ),
                                                color = textSecondary
                                        )
                                }
                        }
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
                // 1. SECTION HEADER (Renamed to match System Theme)
                Text(
                        text = "BIOLOGICAL RECOVERY",
                        style = MaterialTheme.typography.labelSmall.copy(
                                letterSpacing = 2.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray
                        ),
                        modifier = Modifier.padding(bottom = 12.dp, start = 4.dp)
                )

                // 2. THE MODULE CARD
                Surface(
                        modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .clickable(onClick = onClick),
                        color = Color(0xFF161B22), // Deep dark panel color
                        border = BorderStroke(1.dp, Color(0xFF252A30)), // Tech border
                        shape = RoundedCornerShape(16.dp)
                ) {
                        Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                        ) {
                                // A. Icon with Glow Effect
                                Box(
                                        modifier = Modifier
                                                .size(48.dp)
                                                .background(Color(0xFFEF5350).copy(alpha = 0.1f), CircleShape),
                                        contentAlignment = Alignment.Center
                                ) {
                                        Icon(
                                                painter = painterResource(id = R.drawable.icon_heartbeat),
                                                contentDescription = null,
                                                tint = Color(0xFFEF5350), // Red Accent
                                                modifier = Modifier.size(24.dp)
                                        )
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                // B. Data Column
                                Column(modifier = Modifier.weight(1f)) {
                                        // Title Row
                                        Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                        ) {
                                                Text(
                                                        text = milestone.title,
                                                        style = MaterialTheme.typography.titleSmall.copy(
                                                                color = Color.White,
                                                                fontWeight = FontWeight.Bold
                                                        )
                                                )

                                                // Digital Percentage Readout
                                                Text(
                                                        text = "${(progress * 100).toInt()}%",
                                                        style = MaterialTheme.typography.titleMedium.copy(
                                                                fontFamily = FontFamily.Monospace,
                                                                fontWeight = FontWeight.Bold,
                                                                color = Color(0xFF00FF9D) // Cyber Green
                                                        )
                                                )
                                        }

                                        Spacer(modifier = Modifier.height(4.dp))

                                        // Description text
                                        Text(
                                                text = milestone.description,
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                        color = Color(0xFF8B949E), // Muted Gray
                                                        lineHeight = 16.sp
                                                ),
                                                maxLines = 2
                                        )

                                        Spacer(modifier = Modifier.height(12.dp))

                                        // C. Gradient Progress Bar
                                        // Using a Box allows us to apply a Brush gradient, which isn't possible
                                        // with the standard LinearProgressIndicator
                                        Box(
                                                modifier = Modifier
                                                        .fillMaxWidth()
                                                        .height(6.dp)
                                                        .clip(RoundedCornerShape(50))
                                                        .background(Color(0xFF21262D)) // Dark track background
                                        ) {
                                                Box(
                                                        modifier = Modifier
                                                                .fillMaxWidth(progress) // Width based on progress float
                                                                .fillMaxHeight()
                                                                .background(
                                                                        Brush.horizontalGradient(
                                                                                colors = listOf(
                                                                                        Color(0xFF00FF9D), // Green
                                                                                        Color(0xFF00E5FF)  // Cyan
                                                                                )
                                                                        )
                                                                )
                                                )
                                        }
                                }
                        }
                }
        }
}