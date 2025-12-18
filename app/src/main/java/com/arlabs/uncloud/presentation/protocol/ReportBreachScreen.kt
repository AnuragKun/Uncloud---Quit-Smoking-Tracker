package com.arlabs.uncloud.presentation.protocol

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.arlabs.uncloud.presentation.home.HomeViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

// --- THEME CONSTANTS ---
// --- THEME CONSTANTS REMOVED (Now using MaterialTheme) ---
// private val SysRed...

@Composable
fun ReportBreachScreen(
    onNavigateBack: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    // 1. Data & State
    var selectedTrigger by remember { mutableStateOf<String?>(null) }
    val triggers = listOf(
        // Situational
        "WORK", "SOCIAL", "DRIVING", "ALCOHOL", 
        "COFFEE", "AFTER MEAL", "WAKING UP",
        // Emotional
        "STRESS", "ANXIETY", "ANGER", "BOREDOM", 
        "SADNESS", "LONELINESS", "FATIGUE", "HAPPINESS",
        // Physical
        "CRAVING", "HUNGER", "PAIN"
    )

    // Red Gradient Background
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(MaterialTheme.colorScheme.background, Color.Black)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- HEADER ---
            Spacer(modifier = Modifier.height(32.dp))
            Icon(
                imageVector = Icons.Rounded.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "PROTOCOL BREACH",
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 2.sp
                )
            )
            Text(
                text = "SYSTEM DIAGNOSTICS REQUIRED",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color.Gray,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
            )

            Spacer(modifier = Modifier.height(48.dp))

            // --- INSTRUCTION ---
            Text(
                text = "IDENTIFY FAILURE POINT:",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                ),
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- TRIGGER GRID ---
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(triggers) { trigger ->
                    val isSelected = (selectedTrigger == trigger)
                    Box(
                        modifier = Modifier
                            .height(56.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.error.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface
                            )
                            .border(
                                width = 1.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .pointerInput(Unit) {
                                detectTapGestures { selectedTrigger = trigger }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = trigger,
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = if (isSelected) MaterialTheme.colorScheme.error else Color.Gray,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }

            // --- FOOTER ACTION ---
            Text(
                text = "WARNING: Initiating reboot will reset your active streak to Day 0.",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // The "Hold to Reset" Button
            HoldToConfirmButton(
                isEnabled = (selectedTrigger != null),
                onConfirmed = {
                    selectedTrigger?.let { t ->
                        viewModel.reportBreach(t)
                        onNavigateBack()
                    }
                }
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun HoldToConfirmButton(
    isEnabled: Boolean,
    onConfirmed: () -> Unit
) {
    var progress by remember { mutableFloatStateOf(0f) }
    val scope = rememberCoroutineScope()
    val view = LocalView.current // For Haptic Feedback

    // Smooth animation for the fill
    val animatedProgress by animateFloatAsState(targetValue = progress, label = "progress")

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colorScheme.error.copy(alpha = 0.1f)) // Dark Red bg
            .border(
                1.dp,
                if(isEnabled) MaterialTheme.colorScheme.error else Color.Transparent,
                RoundedCornerShape(4.dp)
            )
            .pointerInput(isEnabled) {
                if (isEnabled) {
                    detectTapGestures(
                        onPress = {
                            val duration = 2000L // 2 seconds hold
                            val startTime = System.currentTimeMillis()

                            // Start progress coroutine
                            val progressJob = scope.launch {
                                while (isActive && progress < 1f) {
                                    val elapsed = System.currentTimeMillis() - startTime
                                    progress = (elapsed / duration.toFloat()).coerceIn(0f, 1f)

                                    // Haptic tick every 10%
                                    if ((progress * 100).toInt() % 10 == 0) {
                                        // view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
                                    }

                                    delay(16) // ~60fps update
                                }
                            }

                            // Wait for release
                            tryAwaitRelease()

                            // Released:
                            progressJob.cancel()

                            if (progress >= 1f) {
                                view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                                onConfirmed()
                            } else {
                                // Failed: Reset progress
                                progress = 0f
                            }
                        }
                    )
                }
            },
        contentAlignment = Alignment.Center
    ) {
        // Progress Fill
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(animatedProgress)
                .align(Alignment.CenterStart)
                .background(MaterialTheme.colorScheme.error)
        )

        // Text Label
        Text(
            text = when {
                !isEnabled -> "SELECT FAILURE POINT"
                progress >= 1f -> "SYSTEM REBOOTING..."
                progress > 0f -> "HOLD TO CONFIRM..."
                else -> "HOLD TO REBOOT SYSTEM"
            },
            style = MaterialTheme.typography.titleMedium.copy(
                color = if (animatedProgress > 0.5f) Color.Black else MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
        )
    }
}