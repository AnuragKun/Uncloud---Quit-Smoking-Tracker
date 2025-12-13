package com.arlabs.uncloud.presentation.protocol

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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.arlabs.uncloud.presentation.home.HomeViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.isActive

@Composable
fun ReportBreachScreen(
    onNavigateBack: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    // 1. Data & State
    var selectedTrigger by remember { mutableStateOf<String?>(null) }
    val triggers = listOf(
        "Stress", "Alcohol", "Social Pressure",
        "Boredom", "Routine", "Argument",
        "Cravings", "Other"
    )

    // Theme Colors
    val alertRed = Color(0xFFEF5350)
    val bgDark = Color(0xFF0F1216)
    val cardBg = Color(0xFF161B22)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgDark)
            .padding(24.dp)
    ) {
        // --- HEADER ---
        Spacer(modifier = Modifier.height(32.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Rounded.Warning,
                contentDescription = null,
                tint = alertRed,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "PROTOCOL BREACH",
                    color = alertRed,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
                Text(
                    text = "SYSTEM DIAGNOSTICS",
                    color = Color.Gray,
                    fontSize = 10.sp,
                    letterSpacing = 1.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "IDENTIFY FAILURE POINT",
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
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
                        .height(50.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) alertRed.copy(alpha = 0.15f) else cardBg)
                        .border(
                            width = 1.dp,
                            color = if (isSelected) alertRed else Color(0xFF252A30),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .pointerInput(Unit) {
                            detectTapGestures { selectedTrigger = trigger }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = trigger,
                        color = if (isSelected) alertRed else Color(0xFF8B9BB4),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // --- FOOTER ACTION ---
        Text(
            text = "Initiating reboot will reset your active streak.",
            color = Color.Gray,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
        )

        // The "Hold to Reset" Button
        HoldToConfirmButton(
            isEnabled = (selectedTrigger != null),
            alertRed = alertRed,
            onConfirmed = {
                selectedTrigger?.let { t ->
                    viewModel.reportBreach(t) // Assuming this function exists in VM
                    onNavigateBack()
                }
            }
        )
        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun HoldToConfirmButton(
    isEnabled: Boolean,
    alertRed: Color,
    onConfirmed: () -> Unit
) {
    var isHolding by remember { mutableStateOf(false) }
    var progress by remember { mutableFloatStateOf(0f) }
    val scope = rememberCoroutineScope()

    // Visual feedback for the bar fill
    val animatedProgress by animateFloatAsState(targetValue = progress, label = "progress")

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF1E0F0F))
            .border(1.dp, if(isEnabled) alertRed.copy(alpha = 0.5f) else Color.Transparent, RoundedCornerShape(12.dp))
            .pointerInput(isEnabled) {
                if (isEnabled) {
                    detectTapGestures(
                        onPress = {
                            isHolding = true
                            val duration = 3000L
                            val startTime = System.currentTimeMillis()

                            // Launch ticker to update progress while holding
                            val progressJob = scope.launch {
                                while (isActive && progress < 1f) {
                                    val elapsed = System.currentTimeMillis() - startTime
                                    progress = (elapsed / duration.toFloat()).coerceIn(0f, 1f)
                                    if (progress >= 1f) {
                                        // Completed! 
                                    }
                                    delay(16)
                                }
                            }

                            // Wait for user to lift finger
                            tryAwaitRelease()
                            
                            // Finger lifted. Cancel ticker.
                            progressJob.cancel()

                            if (progress >= 1f) {
                                onConfirmed()
                            } else {
                                // Failed to hold long enough
                                progress = 0f
                            }
                            isHolding = false
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
                .fillMaxWidth(animatedProgress) // Use animated for smooth visual catch-up
                .align(Alignment.CenterStart)
                .background(alertRed)
        )

        // Text Label
        Text(
            text = when {
                progress >= 1f -> "SYSTEM REBOOTING..."
                progress > 0f -> "HOLD (${3 - (progress * 3).toInt()}s)..."
                else -> "HOLD TO REBOOT SYSTEM"
            },
            color = if (animatedProgress > 0.5f) Color.Black else alertRed,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
    }
}