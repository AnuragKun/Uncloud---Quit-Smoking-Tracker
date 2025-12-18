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
import androidx.compose.ui.platform.LocalContext
import android.os.VibrationEffect
import android.os.Vibrator
import android.content.Context
import android.os.Build
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// --- THEME CONSTANTS REMOVED (Now using MaterialTheme) ---
// private val SysCyan...


@Composable
fun PanicScreen(onNavigateBack: () -> Unit) {
    val context = LocalContext.current
    val vibrator = remember { context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator }

    // 1. Core State
    var instruction by remember { mutableStateOf("INITIALIZING...") }
    var subInstruction by remember { mutableStateOf("Calibrating biological sensors") }
    var systemStatus by remember { mutableStateOf("CRITICAL") }
    var stressLevel by remember { mutableFloatStateOf(1.0f) }
    var hapticsEnabled by remember { mutableStateOf(true) }
    var secondsRemaining by remember { mutableIntStateOf(0) }

    // Animation layout values... (Keeping existing logic)
    val scale = remember { Animatable(1f) }
    val glowAlpha = remember { Animatable(0.0f) }

    val infiniteTransition = rememberInfiniteTransition(label = "scanner")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing)
        ),
        label = "rotation"
    )

    val coreColor by animateColorAsState(
        targetValue = when (instruction) {
            "INHALE" -> MaterialTheme.colorScheme.primary
            "HOLD" -> MaterialTheme.colorScheme.tertiary
            "EXHALE" -> MaterialTheme.colorScheme.secondary
            else -> Color.Gray
        },
        animationSpec = tween(1000),
        label = "color"
    )

    val statusColor = when (systemStatus) {
        "CRITICAL" -> MaterialTheme.colorScheme.error
        "STABILIZING" -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.secondary
    }

    // 2. The 4-7-8 Breathing Logic Loop (Keeping existing logic)
    LaunchedEffect(Unit) {
        delay(2500)
        while (true) {
            systemStatus = when {
                stressLevel > 0.7f -> "CRITICAL"
                stressLevel > 0.3f -> "STABILIZING"
                else -> "NOMINAL"
            }

            // INHALE
            instruction = "INHALE"
            subInstruction = "Fill intake valves completely"
            if (hapticsEnabled) {
               if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                     val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as android.os.VibratorManager
                     val vibrator = vibratorManager.defaultVibrator
                     val effect = VibrationEffect.createOneShot(200, 255)
                     if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        vibrator.vibrate(effect, android.os.VibrationAttributes.createForUsage(android.os.VibrationAttributes.USAGE_ALARM))
                     } else {
                        vibrator.vibrate(effect)
                     }
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                   vibrator?.vibrate(VibrationEffect.createOneShot(200, 255))
                } else {
                   vibrator?.vibrate(200)
                }
            }
            launch { scale.animateTo(1.6f, animationSpec = tween(4000, easing = LinearOutSlowInEasing)) }
            launch { glowAlpha.animateTo(0.5f, animationSpec = tween(4000)) }
            for (i in 4 downTo 1) { secondsRemaining = i; delay(1000) }

            // HOLD
            instruction = "HOLD"
            subInstruction = "Allow oxygen to circulate"
            if (hapticsEnabled) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                     val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as android.os.VibratorManager
                     val vibrator = vibratorManager.defaultVibrator
                     val effect = VibrationEffect.createOneShot(100, 255)
                     if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        vibrator.vibrate(effect, android.os.VibrationAttributes.createForUsage(android.os.VibrationAttributes.USAGE_ALARM))
                     } else {
                        vibrator.vibrate(effect)
                     }
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                   vibrator?.vibrate(VibrationEffect.createOneShot(100, 255))
                } else {
                   vibrator?.vibrate(100)
                }
            }
            launch { glowAlpha.animateTo(0.8f, animationSpec = tween(1000)) }
            for (i in 7 downTo 1) { secondsRemaining = i; delay(1000) }

            // EXHALE
            instruction = "EXHALE"
            subInstruction = "Purge system toxins"
            if (hapticsEnabled) {
                 if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                     val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as android.os.VibratorManager
                     val vibrator = vibratorManager.defaultVibrator
                     val effect = VibrationEffect.createOneShot(200, 255)
                     if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        vibrator.vibrate(effect, android.os.VibrationAttributes.createForUsage(android.os.VibrationAttributes.USAGE_ALARM))
                     } else {
                        vibrator.vibrate(effect)
                     }
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                   vibrator?.vibrate(VibrationEffect.createOneShot(200, 255))
                } else {
                   vibrator?.vibrate(200)
                }
            }
            stressLevel = (stressLevel - 0.15f).coerceAtLeast(0f)
            launch { scale.animateTo(1f, animationSpec = tween(8000, easing = FastOutLinearInEasing)) }
            launch { glowAlpha.animateTo(0.0f, animationSpec = tween(8000)) }
            for (i in 8 downTo 1) { secondsRemaining = i; delay(1000) }
        }
    }

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // 3. Ambient Background Grid (Cyberpunk feel)
            BackgroundGrid()

            // SAFE COLUMN LAYOUT
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // HEADER
                Column(
                    modifier = Modifier
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
                            style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray, fontFamily = FontFamily.Monospace)
                        )
                        Text(
                            text = systemStatus,
                            style = MaterialTheme.typography.labelMedium.copy(color = statusColor, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { stressLevel },
                        modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                        color = statusColor,
                        trackColor = MaterialTheme.colorScheme.surface,
                    )
                }

                // SPACER 1
                Spacer(modifier = Modifier.weight(1f))

                // BREATHING CORE
                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(320.dp)) {
                    Canvas(modifier = Modifier.fillMaxSize().scale(1.2f)) {
                        rotate(rotation) {
                            drawArc(
                                brush = Brush.sweepGradient(colors = listOf(Color.Transparent, coreColor.copy(alpha = 0.5f), Color.Transparent)),
                                startAngle = 0f, sweepAngle = 120f, useCenter = false,
                                style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
                            )
                        }
                        val bracketSize = 20.dp.toPx(); val stroke = 2.dp.toPx(); val offset = 10.dp.toPx()
                        drawLine(coreColor, Offset(offset, offset + bracketSize), Offset(offset, offset), stroke)
                        drawLine(coreColor, Offset(offset, offset), Offset(offset + bracketSize, offset), stroke)
                        drawLine(coreColor, Offset(size.width - offset - bracketSize, offset), Offset(size.width - offset, offset), stroke)
                        drawLine(coreColor, Offset(size.width - offset, offset), Offset(size.width - offset, offset + bracketSize), stroke)
                        drawLine(coreColor, Offset(offset, size.height - offset - bracketSize), Offset(offset, size.height - offset), stroke)
                        drawLine(coreColor, Offset(offset, size.height - offset), Offset(offset + bracketSize, size.height - offset), stroke)
                        drawLine(coreColor, Offset(size.width - offset - bracketSize, size.height - offset), Offset(size.width - offset, size.height - offset), stroke)
                        drawLine(coreColor, Offset(size.width - offset, size.height - offset), Offset(size.width - offset, size.height - offset - bracketSize), stroke)
                    }
                    Box(modifier = Modifier.size(140.dp).scale(scale.value).background(brush = Brush.radialGradient(colors = listOf(coreColor, coreColor.copy(alpha = 0.2f), Color.Transparent)), shape = CircleShape))
                    if (secondsRemaining > 0) {
                        Text(
                            text = secondsRemaining.toString(),
                            style = MaterialTheme.typography.displayLarge.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = Color.White),
                            modifier = Modifier.scale(scale.value)
                        )
                    }
                }

                // INSTRUCTIONS (Stacked below core)
                Spacer(modifier = Modifier.height(24.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = instruction,
                        style = MaterialTheme.typography.displayMedium.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, letterSpacing = 4.sp),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = subInstruction.uppercase(),
                        style = MaterialTheme.typography.labelMedium.copy(fontFamily = FontFamily.Monospace, color = MaterialTheme.colorScheme.primary),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }

                // SPACER 2
                Spacer(modifier = Modifier.weight(1f))

                // FOOTER CONTROLS
                Row(
                    modifier = Modifier.padding(bottom = 48.dp, top = 24.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ControlButton(icon = Icons.Rounded.Vibration, isActive = hapticsEnabled, onClick = { hapticsEnabled = !hapticsEnabled })
                    Spacer(modifier = Modifier.width(24.dp))
                    Button(
                        onClick = onNavigateBack,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.1f)),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.height(50.dp)
                    ) {
                        Text(text = "ABORT PROTOCOL", color = MaterialTheme.colorScheme.error, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                    }
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
            .background(if (isActive) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface)
            .border(1.dp, if (isActive) MaterialTheme.colorScheme.primary else Color.Gray, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isActive) MaterialTheme.colorScheme.primary else Color.Gray
        )
    }
}

@Composable
fun BackgroundGrid() {
    val gridColor = MaterialTheme.colorScheme.primary
    Canvas(modifier = Modifier
        .fillMaxSize()
        .alpha(0.1f)) {
        val step = 40.dp.toPx()
        val width = size.width
        val height = size.height

        for (x in 0..width.toInt() step step.toInt()) {
            drawLine(
                color = gridColor,
                start = Offset(x.toFloat(), 0f),
                end = Offset(x.toFloat(), height),
                strokeWidth = 1f
            )
        }

        for (y in 0..height.toInt() step step.toInt()) {
            drawLine(
                color = gridColor,
                start = Offset(0f, y.toFloat()),
                end = Offset(width, y.toFloat()),
                strokeWidth = 1f
            )
        }
    }
}