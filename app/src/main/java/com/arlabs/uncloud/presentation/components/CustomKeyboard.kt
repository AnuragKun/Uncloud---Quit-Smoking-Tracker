package com.arlabs.uncloud.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Backspace
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Theme Constants (Matching your other files)
private val SysCyan = Color(0xFF00E5FF)
private val SysPanel = Color(0xFF161B22)
private val SysBorder = Color(0xFF30363D)

@Composable
fun CustomNumericKeyboard(
    onNumberClick: (String) -> Unit,
    onBackspaceClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp) // Spacing between rows
    ) {
        // Row 1
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SystemKey(symbol = "1", modifier = Modifier.weight(1f)) {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onNumberClick("1")
            }
            SystemKey(symbol = "2", modifier = Modifier.weight(1f)) {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onNumberClick("2")
            }
            SystemKey(symbol = "3", modifier = Modifier.weight(1f)) {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onNumberClick("3")
            }
        }

        // Row 2
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SystemKey(symbol = "4", modifier = Modifier.weight(1f)) {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onNumberClick("4")
            }
            SystemKey(symbol = "5", modifier = Modifier.weight(1f)) {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onNumberClick("5")
            }
            SystemKey(symbol = "6", modifier = Modifier.weight(1f)) {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onNumberClick("6")
            }
        }

        // Row 3
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SystemKey(symbol = "7", modifier = Modifier.weight(1f)) {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onNumberClick("7")
            }
            SystemKey(symbol = "8", modifier = Modifier.weight(1f)) {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onNumberClick("8")
            }
            SystemKey(symbol = "9", modifier = Modifier.weight(1f)) {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onNumberClick("9")
            }
        }

        // Row 4 (Empty - 0 - Backspace)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            // Empty placeholder to balance grid
            Spacer(modifier = Modifier.weight(1f))

            SystemKey(symbol = "0", modifier = Modifier.weight(1f)) {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onNumberClick("0")
            }

            // Backspace Key
            SystemActionKey(modifier = Modifier.weight(1f)) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onBackspaceClick()
            }
        }
    }
}

@Composable
private fun SystemKey(
    symbol: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(56.dp) // Taller, easier to hit
            .clip(RoundedCornerShape(8.dp))
            .background(SysPanel)
            .border(1.dp, SysBorder, RoundedCornerShape(8.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(color = SysCyan),
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = symbol,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = Color.White,
                fontSize = 24.sp
            )
        )
    }
}

@Composable
private fun SystemActionKey(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(56.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.Transparent) // Transparent to distinguish from numbers
            .border(1.dp, Color(0xFFEF5350).copy(alpha = 0.5f), RoundedCornerShape(8.dp)) // Red border for delete
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(color = Color(0xFFEF5350)),
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Rounded.Backspace,
            contentDescription = "Backspace",
            tint = Color(0xFFEF5350) // Red tint for destructive action
        )
    }
}