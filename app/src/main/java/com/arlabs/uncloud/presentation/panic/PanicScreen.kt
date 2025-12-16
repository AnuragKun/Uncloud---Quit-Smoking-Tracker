package com.arlabs.uncloud.presentation.panic

import android.view.HapticFeedbackConstants
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Vibration
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// --- THEME CONSTANTS ---
private val SysCyan = Color(0xFF00E5FF)
private val SysRed = Color(0xFFFF5252)
private val SysGreen = Color(0xFF00FF9D)
private val SysDark = Color(0xFF0D1117)
private val SysPanel = Color(0xFF161B22)
// Defined Amber here for consistency
private val SysAmber = Color(0xFFFFAB40)

@Composable
fun PanicScreen(onNavigateBack: () -> Unit) {
    // 1. Core State
    var instruction by remember { mutableStateOf("INITIALIZING...") }
    var subInstruction by remember { mutableStateOf("Calibrating biological sensors") }
    var systemStatus by remember { mutableStateOf("CRITICAL") }
    var stressLevel by remember { mutableFloatStateOf(1.0f) } // 1.0 = Max stress, 0.0 = Calm
    var hapticsEnabled by remember { mutableStateOf(true) }
    var secondsRemaining by remember { mutableIntStateOf(0) }

    // Animation Values
    val scale = remember { Animatable(1f) }
    val glowAlpha = remember { Animatable(0.0f) }

    // Rotating Scanner Ring
    val infiniteTransition = rememberInfiniteTransition(label = "scanner")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing)
        ),
        label = "rotation"
    )

    // Dynamic Color based on breathing phase
    val coreColor by animateColorAsState(
        targetValue = when (instruction) {
            "INHALE" -> SysCyan
            "HOLD" -> SysAmber
            "EXHALE" -> SysGreen
            else -> Color.Gray
        },
        animationSpec = tween(1000),
        label = "color"
    )

    // --- NEW: Determine Status Color based on current systemStatus ---
    val statusColor = when (systemStatus) {
        "CRITICAL" -> SysRed
        "STABILIZING" -> SysAmber
        else -> SysGreen
    }

    val view = LocalView.current

    // 2. The 4-7-8 Breathing Logic Loop
    LaunchedEffect(Unit) {
        delay(2500) // Startup delay

        while (true) {
            // Check stress level to update status text
            systemStatus = when {
                stressLevel > 0.7f -> "CRITICAL"
                stressLevel > 0.3f -> "STABILIZING"
                else -> "NOMINAL"
            }

            // --- INHALE (4 Seconds) ---
            instruction = "INHALE"
            subInstruction = "Fill intake valves completely"
            if (hapticsEnabled) view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)

            launch { scale.animateTo(1.6f, animationSpec = tween(4000, easing = LinearOutSlowInEasing)) }
            launch { glowAlpha.animateTo(0.5f, animationSpec = tween(4000)) }

            for (i in 4 downTo 1) {
                secondsRemaining = i
                delay(1000)
            }

            // --- HOLD (7 Seconds) ---
            instruction = "HOLD"
            subInstruction = "Allow oxygen to circulate"
            if (hapticsEnabled) view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
            launch { glowAlpha.animateTo(0.8f, animationSpec = tween(1000)) }

            for (i in 7 downTo 1) {
                secondsRemaining = i
                delay(1000)
            }

            // --- EXHALE (8 Seconds) ---
            instruction = "EXHALE"
            subInstruction = "Purge system toxins"
            if (hapticsEnabled) view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)

            // Decrease stress level slightly after every cycle
            stressLevel = (stressLevel - 0.15f).coerceAtLeast(0f)

            launch { scale.animateTo(1f, animationSpec = tween(8000, easing = FastOutLinearInEasing)) }
            launch { glowAlpha.animateTo(0.0f, animationSpec = tween(8000)) }

            for (i in 8 downTo 1) {
                secondsRemaining = i
                delay(1000)
            }
        }
    }

    Scaffold(containerColor = SysDark) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // 3. Ambient Background Grid (Cyberpunk feel)
            BackgroundGrid()

            // 4. Header: System Status Bar
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 40.dp, start = 24.dp, end = 24.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SYSTEM STATUS:",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.Gray,
                            fontFamily = FontFamily.Monospace
                        )
                    )
                    Text(
                        text = systemStatus,
                        style = MaterialTheme.typography.labelMedium.copy(
                            // Use the dynamic color here
                            color = statusColor,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Stress Bar
                LinearProgressIndicator(
                    progress = { stressLevel },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    // Use the same dynamic color here
                    color = statusColor,
                    trackColor = SysPanel,
                )
            }

            // 5. Center Core (Breathing Visualizer)
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(320.dp)
            ) {
                // Rotating Scanner Ring
                Canvas(modifier = Modifier
                    .fillMaxSize()
                    .scale(1.2f)) {
                    rotate(rotation) {
                        drawArc(
                            brush = Brush.sweepGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    coreColor.copy(alpha = 0.5f),
                                    Color.Transparent
                                )
                            ),
                            startAngle = 0f,
                            sweepAngle = 120f,
                            useCenter = false,
                            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }

                    val bracketSize = 20.dp.toPx()
                    val stroke = 2.dp.toPx()
                    val offset = 10.dp.toPx()

                    drawLine(coreColor, Offset(offset, offset + bracketSize), Offset(offset, offset), stroke)
                    drawLine(coreColor, Offset(offset, offset), Offset(offset + bracketSize, offset), stroke)
                    drawLine(coreColor, Offset(size.width - offset - bracketSize, offset), Offset(size.width - offset, offset), stroke)
                    drawLine(coreColor, Offset(size.width - offset, offset), Offset(size.width - offset, offset + bracketSize), stroke)
                    drawLine(coreColor, Offset(offset, size.height - offset - bracketSize), Offset(offset, size.height - offset), stroke)
                    drawLine(coreColor, Offset(offset, size.height - offset), Offset(offset + bracketSize, size.height - offset), stroke)
                    drawLine(coreColor, Offset(size.width - offset - bracketSize, size.height - offset), Offset(size.width - offset, size.height - offset), stroke)
                    drawLine(coreColor, Offset(size.width - offset, size.height - offset), Offset(size.width - offset, size.height - offset - bracketSize), stroke)
                }

                // Breathing Core
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .scale(scale.value)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    coreColor,
                                    coreColor.copy(alpha = 0.2f),
                                    Color.Transparent
                                )
                            ),
                            shape = CircleShape
                        )
                )

                // Inner Countdown Timer
                if (secondsRemaining > 0) {
                    Text(
                        text = secondsRemaining.toString(),
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        ),
                        modifier = Modifier.scale(scale.value)
                    )
                }
            }

            // 6. Text Instructions (Below Core)
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = 180.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = instruction,
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 4.sp
                    ),
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = subInstruction.uppercase(),
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontFamily = FontFamily.Monospace,
                        color = SysCyan
                    )
                )
            }

            // 7. Footer Controls
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 48.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ControlButton(
                    icon = Icons.Rounded.Vibration,
                    isActive = hapticsEnabled,
                    onClick = { hapticsEnabled = !hapticsEnabled }
                )

                Spacer(modifier = Modifier.width(24.dp))

                Button(
                    onClick = onNavigateBack,
                    colors = ButtonDefaults.buttonColors(containerColor = SysRed.copy(alpha = 0.1f)),
                    border = BorderStroke(1.dp, SysRed.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.height(50.dp)
                ) {
                    Text(
                        text = "ABORT PROTOCOL",
                        color = SysRed,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// --- SUB-COMPONENTS ---
@Composable
fun ControlButton(
    icon: ImageVector,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(50.dp)
            .clip(CircleShape)
            .background(if (isActive) SysCyan.copy(alpha = 0.2f) else SysPanel)
            .border(1.dp, if (isActive) SysCyan else Color.Gray, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isActive) SysCyan else Color.Gray
        )
    }
}

@Composable
fun BackgroundGrid() {
    Canvas(modifier = Modifier
        .fillMaxSize()
        .alpha(0.1f)) {
        val step = 40.dp.toPx()
        val width = size.width
        val height = size.height

        for (x in 0..width.toInt() step step.toInt()) {
            drawLine(
                color = SysCyan,
                start = Offset(x.toFloat(), 0f),
                end = Offset(x.toFloat(), height),
                strokeWidth = 1f
            )
        }

        for (y in 0..height.toInt() step step.toInt()) {
            drawLine(
                color = SysCyan,
                start = Offset(0f, y.toFloat()),
                end = Offset(width, y.toFloat()),
                strokeWidth = 1f
            )
        }
    }
}