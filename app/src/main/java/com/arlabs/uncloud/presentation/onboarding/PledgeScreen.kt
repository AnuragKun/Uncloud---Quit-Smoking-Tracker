package com.arlabs.uncloud.presentation.onboarding

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arlabs.uncloud.R

@Composable
fun PledgeScreen(onPledgeConfirmed: () -> Unit) {
    // Pulse Animation for the logo "Reactor"
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            // Background is handled by the parent Scaffold, but we add padding
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(0.5f))

            // 1. Hero Icon with Animated Glow
            Box(contentAlignment = Alignment.Center) {
                // The glowing aura
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .scale(pulseScale)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), // Cyan Glow
                                    Color.Transparent
                                )
                            ),
                            shape = CircleShape
                        )
                )
                // The app logo
                Icon(
                    painter = painterResource(id = R.drawable.icon_app_logo),
                    contentDescription = "System Core",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(20.dp)),
                    tint = Color.Unspecified
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            // 2. Typographic Header (FIXED FONT SIZE)
            Text(
                text = "SYSTEM\nINITIALIZATION",
                style = MaterialTheme.typography.headlineMedium.copy( // Changed from displaySmall
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = Color.White,
                    letterSpacing = 1.sp // Reduced spacing slightly
                ),
                maxLines = 2,
                overflow = TextOverflow.Visible,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth() // Ensure it uses full width
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 3. The Pledge Card (Styled as a terminal readout)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f), RoundedCornerShape(16.dp))
                    .padding(24.dp)
            ) {
                Column {
                    Text(
                        text = "// PROTOCOL_AGREEMENT",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.primary,
                            fontFamily = FontFamily.Monospace
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "I am ready to reclaim my freedom.\n\nI choose health over habit, and my future over the past.\n\nToday, I take control.",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 28.sp,
                            fontFamily = FontFamily.Monospace
                        ),
                        textAlign = TextAlign.Start
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // 4. Action Button
            Button(
                onClick = onPledgeConfirmed,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(4.dp), // Tech-looking sharp corners
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary // Cyber Cyan
                )
            ) {
                Text(
                    text = "INITIATE SEQUENCE",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = Color.Black,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}