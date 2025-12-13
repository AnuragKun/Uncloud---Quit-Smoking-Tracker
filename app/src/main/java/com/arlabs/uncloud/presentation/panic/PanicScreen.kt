package com.arlabs.uncloud.presentation.panic

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun PanicScreen(onNavigateBack: () -> Unit) {
    // 1. Core State
    var instruction by remember { mutableStateOf("Ready") }
    var subInstruction by remember { mutableStateOf("Relax your shoulders") }

    // Animation Values
    val scale = remember { Animatable(1f) }
    val glowAlpha = remember { Animatable(0.0f) }
    val progress = remember { Animatable(0f) } // For the ring progress

    // Haptics for "Eyes Closed" usage
    val view = LocalView.current

    // Brand Colors
    val calmCyan = Color(0xFF00E5FF)
    val deepBg = Color(0xFF0F1216)

    // 2. The 4-7-8 Breathing Logic Loop
    LaunchedEffect(Unit) {
        // Initial settle time
        delay(500)

        while (true) {
            // --- INHALE (4 Seconds) ---
            instruction = "Inhale"
            subInstruction = "Fill your lungs completely"
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)

            launch { scale.animateTo(1.5f, animationSpec = tween(4000, easing = LinearOutSlowInEasing)) }
            launch { glowAlpha.animateTo(0.6f, animationSpec = tween(4000)) }
            launch { progress.animateTo(1f, animationSpec = tween(4000)) }
            delay(4000)

            // --- HOLD (7 Seconds) ---
            instruction = "Hold"
            subInstruction = "Keep the oxygen inside"
            view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)

            // Keep scale/glow steady, maybe slight pulse?
            launch { glowAlpha.animateTo(0.8f, animationSpec = tween(1000)) } // Peak glow
            delay(7000)

            // --- EXHALE (8 Seconds) ---
            instruction = "Exhale"
            subInstruction = "Slowly through your mouth"
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)

            launch { scale.animateTo(1f, animationSpec = tween(8000, easing = FastOutLinearInEasing)) }
            launch { glowAlpha.animateTo(0.0f, animationSpec = tween(8000)) }
            launch { progress.animateTo(0f, animationSpec = tween(8000)) }
            delay(8000)
        }
    }

    Scaffold(containerColor = deepBg) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // 3. Ambient Background Glow
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(Color(0xFF004D40).copy(alpha = 0.3f), deepBg),
                            center = Offset.Unspecified,
                            radius = 1200f
                        )
                    )
            )

            // Close Button
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(24.dp)
                    .background(Color.White.copy(alpha = 0.1f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color.White
                )
            }

            // 4. Main Content
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Text Area
                Text(
                    text = instruction,
                    style = MaterialTheme.typography.displayMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )

                Text(
                    text = subInstruction,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.5f),
                    modifier = Modifier.padding(top = 8.dp)
                )

                Spacer(modifier = Modifier.height(60.dp))

                // 5. The Living Circle Visualizer
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(300.dp)
                ) {
                    // Outer Glow (Breathing Aura)
                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .scale(scale.value)
                            .alpha(glowAlpha.value)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(calmCyan.copy(alpha = 0.5f), Color.Transparent)
                                ),
                                CircleShape
                            )
                    )

                    // The Core Circle
                    Box(
                        modifier = Modifier
                            .size(120.dp) // Base size
                            .scale(scale.value)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(calmCyan, Color(0xFF00BFA5))
                                ),
                                CircleShape
                            )
                    )

                    // Optional: Progress Ring for Hold/Timing visual
                    Canvas(modifier = Modifier.size(260.dp)) {
                        drawArc(
                            color = Color.White.copy(alpha = 0.1f),
                            startAngle = 0f,
                            sweepAngle = 360f,
                            useCenter = false,
                            style = Stroke(width = 4.dp.toPx())
                        )
                    }
                }

                Spacer(modifier = Modifier.height(50.dp))

                // Footer Quote
                Text(
                    text = "This craving will pass in minutes.",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White.copy(alpha = 0.3f)
                )
            }
        }
    }
}