package com.arlabs.uncloud.presentation.settings

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.arlabs.uncloud.BuildConfig
import com.arlabs.uncloud.domain.model.UserConfig
import com.arlabs.uncloud.presentation.settings.components.CombinedDateTimeDialog

import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
        onNavigateBack: () -> Unit,
        onNavigateToBreach: () -> Unit,
        onNavigateToPrivacy: () -> Unit,

        onNavigateToTerms: () -> Unit,
        onNavigateToOnboarding: () -> Unit,
        viewModel: SettingsViewModel = hiltViewModel()
) {
        val userConfig by viewModel.userConfig.collectAsState()
        val notificationSettings by viewModel.notificationSettings.collectAsState()
        val context = LocalContext.current
        val scrollState = rememberScrollState()

        // Dialog States
        var showEditDetailsDialog by remember { mutableStateOf(false) }
        var showQuitDialog by remember { mutableStateOf(false) } // The Intercept Dialog
        var showDatePickerDialog by remember { mutableStateOf(false) } // The Actual Date Picker
        var showResetDialog by remember { mutableStateOf(false) }
        var showNotificationTimeDialog by remember { mutableStateOf(false) }

        // Cyberpunk Background
        val backgroundBrush = Brush.verticalGradient(
                colors = listOf(Color(0xFF0D1117), Color(0xFF000000))
        )

        Scaffold(
                topBar = {
                        TopAppBar(
                                title = {
                                        Text(
                                                "SYSTEM CONFIG",
                                                style = MaterialTheme.typography.titleMedium.copy(
                                                        fontFamily = FontFamily.Monospace,
                                                        fontWeight = FontWeight.Bold,
                                                        letterSpacing = 2.sp,
                                                        color = Color(0xFF00E5FF)
                                                )
                                        )
                                },
                                navigationIcon = {
                                        IconButton(onClick = onNavigateBack) {
                                                Icon(
                                                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                                        contentDescription = "Back",
                                                        tint = Color.Gray
                                                )
                                        }
                                },
                                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                        )
                },
                containerColor = Color.Transparent
        ) { padding ->
                Box(
                        modifier = Modifier
                                .fillMaxSize()
                                .background(backgroundBrush)
                                .background(backgroundBrush)
                                // .padding(padding)
                ) {
                        Column(
                                modifier = Modifier
                                        .fillMaxSize()
                                        .verticalScroll(scrollState)
                                        .padding(padding)
                                        .padding(horizontal = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(24.dp)
                        ) {
                                Spacer(modifier = Modifier.height(8.dp))

                                // 1. Personal Data
                                SettingsSection(title = "PROTOCOL PARAMETERS") {
                                        SettingsItem(
                                                icon = Icons.Rounded.EditCalendar,
                                                title = "CALIBRATE TIMELINE",
                                                subtitle = "Correct start date due to data entry error",
                                                onClick = { showQuitDialog = true }
                                        )
                                        SettingsItem(
                                                icon = Icons.Rounded.Edit,
                                                title = "RESOURCE DETAILS",
                                                subtitle = "Update cost, usage, and pack size",
                                                onClick = { showEditDetailsDialog = true }
                                        )
                                        SettingsItem(
                                                icon = Icons.Rounded.DeleteForever,
                                                title = "FACTORY RESET",
                                                subtitle = "Wipe all data and restart protocol",
                                                textColor = Color(0xFFFF5252),
                                                iconColor = Color(0xFFFF5252),
                                                onClick = { showResetDialog = true }
                                        )
                                }

                                // 2. App Preferences
                                SettingsSection(title = "SYSTEM NOTIFICATIONS") {
                                        SwitchSettingsItem(
                                                icon = Icons.Rounded.NotificationsActive,
                                                title = "DAILY BRIEFING",
                                                subtitle = "Receive daily motivational transmission",
                                                checked = notificationSettings.dailyMotivationEnabled,
                                                onCheckedChange = {
                                                        viewModel.updateNotificationSettings(
                                                                it,
                                                                notificationSettings.healthMilestonesEnabled,
                                                                notificationSettings.notificationHour,
                                                                notificationSettings.notificationMinute
                                                        )
                                                }
                                        )
                                        SwitchSettingsItem(
                                                icon = Icons.Rounded.HealthAndSafety,
                                                title = "MILESTONE ALERTS",
                                                subtitle = "Notify when biological repairs complete",
                                                checked = notificationSettings.healthMilestonesEnabled,
                                                onCheckedChange = {
                                                        viewModel.updateNotificationSettings(
                                                                notificationSettings.dailyMotivationEnabled,
                                                                it,
                                                                notificationSettings.notificationHour,
                                                                notificationSettings.notificationMinute
                                                        )
                                                }
                                        )
                                        SettingsItem(
                                                icon = Icons.Rounded.Schedule,
                                                title = "BRIEFING SCHEDULE",
                                                subtitle = String.format(
                                                        Locale.getDefault(),
                                                        "%02d:%02d",
                                                        notificationSettings.notificationHour,
                                                        notificationSettings.notificationMinute
                                                ),
                                                onClick = { showNotificationTimeDialog = true }
                                        )
                                }

                                // 3. Support & Feedback
                                SettingsSection(title = "EXTERNAL LINKS") {
                                        SettingsItem(
                                                icon = Icons.Rounded.Star,
                                                title = "RATE PROTOCOL",
                                                subtitle = "Support development on Google Play",
                                                onClick = {
                                                        try {
                                                                context.startActivity(
                                                                        Intent(
                                                                                Intent.ACTION_VIEW,
                                                                                Uri.parse("market://details?id=${context.packageName}")
                                                                        )
                                                                )
                                                        } catch (e: Exception) {
                                                                Toast.makeText(context, "Link failure", Toast.LENGTH_SHORT).show()
                                                        }
                                                }
                                        )
                                        SettingsItem(
                                                icon = Icons.Rounded.Share,
                                                title = "SHARE ACCESS",
                                                subtitle = "Distribute app link to other users",
                                                onClick = {
                                                        val shareIntent = Intent().apply {
                                                                action = Intent.ACTION_SEND
                                                                putExtra(
                                                                        Intent.EXTRA_TEXT,
                                                                        "I am using the Uncloud Protocol to track my recovery. Join the system here: https://play.google.com/store/apps/details?id=${context.packageName}"
                                                                )
                                                                type = "text/plain"
                                                        }
                                                        context.startActivity(Intent.createChooser(shareIntent, "TRANSMIT VIA"))
                                                }
                                        )
                                        SettingsItem(
                                                icon = Icons.Rounded.BugReport,
                                                title = "REPORT GLITCH",
                                                subtitle = "Contact developers directly",
                                                onClick = {
                                                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                                                                data = Uri.parse("mailto:")
                                                                putExtra(Intent.EXTRA_EMAIL, arrayOf("anuragrana050305@gmail.com"))
                                                                putExtra(Intent.EXTRA_SUBJECT, "UNCLOUD // User Feedback")
                                                        }
                                                        try {
                                                                context.startActivity(intent)
                                                        } catch (e: Exception) {
                                                                Toast.makeText(context, "No email client found", Toast.LENGTH_SHORT).show()
                                                        }
                                                }
                                        )
                                }

                                // 4. About & Legal
                                SettingsSection(title = "LEGAL & VERSION") {
                                        SettingsItem(
                                                icon = Icons.Rounded.PrivacyTip,
                                                title = "PRIVACY POLICY",
                                                subtitle = "Review data handling protocols",
                                                onClick = onNavigateToPrivacy
                                        )
                                        SettingsItem(
                                                icon = Icons.Rounded.Gavel,
                                                title = "TERMS OF SERVICE",
                                                subtitle = "User agreement and liability",
                                                onClick = onNavigateToTerms
                                        )
                                        SettingsItem(
                                                icon = Icons.Rounded.Info,
                                                title = "BUILD VERSION",
                                                subtitle = "v${BuildConfig.VERSION_NAME}",
                                                onClick = {}
                                        )
                                }
                                Spacer(modifier = Modifier.height(32.dp))
                        }
                }

                // --- DIALOGS ---

                if (showQuitDialog) {
                        AlertDialog(
                                onDismissRequest = { showQuitDialog = false },
                                containerColor = Color(0xFF0D1117),
                                icon = { Icon(Icons.Rounded.Warning, null, tint = Color(0xFF00E5FF)) },
                                title = { Text("SYSTEM CALIBRATION", fontFamily = FontFamily.Monospace, color = Color.White) },
                                text = {
                                        Text(
                                                "Why are you modifying the timeline?\n\n" +
                                                        "• Choose 'CORRECTION' if you entered the wrong date originally.\n" +
                                                        "• Choose 'RELAPSE' if you broke the protocol today.",
                                                color = Color.Gray
                                        )
                                },
                                confirmButton = {
                                        TextButton(
                                                onClick = {
                                                        showQuitDialog = false
                                                        showDatePickerDialog = true
                                                }
                                        ) {
                                                Text("CORRECTION", color = Color(0xFF00E5FF), fontWeight = FontWeight.Bold)
                                        }
                                },
                                dismissButton = {
                                        TextButton(
                                                onClick = {
                                                        showQuitDialog = false
                                                        onNavigateToBreach()
                                                }
                                        ) {
                                                Text("I RELAPSED", color = Color(0xFFFF5252), fontWeight = FontWeight.Bold)
                                        }
                                }
                        )
                }

                if (showDatePickerDialog) {
                        CombinedDateTimeDialog(
                                initialMillis = userConfig?.quitTimestamp ?: System.currentTimeMillis(),
                                onDismiss = { showDatePickerDialog = false },
                                onConfirm = { timestamp ->
                                        viewModel.updateQuitDate(timestamp)
                                        showDatePickerDialog = false
                                }
                        )
                }

                if (showNotificationTimeDialog) {
                        TimeSelectionDialog(
                                initialHour = notificationSettings.notificationHour,
                                initialMinute = notificationSettings.notificationMinute,
                                onDismiss = { showNotificationTimeDialog = false },
                                onConfirm = { hour, minute ->
                                        viewModel.updateNotificationSettings(
                                                notificationSettings.dailyMotivationEnabled,
                                                notificationSettings.healthMilestonesEnabled,
                                                hour,
                                                minute
                                        )
                                        showNotificationTimeDialog = false
                                }
                        )
                }

                if (showEditDetailsDialog && userConfig != null) {
                        EditDetailsDialog(
                                currentConfig = userConfig!!,
                                onDismiss = { showEditDetailsDialog = false },
                                onConfirm = { cost, day, pack, currency ->
                                        viewModel.updateUserConfig(cost, day, pack, currency)
                                        showEditDetailsDialog = false
                                }
                        )
                }

                if (showResetDialog) {
                        AlertDialog(
                                onDismissRequest = { showResetDialog = false },
                                containerColor = Color(0xFF0D1117),
                                title = { Text("CONFIRM RESET", fontFamily = FontFamily.Monospace, color = Color.White) },
                                text = {
                                        Text(
                                                "WARNING: This will permanently delete all logs, streaks, and achievements. This action cannot be undone.",
                                                color = Color.Gray
                                        )
                                },
                                confirmButton = {
                                        TextButton(
                                                onClick = {
                                                        viewModel.resetProgress()
                                                        showResetDialog = false
                                                        onNavigateToOnboarding()
                                                }
                                        ) { Text("WIPE DATA", color = Color(0xFFFF5252), fontWeight = FontWeight.Bold) }
                                },
                                dismissButton = {
                                        TextButton(onClick = { showResetDialog = false }) {
                                                Text("CANCEL", color = Color.White)
                                        }
                                }
                        )
                }
        }
}

// --- SUB-COMPONENTS ---

@Composable
fun SettingsSection(title: String, content: @Composable () -> Unit) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                        text = title,
                        style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp,
                                fontFamily = FontFamily.Monospace
                        ),
                        color = Color(0xFF00E5FF),
                        modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
                )
                // Replaced Card with a transparent Column for cleaner look
                Column(
                        modifier = Modifier
                                .background(Color(0xFF161B22), RoundedCornerShape(12.dp))
                ) {
                        content()
                }
        }
}

@Composable
fun SettingsItem(
        icon: ImageVector,
        title: String,
        subtitle: String,
        onClick: () -> Unit,
        textColor: Color = Color.White,
        iconColor: Color = Color.Gray
) {
        Row(
                modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onClick)
                        .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
                Icon(imageVector = icon, contentDescription = null, tint = iconColor)
                Column(modifier = Modifier.weight(1f)) {
                        Text(
                                text = title,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                ),
                                color = textColor
                        )
                        Text(
                                text = subtitle,
                                style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray)
                        )
                }
                Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                        contentDescription = null,
                        tint = Color.DarkGray,
                        modifier = Modifier.size(16.dp)
                )
        }
        // Subtle separator instead of full divider
        Box(
                modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color(0xFF252A30))
        )
}

@Composable
fun SwitchSettingsItem(
        icon: ImageVector,
        title: String,
        subtitle: String,
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit
) {
        Row(
                modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
                Icon(imageVector = icon, contentDescription = null, tint = Color.Gray)
                Column(modifier = Modifier.weight(1f)) {
                        Text(
                                text = title,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                ),
                                color = Color.White
                        )
                        Text(
                                text = subtitle,
                                style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray)
                        )
                }
                Switch(
                        checked = checked,
                        onCheckedChange = onCheckedChange,
                        colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFF00E5FF),
                                checkedTrackColor = Color(0xFF004D40),
                                uncheckedThumbColor = Color.Gray,
                                uncheckedTrackColor = Color.DarkGray
                        )
                )
        }
        Box(
                modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color(0xFF252A30))
        )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditDetailsDialog(
        currentConfig: UserConfig,
        onDismiss: () -> Unit,
        onConfirm: (Double, Int, Int, String) -> Unit
) {
        var costText by remember { mutableStateOf(currentConfig.costPerPack.toString()) }
        var cigsPerDayText by remember { mutableStateOf(currentConfig.cigarettesPerDay.toString()) }
        var cigsInPackText by remember { mutableStateOf(currentConfig.cigarettesInPack.toString()) }
        var currencyText by remember { mutableStateOf(currentConfig.currency) }

        AlertDialog(
                onDismissRequest = onDismiss,
                containerColor = Color(0xFF0D1117),
                title = {
                        Text(
                                "UPDATE PARAMETERS",
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                        )
                },
                text = {
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                // Cost Input
                                SystemTextField(
                                        value = costText,
                                        onValueChange = { costText = it },
                                        label = "Cost per Pack"
                                )
                                // Cigs per Day
                                SystemTextField(
                                        value = cigsPerDayText,
                                        onValueChange = { cigsPerDayText = it },
                                        label = "Cigarettes per Day"
                                )
                                // Pack Size
                                SystemTextField(
                                        value = cigsInPackText,
                                        onValueChange = { cigsInPackText = it },
                                        label = "Cigarettes in Pack"
                                )

                                var showCurrencyDialog by remember { mutableStateOf(false) }

                                // Currency Selector
                                OutlinedTextField(
                                        value = currencyText,
                                        onValueChange = {},
                                        label = { Text("Currency", fontFamily = FontFamily.Monospace) },
                                        readOnly = true,
                                        trailingIcon = {
                                                Icon(Icons.Rounded.ArrowDropDown, null, tint = Color.Gray)
                                        },
                                        modifier = Modifier.fillMaxWidth().clickable { showCurrencyDialog = true },
                                        enabled = false,
                                        colors = OutlinedTextFieldDefaults.colors(
                                                disabledTextColor = Color.White,
                                                disabledLabelColor = Color.Gray,
                                                disabledBorderColor = Color(0xFF30363D),
                                                disabledTrailingIconColor = Color.Gray
                                        )
                                )

                                if (showCurrencyDialog) {
                                        CurrencySelectionDialog(
                                                onDismiss = { showCurrencyDialog = false },
                                                onCurrencySelected = { currency ->
                                                        currencyText = currency.symbol
                                                        showCurrencyDialog = false
                                                }
                                        )
                                }
                        }
                },
                confirmButton = {
                        Button(
                                onClick = {
                                        val cost = costText.toDoubleOrNull() ?: currentConfig.costPerPack
                                        val day = cigsPerDayText.toIntOrNull() ?: currentConfig.cigarettesPerDay
                                        val pack = cigsInPackText.toIntOrNull() ?: currentConfig.cigarettesInPack
                                        onConfirm(cost, day, pack, currencyText)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF))
                        ) {
                                Text("SAVE", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                },
                dismissButton = {
                        TextButton(onClick = onDismiss) {
                                Text("CANCEL", color = Color.Gray)
                        }
                }
        )
}

@Composable
fun SystemTextField(
        value: String,
        onValueChange: (String) -> Unit,
        label: String
) {
        OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                label = { Text(label, fontFamily = FontFamily.Monospace, fontSize = 12.sp) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = Color(0xFF00E5FF),
                        unfocusedLabelColor = Color.Gray,
                        focusedBorderColor = Color(0xFF00E5FF),
                        unfocusedBorderColor = Color(0xFF30363D),
                        cursorColor = Color(0xFF00E5FF)
                ),
                modifier = Modifier.fillMaxWidth()
        )
}