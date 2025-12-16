package com.arlabs.uncloud.presentation.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

// --- THEME CONSTANTS ---
private val SysCyan = Color(0xFF00E5FF)
private val SysDark = Color(0xFF0D1117)
private val SysPanel = Color(0xFF161B22)
private val SysBorder = Color(0xFF30363D)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeSelectionDialog(
    initialHour: Int,
    initialMinute: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int, Int) -> Unit
) {
    val timePickerState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            shape = RoundedCornerShape(4.dp), // Tech/Sharp corners
            colors = CardDefaults.cardColors(containerColor = SysDark),
            border = BorderStroke(1.dp, SysBorder),
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // HEADER
                Text(
                    text = "SCHEDULE CONFIGURATION",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = SysCyan
                    ),
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                // CUSTOMIZED TIME PICKER
                TimePicker(
                    state = timePickerState,
                    colors = TimePickerDefaults.colors(
                        // Clock Face
                        clockDialColor = SysPanel,
                        clockDialSelectedContentColor = Color.Black,
                        clockDialUnselectedContentColor = Color.White,

                        // Selector (Hand)
                        selectorColor = SysCyan,

                        // Container (Background)
                        containerColor = Color.Transparent,

                        // AM/PM Selector
                        periodSelectorBorderColor = SysCyan,
                        periodSelectorSelectedContainerColor = SysCyan.copy(alpha = 0.2f),
                        periodSelectorUnselectedContainerColor = Color.Transparent,
                        periodSelectorSelectedContentColor = SysCyan,
                        periodSelectorUnselectedContentColor = Color.Gray,

                        // Time Input Box (if in keyboard mode)
                        timeSelectorSelectedContainerColor = SysPanel,
                        timeSelectorUnselectedContainerColor = SysPanel,
                        timeSelectorSelectedContentColor = SysCyan,
                        timeSelectorUnselectedContentColor = Color.White
                    )
                )

                // ACTION BUTTONS
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(
                            text = "ABORT",
                            color = Color.Gray,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Button(
                        onClick = {
                            onConfirm(timePickerState.hour, timePickerState.minute)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SysCyan),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "CONFIRM",
                            color = Color.Black,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}