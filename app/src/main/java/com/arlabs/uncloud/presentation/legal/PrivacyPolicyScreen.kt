package com.arlabs.uncloud.presentation.legal

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyScreen(onNavigateBack: () -> Unit) {
    val currentDate = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(Date())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Privacy Policy", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
            )
        },
        containerColor = Color.Black
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                Text(
                    text = "Privacy Policy for Uncloud",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Last Updated: 12 December 2025",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                Text(
                    text = "Uncloud (\"we,\" \"our,\" or \"us\") is committed to protecting your privacy. This Privacy Policy explains how your personal information is collected, used, and disclosed by Uncloud.\n\n" +
                            "1. The Data We Collect\n" +
                            "We believe your journey to quitting smoking is personal. Therefore, Uncloud is designed to be privacy-first.\n\n" +
                            "Personal Data: Information you enter into the app (such as your quit date, cigarettes smoked per day, packet cost, and currency preference) is stored locally on your device. We do not transmit this data to external servers, and we cannot access it.\n\n" +
                            "Usage Data: The app may collect anonymous, non-identifiable data regarding app performance (e.g., crash logs) through standard Google Play Services to help us fix bugs and improve stability.\n\n" +
                            "2. Permissions We Request\n" +
                            "To provide the core features of the app, we request the following specific permission:\n\n" +
                            "Notifications (POST_NOTIFICATIONS): We use this permission solely to send you:\n" +
                            "Daily motivational quotes.\n" +
                            "Health milestone achievements (e.g., \"Nicotine Free\").\n" +
                            "Recovery progress alerts.\n\n" +
                            "You can revoke this permission at any time in your device settings. If you do, you will stop receiving alerts, but the app will continue to function.\n\n" +
                            "3. Third-Party Services\n" +
                            "Uncloud does not sell, trade, or rent your personal identification information to others.\n\n" +
                            "We may use third-party services for standard app functionality:\n\n" +
                            "Google Play Services: Used for app distribution and core Android functionality.\n\n" +
                            "4. Data Security\n" +
                            "Since your data is stored locally on your device, the security of your data relies on the security of your device. We recommend keeping your device updated and secured with a password or biometric lock to prevent unauthorized access to your health stats.\n\n" +
                            "5. Children’s Privacy\n" +
                            "Uncloud is not intended for use by children under the age of 13. We do not knowingly collect personal information from children under 13.\n\n" +
                            "6. Changes to This Privacy Policy\n" +
                            "We may update our Privacy Policy from time to time. We will notify you of any changes by posting the new Privacy Policy on this page and updating the \"Last Updated\" date.\n\n" +
                            "7. Contact Us\n" +
                            "If you have any questions or suggestions about our Privacy Policy, do not hesitate to contact us at:\n\n" +
                            "[ anuragrana050305@gmail.com ]",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White,
                    lineHeight = 24.sp
                )
            }
        }
    }
}
