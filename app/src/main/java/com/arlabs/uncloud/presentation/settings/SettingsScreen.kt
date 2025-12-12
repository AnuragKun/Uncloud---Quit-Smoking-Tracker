package com.arlabs.uncloud.presentation.settings

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.arlabs.uncloud.BuildConfig
import com.arlabs.uncloud.domain.model.UserConfig
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToPrivacy: () -> Unit,
    onNavigateToTerms: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
        val userConfig by viewModel.userConfig.collectAsState()
        val notificationSettings by viewModel.notificationSettings.collectAsState()
        val isDarkTheme by viewModel.isDarkTheme.collectAsState()

        val context = LocalContext.current
        val scrollState = rememberScrollState()

        // Dialog States
        var showEditDetailsDialog by remember { mutableStateOf(false) }
        var showQuitDialog by remember { mutableStateOf(false) }
        var showResetDialog by remember { mutableStateOf(false) }
        var showNotificationTimeDialog by remember { mutableStateOf(false) }

        Scaffold(
                topBar = {
                        TopAppBar(
                                title = { Text("Settings", color = Color.White) },
                                navigationIcon = {
                                        IconButton(onClick = onNavigateBack) {
                                                Icon(
                                                        imageVector =
                                                                Icons.AutoMirrored.Filled.ArrowBack,
                                                        contentDescription = "Back",
                                                        tint = Color.White
                                                )
                                        }
                                },
                                colors =
                                        TopAppBarDefaults.topAppBarColors(
                                                containerColor = Color.Black
                                        )
                        )
                },
                containerColor = Color.Black
        ) { padding ->
                Column(
                        modifier =
                                Modifier.fillMaxSize()
                                        .padding(padding)
                                        .verticalScroll(scrollState)
                                        .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                        // 1. Personal Data
                        SettingsSection(title = "Personal Data") {
                                SettingsItem(
                                        icon = Icons.Default.Edit,
                                        title = "Edit Quit Date & Time",
                                        subtitle =
                                                userConfig?.let {
                                                        SimpleDateFormat(
                                                                        "dd MMM yyyy, hh:mm a",
                                                                        Locale.getDefault()
                                                                )
                                                                .format(it.quitTimestamp)
                                                }
                                                        ?: "Not Set",
                                        onClick = { showQuitDialog = true }
                                )
                                SettingsItem(
                                        icon = Icons.Default.Edit,
                                        title = "Cigarette Details",
                                        subtitle = "Update cost, per day, pack size",
                                        onClick = { showEditDetailsDialog = true }
                                )
                                // Currency logic can be part of Edit Details for simplicity or
                                // separate
                                SettingsItem(
                                        icon = Icons.Default.Delete,
                                        title = "Reset Progress",
                                        subtitle = "Clear all data and start over",
                                        textColor = Color(0xFFFF5252),
                                        iconColor = Color(0xFFFF5252),
                                        onClick = { showResetDialog = true }
                                )
                        }

                        // 2. App Preferences
                        SettingsSection(title = "App Preferences") {
                                SwitchSettingsItem(
                                        icon = Icons.Default.Notifications,
                                        title = "Daily Motivation",
                                        subtitle = "Receive daily motivational quotes",
                                        checked = notificationSettings.dailyMotivationEnabled,
                                        onCheckedChange = {
                                                viewModel.updateNotificationSettings(
                                                        it,
                                                        notificationSettings
                                                                .healthMilestonesEnabled,
                                                        notificationSettings.notificationHour,
                                                        notificationSettings.notificationMinute
                                                )
                                        }
                                )
                                SwitchSettingsItem(
                                        icon = Icons.Default.Info,
                                        title = "Health Milestones",
                                        subtitle = "Alert when milestones are reached",
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
                                        icon = Icons.Default.Notifications,
                                        title = "Notification Time",
                                        subtitle =
                                                String.format(
                                                        Locale.getDefault(),
                                                        "%02d:%02d",
                                                        notificationSettings.notificationHour,
                                                        notificationSettings.notificationMinute
                                                ),
                                        onClick = {
                                                showNotificationTimeDialog = true
                                        }
                                )
//                                SettingsItem(
//                                        icon = Icons.Default.Notifications,
//                                        title = "Test Notification",
//                                        subtitle = "Send a test notification now",
//                                        onClick = {
//                                                val helper =
//                                                        com.arlabs.quitsmoking.presentation.util
//                                                                .NotificationHelper(context)
//                                                helper.createNotificationChannel()
//                                                helper.showNotification(
//                                                        "Test Notification",
//                                                        "This is a test message to verify notifications work."
//                                                )
//                                        }
//                                )
                                // Theme Toggle (Simplified as switch)
//                                SwitchSettingsItem(
//                                        icon = Icons.Default.Settings,
//                                        title = "Dark Theme",
//                                        subtitle = "Use dark appearance",
//                                        checked = isDarkTheme,
//                                        onCheckedChange = { viewModel.toggleTheme(it) }
//                                )
                        }

                        // 3. Support & Feedback
                        SettingsSection(title = "Support") {
                                SettingsItem(
                                        icon = Icons.Default.Star,
                                        title = "Rate Us",
                                        subtitle = "Help us improve with a review",
                                        onClick = {
                                                try {
                                                        context.startActivity(
                                                                Intent(
                                                                        Intent.ACTION_VIEW,
                                                                        Uri.parse(
                                                                                "market://details?id=${context.packageName}"
                                                                        )
                                                                )
                                                        )
                                                } catch (e: Exception) {
                                                        Toast.makeText(
                                                                        context,
                                                                        "Could not open Play Store",
                                                                        Toast.LENGTH_SHORT
                                                                )
                                                                .show()
                                                }
                                        }
                                )
                                SettingsItem(
                                        icon = Icons.Default.Share,
                                        title = "Share App",
                                        subtitle = "Tell a friend about us",
                                        onClick = {
                                                val shareIntent =
                                                        Intent().apply {
                                                                action = Intent.ACTION_SEND
                                                                putExtra(
                                                                        Intent.EXTRA_TEXT,
                                                                        "Hey, I've been using this app called Uncloud to quit smoking and it's actually working. It tracks your biology and money saved. I think you'd like it.\n" +
                                                                                "\n" +
                                                                                "Download here: https://play.google.com/store/apps/details?id=${context.packageName}"
                                                                )
                                                                type = "text/plain"
                                                        }
                                                context.startActivity(
                                                        Intent.createChooser(
                                                                shareIntent,
                                                                "Share via"
                                                        )
                                                )
                                        }
                                )
                                SettingsItem(
                                        icon = Icons.Default.Warning,
                                        title = "Feedback & Bugs",
                                        subtitle = "report@arlabs.com",
                                        onClick = {
                                                val intent =
                                                        Intent(Intent.ACTION_SENDTO).apply {
                                                                data = Uri.parse("mailto:")
                                                                putExtra(
                                                                        Intent.EXTRA_EMAIL,
                                                                        arrayOf(
                                                                                "anuragrana050305@gmail.com"
                                                                        )
                                                                )
                                                                putExtra(
                                                                        Intent.EXTRA_SUBJECT,
                                                                        "Quit Smoking App Feedback"
                                                                )
                                                        }
                                                try {
                                                        context.startActivity(intent)
                                                } catch (e: Exception) {
                                                        Toast.makeText(
                                                                        context,
                                                                        "No email app found",
                                                                        Toast.LENGTH_SHORT
                                                                )
                                                                .show()
                                                }
                                        }
                                )
                        }

                        // 4. About & Legal
                        SettingsSection(title = "About") {
                                SettingsItem(
                                        icon = Icons.Default.Info,
                                        title = "Privacy Policy",
                                        subtitle = "Read our privacy policy",
                                        onClick = onNavigateToPrivacy
//                                        onClick = {
//                                                val intent =
//                                                        Intent(
//                                                                Intent.ACTION_VIEW,
//                                                                Uri.parse(
//                                                                        "https://policies.google.com/privacy"
//                                                                )
//                                                        )
//                                                context.startActivity(intent)
//                                        }
                                )
                                SettingsItem(
                                        icon = Icons.Default.Info,
                                        title = "Terms of Service",
                                        subtitle = "Read our terms",
                                        onClick = onNavigateToTerms
//                                        onClick = {
//                                                val intent =
//                                                        Intent(
//                                                                Intent.ACTION_VIEW,
//                                                                Uri.parse(
//                                                                        "https://policies.google.com/terms"
//                                                                )
//                                                        )
//                                                context.startActivity(intent)
//                                        }
                                )
                                SettingsItem(
                                        icon = Icons.Default.Info,
                                        title = "Version",
                                        subtitle = BuildConfig.VERSION_NAME,
                                        onClick = {}
                                )
                        }
                        Spacer(modifier = Modifier.height(32.dp))
                }
        }

        if (showQuitDialog) {
                CombinedDateTimeDialog(
                        initialMillis = userConfig?.quitTimestamp ?: System.currentTimeMillis(),
                        onDismiss = { showQuitDialog = false },
                        onConfirm = { timestamp ->
                                viewModel.updateQuitDate(timestamp)
                                showQuitDialog = false
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
                        title = { Text("Reset Progress?", color = Color.White) },
                        text = {
                                Text(
                                        "This will permanently delete all your data and progress. This action cannot be undone.",
                                        color = Color.Gray
                                )
                        },
                        confirmButton = {
                                TextButton(
                                        onClick = {
                                                viewModel.resetProgress()
                                                showResetDialog = false
                                                // Ideally navigate to Onboarding, but StateFlow in
                                                // MainActivity should handle this automatically
                                                // since userConfig becomes null.
                                        }
                                ) { Text("Reset", color = Color(0xFFFF5252)) }
                        },
                        dismissButton = {
                                TextButton(onClick = { showResetDialog = false }) {
                                        Text("Cancel", color = Color.White)
                                }
                        },
                        containerColor = Color(0xFF1E1E1E)
                )
        }
}

@Composable
fun SettingsSection(title: String, content: @Composable () -> Unit) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                        text = title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4CAF50),
                        modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
                )
                Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF161616)),
                        shape = RoundedCornerShape(16.dp)
                ) { Column { content() } }
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
                modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
                Icon(imageVector = icon, contentDescription = null, tint = iconColor)
                Column(modifier = Modifier.weight(1f)) {
                        Text(
                                text = title,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = textColor
                        )
                        Text(text = subtitle, fontSize = 12.sp, color = Color.Gray)
                }
                Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Color.DarkGray,
                        modifier = Modifier.size(16.dp)
                )
        }
        HorizontalDivider(
                color = Color(0xFF252525),
                thickness = 0.5.dp,
                modifier = Modifier.padding(horizontal = 16.dp)
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
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
                Icon(imageVector = icon, contentDescription = null, tint = Color.Gray)
                Column(modifier = Modifier.weight(1f)) {
                        Text(
                                text = title,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White
                        )
                        Text(text = subtitle, fontSize = 12.sp, color = Color.Gray)
                }
                Switch(
                        checked = checked,
                        onCheckedChange = onCheckedChange,
                        colors =
                                SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = Color(0xFF4CAF50)
                                )
                )
        }
        HorizontalDivider(
                color = Color(0xFF252525),
                thickness = 0.5.dp,
                modifier = Modifier.padding(horizontal = 16.dp)
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
                title = { Text("Update Details", color = Color.White) },
                text = {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                OutlinedTextField(
                                        value = costText,
                                        onValueChange = { costText = it },
                                        label = { Text("Cost per Pack") },
                                        singleLine = true,
                                        keyboardOptions =
                                                KeyboardOptions(
                                                        keyboardType = KeyboardType.Number,
                                                        imeAction =
                                                                androidx.compose.ui.text.input
                                                                        .ImeAction.Next
                                                ),
                                        colors =
                                                androidx.compose.material3.OutlinedTextFieldDefaults
                                                        .colors(
                                                                focusedTextColor = Color.White,
                                                                unfocusedTextColor = Color.White,
                                                                focusedLabelColor =
                                                                        Color(0xFF4CAF50),
                                                                unfocusedLabelColor = Color.Gray,
                                                                focusedBorderColor =
                                                                        Color(0xFF4CAF50),
                                                                cursorColor = Color(0xFF4CAF50)
                                                        )
                                )
                                OutlinedTextField(
                                        value = cigsPerDayText,
                                        onValueChange = { cigsPerDayText = it },
                                        label = { Text("Cigarettes per Day") },
                                        singleLine = true,
                                        keyboardOptions =
                                                KeyboardOptions(
                                                        keyboardType = KeyboardType.Number,
                                                        imeAction =
                                                                androidx.compose.ui.text.input
                                                                        .ImeAction.Next
                                                ),
                                        colors =
                                                androidx.compose.material3.OutlinedTextFieldDefaults
                                                        .colors(
                                                                focusedTextColor = Color.White,
                                                                unfocusedTextColor = Color.White,
                                                                focusedLabelColor =
                                                                        Color(0xFF4CAF50),
                                                                unfocusedLabelColor = Color.Gray,
                                                                focusedBorderColor =
                                                                        Color(0xFF4CAF50),
                                                                cursorColor = Color(0xFF4CAF50)
                                                        )
                                )
                                OutlinedTextField(
                                        value = cigsInPackText,
                                        onValueChange = { cigsInPackText = it },
                                        label = { Text("Cigarettes in Pack") },
                                        singleLine = true,
                                        keyboardOptions =
                                                KeyboardOptions(
                                                        keyboardType = KeyboardType.Number,
                                                        imeAction =
                                                                androidx.compose.ui.text.input
                                                                        .ImeAction.Done
                                                ),
                                        colors =
                                                androidx.compose.material3.OutlinedTextFieldDefaults
                                                        .colors(
                                                                focusedTextColor = Color.White,
                                                                unfocusedTextColor = Color.White,
                                                                focusedLabelColor =
                                                                        Color(0xFF4CAF50),
                                                                unfocusedLabelColor = Color.Gray,
                                                                focusedBorderColor =
                                                                        Color(0xFF4CAF50),
                                                                cursorColor = Color(0xFF4CAF50)
                                                        )
                                )

                                var showCurrencyDialog by remember { mutableStateOf(false) }

                                OutlinedTextField(
                                        value = currencyText,
                                        onValueChange = {}, // Read-only
                                        label = { Text("Currency") },
                                        readOnly = true,
                                        trailingIcon = {
                                                androidx.compose.material3.Icon(
                                                        imageVector =
                                                                androidx.compose.material.icons
                                                                        .Icons.Default
                                                                        .ArrowDropDown,
                                                        contentDescription = "Select Currency",
                                                        tint = Color.Gray,
                                                        modifier =
                                                                Modifier.clickable {
                                                                        showCurrencyDialog = true
                                                                }
                                                )
                                        },
                                        modifier = Modifier.clickable { showCurrencyDialog = true },
                                        enabled = false, // Disable typing, handled by click
                                        colors =
                                                androidx.compose.material3.OutlinedTextFieldDefaults
                                                        .colors(
                                                                disabledTextColor = Color.White,
                                                                disabledLabelColor = Color.Gray,
                                                                disabledBorderColor = Color.Gray,
                                                                disabledTrailingIconColor =
                                                                        Color.Gray
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

                                Row(
                                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                                        horizontalArrangement = Arrangement.End
                                ) {
                                        androidx.compose.material3.TextButton(
                                                onClick = { onDismiss() }
                                        ) { Text("Cancel", color = Color.Gray) }
                                        Spacer(modifier = Modifier.size(8.dp))
                                        androidx.compose.material3.Button(
                                                onClick = {
                                                        val cost =
                                                                costText.toDoubleOrNull()
                                                                        ?: currentConfig.costPerPack
                                                        val day =
                                                                cigsPerDayText.toIntOrNull()
                                                                        ?: currentConfig
                                                                                .cigarettesPerDay
                                                        val pack =
                                                                cigsInPackText.toIntOrNull()
                                                                        ?: currentConfig
                                                                                .cigarettesInPack
                                                        onConfirm(cost, day, pack, currencyText)
                                                },
                                                colors =
                                                        androidx.compose.material3.ButtonDefaults
                                                                .buttonColors(
                                                                        containerColor =
                                                                                Color(0xFF4CAF50)
                                                                )
                                        ) { Text("Save", color = Color.Black) }
                                }
                        }
                },
                confirmButton = {},
                dismissButton = {},
                containerColor = Color(0xFF1E1E1E)
        )
}
