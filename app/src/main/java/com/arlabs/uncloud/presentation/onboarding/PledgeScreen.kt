package com.arlabs.uncloud.presentation.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arlabs.uncloud.R

@Composable
fun PledgeScreen(onPledgeConfirmed: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF0F1216)).padding(24.dp)) {
        Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))

            // Hero Icon with Radial Glow
            Box(contentAlignment = Alignment.Center) {
                Box(
                        modifier =
                                Modifier.size(120.dp)
                                        .background(
                                                brush =
                                                        Brush.radialGradient(
                                                                colors =
                                                                        listOf(
                                                                                Color(0xFF00E5FF)
                                                                                        .copy(
                                                                                                alpha =
                                                                                                        0.2f
                                                                                        ),
                                                                                Color.Transparent
                                                                        )
                                                        ),
                                                shape = CircleShape
                                        )
                )
                Icon(
                    painter = painterResource(id = R.drawable.icon_app_logo),
                    contentDescription = "App Logo",
                    modifier = Modifier.size(64.dp).clip(RoundedCornerShape(16.dp)),
                    tint = Color.Unspecified // <--- Renders original image colors
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                    text = "Time to Uncloud",
                    style =
                            MaterialTheme.typography.displayMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                            ),
                    textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                    text =
                            "I am ready to reclaim my freedom.\n\nI am choosing health over habit, and my future over the past.\n\nToday, I am taking control.",
                    style =
                            MaterialTheme.typography.bodyLarge.copy(
                                    color = Color.LightGray,
                                    lineHeight = 28.sp
                            ),
                    textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                    onClick = onPledgeConfirmed,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(50), // Pill-shaped
                    colors =
                            ButtonDefaults.buttonColors(
                                    containerColor =
                                            Color(
                                                0xFF2E8B57
                                            ) // Green from Home Screen cards is actually
                                    // 0xFF209A24, trying requested approx 0xFF2E7D32
                                    // User asked for "approx 0xFF2E7D32", but existing is
                                    // 0xFF2E8B57 (SeaGreen) in onboarding.
                                    // Let's stick to the user request or match the existing "Next"
                                    // button color 0xFF2E8B57 for consistency?
                                    // User said "match my other 'Next' buttons".
                                    // checking OnboardingScreen.kt line 133: containerColor =
                                    // Color(0xFF2E8B57)
                                    // I will use 0xFF2E8B57 to match.
                                    )
            ) {
                Text(
                        text = "I PLEDGE TO QUIT",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
