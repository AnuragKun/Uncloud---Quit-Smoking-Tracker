package com.arlabs.uncloud.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CustomNumericKeyboard(
        onNumberClick: (String) -> Unit,
        onBackspaceClick: () -> Unit,
        modifier: Modifier = Modifier
) {
    Column(
            modifier = modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        val keys = listOf(listOf("7", "8", "9"), listOf("4", "5", "6"), listOf("1", "2", "3"))

        keys.forEach { row ->
            Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                row.forEach { key ->
                    KeyboardKey(
                            text = key,
                            modifier = Modifier.weight(1f),
                            onClick = { onNumberClick(key) }
                    )
                }
            }
        }

        // Last row: dot (optional), 0, backspace
        Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Placeholder for alignment or dot if needed later
            Box(modifier = Modifier.weight(1f))

            KeyboardKey(
                    text = "0",
                    modifier = Modifier.weight(1f),
                    onClick = { onNumberClick("0") }
            )

            Box(
                    modifier =
                            Modifier.weight(1f)
                                    .height(60.dp)
                                    .clip(RoundedCornerShape(25.dp))
                                    .background(
                                            Color.DarkGray.copy(alpha = 0.3f)
                                    ) // Darker for function keys
                                    .clickable { onBackspaceClick() },
                    contentAlignment = Alignment.Center
            ) {
                Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Backspace",
                        tint = Color.White
                )
            }
        }
    }
}

@Composable
fun KeyboardKey(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
            modifier =
                    modifier.height(60.dp)
                            .clip(RoundedCornerShape(25.dp))
                            .background(Color.DarkGray.copy(alpha = 0.3f))
                            .clickable { onClick() },
            contentAlignment = Alignment.Center
    ) { Text(text = text, fontSize = 24.sp, fontWeight = FontWeight.Medium, color = Color.White) }
}
