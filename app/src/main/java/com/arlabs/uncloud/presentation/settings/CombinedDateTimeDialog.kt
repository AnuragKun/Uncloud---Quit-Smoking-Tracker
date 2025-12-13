package com.arlabs.uncloud.presentation.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CombinedDateTimeDialog(initialMillis: Long, onDismiss: () -> Unit, onConfirm: (Long) -> Unit) {
    val initialDateTime = Instant.ofEpochMilli(initialMillis).atZone(ZoneId.systemDefault())
    val initialDateMillis =
            initialDateTime
                    .toLocalDate()
                    .atStartOfDay(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli()

    var selectedTab by remember { mutableIntStateOf(0) } // 0 for Date, 1 for Time

    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialDateMillis)

    val timePickerState =
            rememberTimePickerState(
                    initialHour = initialDateTime.hour,
                    initialMinute = initialDateTime.minute
            )

    // Using basic Dialog or DatePickerDialog as container?
    // Since we need custom content (Tabs), let's use a standard DatePickerDialog container but
    // customize content
    // Or just a full screen Dialog?
    // User asked for "a dialog box". DatePickerDialog has a specific look.
    // Let's build a custom Dialog using android.ui.window.Dialog but styled like M3.
    // Actually, DatePickerDialog is just a container. Let's use standard Dialog composable.

    androidx.compose.ui.window.Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
                shape = MaterialTheme.shapes.extraLarge,
                tonalElevation = 6.dp,
                modifier =
                        Modifier.fillMaxWidth(0.95f) // Slightly less than full width
                                .padding(16.dp),
                color = MaterialTheme.colorScheme.surfaceContainer
        ) {
            Column(
                    horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                        text = "Edit Quit Date & Time",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(top = 24.dp, bottom = 16.dp, start = 16.dp, end = 16.dp)
                )

                TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
                ) {
                    Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            text = { Text("Date") }
                    )
                    Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = { Text("Time") }
                    )
                }

                if (selectedTab == 0) {
                    DatePicker(
                            state = datePickerState,
                            showModeToggle = false,
                            modifier = Modifier.height(400.dp),
                            title = null,
                            headline = null
                    )
                } else {
                    Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.height(400.dp).padding(top = 32.dp)
                    ) { TimePicker(state = timePickerState) }
                }

                Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                        horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(modifier = Modifier.padding(8.dp))
                    Button(
                            onClick = {
                                val selectedDate = datePickerState.selectedDateMillis
                                if (selectedDate != null) {
                                    val localDate =
                                            Instant.ofEpochMilli(selectedDate)
                                                    .atZone(ZoneId.systemDefault())
                                                    .toLocalDate()

                                    val localTime =
                                            LocalTime.of(
                                                    timePickerState.hour,
                                                    timePickerState.minute
                                            )

                                    val finalDateTime =
                                            java.time.LocalDateTime.of(localDate, localTime)
                                    val finalMillis =
                                            finalDateTime
                                                    .atZone(ZoneId.systemDefault())
                                                    .toInstant()
                                                    .toEpochMilli()

                                    onConfirm(finalMillis)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                    ) { Text("Save") }
                }
            }
        }
    }
}
