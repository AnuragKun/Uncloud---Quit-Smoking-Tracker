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
fun TermsOfServiceScreen(onNavigateBack: () -> Unit) {
    val currentDate = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(Date())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Terms of Service", color = Color.White) },
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
                    text = "Terms of Service for Uncloud",
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
                    text = "Please read these Terms of Service (\"Terms\") carefully before using the Uncloud mobile application (the \"Service\") operated by AR Labs (\"us\", \"we\", or \"our\").\n\n" +
                            "By accessing or using the Service, you agree to be bound by these Terms. If you disagree with any part of the terms, you may not use the Service.\n\n" +
                            "1. Medical Disclaimer (Crucial)\n" +
                            "Uncloud is not a medical device and does not provide medical advice. The content, health milestones, and recovery data found within the app are for informational, educational, and motivational purposes only. They are based on general data from public health organizations (such as the WHO) but do not account for individual medical history.\n\n" +
                            "Not a Substitute: This app is not a substitute for professional medical advice, diagnosis, or treatment.\n\n" +
                            "Consult Professionals: Always seek the advice of your physician or other qualified health provider with any questions you may have regarding a medical condition or addiction withdrawal.\n\n" +
                            "Emergency: If you think you may have a medical emergency, call your doctor or emergency services immediately.\n\n" +
                            "No Liability: We are not responsible for any health complications or relapses that may occur while using this app.\n\n" +
                            "2. User Data & Privacy\n" +
                            "Uncloud is designed to be privacy-first. Your personal data (quit date, habits, costs) is stored locally on your device.\n\n" +
                            "Data Loss: We are not responsible for the loss of your streak or data due to device failure, factory resets, or uninstalling the app, as we do not maintain a cloud backup of your personal habits.\n\n" +
                            "Accuracy: You are responsible for the accuracy of the data you enter (e.g., cigarette cost), which drives the app's calculations.\n\n" +
                            "3. Intellectual Property\n" +
                            "The Service and its original content (including the \"Uncloud\" name, logo, 50-milestone roadmap, and design) are the exclusive property of AR Labs. You may not reproduce, distribute, or create derivative works from this content without our express written permission.\n\n" +
                            "4. Limitation of Liability\n" +
                            "In no event shall AR Labs be liable for any indirect, incidental, special, or consequential damages arising out of or in connection with your use of the Service. This includes, but is not limited to, damages for loss of profits, data, or other intangible losses.\n\n" +
                            "5. Third-Party Links\n" +
                            "Our Service may contain links to third-party websites (e.g., support resources) that are not owned or controlled by us. We have no control over, and assume no responsibility for, the content, privacy policies, or practices of any third-party websites.\n\n" +
                            "6. Termination\n" +
                            "We may terminate or suspend access to our Service immediately, without prior notice or liability, for any reason whatsoever, including without limitation if you breach the Terms.\n\n" +
                            "7. Changes to These Terms\n" +
                            "We reserve the right, at our sole discretion, to modify or replace these Terms at any time. By continuing to access or use our Service after those revisions become effective, you agree to be bound by the revised terms.\n\n" +
                            "8. Contact Us\n" +
                            "If you have any questions about these Terms, please contact us at: [ anuragrana050305@gmail.com ]",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White,
                    lineHeight = 24.sp
                )
            }
        }
    }
}
