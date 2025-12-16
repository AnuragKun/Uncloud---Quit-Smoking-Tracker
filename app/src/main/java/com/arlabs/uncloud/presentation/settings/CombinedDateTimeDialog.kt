package com.arlabs.uncloud.presentation.settings.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CombinedDateTimeDialog(
    initialMillis: Long,
    onDismiss: () -> Unit,
    onConfirm: (Long) -> Unit
) {
    // Logic setup
    val initialDateTime = Instant.ofEpochMilli(initialMillis).atZone(ZoneId.systemDefault())
    val initialDateMillis = initialDateTime.toLocalDate()
        .atStartOfDay(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()

    var selectedTab by remember { mutableIntStateOf(0) } // 0 for Date, 1 for Time

    // Theme Colors
    val sysCyan = Color(0xFF00E5FF)
    val sysDark = Color(0xFF0D1117)
    val sysPanel = Color(0xFF161B22)

    // Picker States
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDateMillis,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val selectedDate = Instant.ofEpochMilli(utcTimeMillis)
                    .atZone(ZoneId.of("UTC"))
                    .toLocalDate()
                val today = java.time.LocalDate.now()
                return !selectedDate.isAfter(today) 
            }

            override fun isSelectableYear(year: Int): Boolean {
                 return year <= java.time.LocalDate.now().year
             }
        }
    )
    val timePickerState = rememberTimePickerState(
        initialHour = initialDateTime.hour,
        initialMinute = initialDateTime.minute,
        is24Hour = true
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            shape = RoundedCornerShape(4.dp),
            colors = CardDefaults.cardColors(containerColor = sysDark),
            border = BorderStroke(1.dp, Color(0xFF30363D)),
            modifier = Modifier
                .fillMaxWidth(0.95f)
                // We wrap content height but limit it to avoid screen overflow
                .wrapContentHeight()
                .padding(16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                // Removed the global padding(16.dp) here so DatePicker can touch edges
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {

                // --- PADDED HEADER AREA ---
                Column(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "TEMPORAL CALIBRATION",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = sysCyan
                        ),
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    // Tabs
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .border(1.dp, Color(0xFF30363D), RoundedCornerShape(8.dp))
                            .clip(RoundedCornerShape(8.dp))
                    ) {
                        // Date Tab
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .background(if (selectedTab == 0) sysCyan.copy(alpha = 0.1f) else Color.Transparent)
                                .clickable { selectedTab = 0 }
                        ) {
                            Text(
                                text = "DATE SECTOR",
                                fontFamily = FontFamily.Monospace,
                                fontWeight = if(selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == 0) sysCyan else Color.Gray,
                                fontSize = 12.sp
                            )
                        }

                        // Divider
                        Box(modifier = Modifier.width(1.dp).fillMaxHeight().background(Color(0xFF30363D)))

                        // Time Tab
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .background(if (selectedTab == 1) sysCyan.copy(alpha = 0.1f) else Color.Transparent)
                                .clickable { selectedTab = 1 }
                        ) {
                            Text(
                                text = "TIME MARKER",
                                fontFamily = FontFamily.Monospace,
                                fontWeight = if(selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == 1) sysCyan else Color.Gray,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // --- FULL WIDTH PICKER AREA ---
                if (selectedTab == 0) {
                    // DatePicker gets full width (no padding) to ensure Saturday fits
                    DatePicker(
                        state = datePickerState,
                        showModeToggle = false,
                        title = null,
                        headline = null,
                        // Removed fixed height constraint
                        colors = DatePickerDefaults.colors(
                            containerColor = Color.Transparent,
                            weekdayContentColor = sysCyan,
                            subheadContentColor = Color.Gray,
                            yearContentColor = Color.White,
                            currentYearContentColor = sysCyan,
                            selectedYearContentColor = Color.Black,
                            selectedYearContainerColor = sysCyan,
                            dayContentColor = Color.White,
                            disabledDayContentColor = Color.DarkGray,
                            selectedDayContentColor = Color.Black,
                            selectedDayContainerColor = sysCyan,
                            todayDateBorderColor = sysCyan,
                            todayContentColor = sysCyan
                        )
                    )
                } else {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                    ) {
                        TimePicker(
                            state = timePickerState,
                            colors = TimePickerDefaults.colors(
                                clockDialColor = sysPanel,
                                clockDialSelectedContentColor = Color.Black,
                                clockDialUnselectedContentColor = Color.White,
                                selectorColor = sysCyan,
                                containerColor = sysDark,
                                periodSelectorBorderColor = sysCyan,
                                periodSelectorSelectedContainerColor = sysCyan.copy(alpha = 0.2f),
                                periodSelectorUnselectedContainerColor = Color.Transparent,
                                periodSelectorSelectedContentColor = sysCyan,
                                periodSelectorUnselectedContentColor = Color.Gray,
                                timeSelectorSelectedContainerColor = sysPanel,
                                timeSelectorUnselectedContainerColor = sysPanel,
                                timeSelectorSelectedContentColor = sysCyan,
                                timeSelectorUnselectedContentColor = Color.White
                            )
                        )
                    }
                }

                // --- PADDED FOOTER AREA ---
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("ABORT", color = Color.Gray, fontFamily = FontFamily.Monospace)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val selectedDate = datePickerState.selectedDateMillis
                            if (selectedDate != null) {
                                val localDate = Instant.ofEpochMilli(selectedDate)
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate()

                                val localTime = LocalTime.of(
                                    timePickerState.hour,
                                    timePickerState.minute
                                )

                                val finalDateTime = java.time.LocalDateTime.of(localDate, localTime)
                                val finalMillis = finalDateTime
                                    .atZone(ZoneId.systemDefault())
                                    .toInstant()
                                    .toEpochMilli()

                                // Enforce Validation
                                if (finalMillis > System.currentTimeMillis()) return@Button

                                onConfirm(finalMillis)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = sysCyan),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            "EXECUTE",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}