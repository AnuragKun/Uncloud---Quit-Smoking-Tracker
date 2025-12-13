package com.arlabs.uncloud.presentation.widgets

import android.content.Context
import android.content.Intent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.color.ColorProvider
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import com.arlabs.uncloud.presentation.MainActivity

class EmergencyShieldWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val intent = Intent(context, MainActivity::class.java).apply {
            action = Intent.ACTION_VIEW
            data = android.net.Uri.parse("uncloud://panic")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        provideContent {
            // Theme Colors
            val bgDark = ColorProvider(day = Color(0xFF0F1216), night = Color(0xFF0F1216))
            val borderGray = ColorProvider(day = Color(0xFF252A30), night = Color(0xFF252A30))
            val headerText = ColorProvider(day = Color(0xFF8B9BB4), night = Color(0xFF8B9BB4))

            // Button Specific Colors
            val bezelColor = ColorProvider(day = Color(0xFF1E2429), night = Color(0xFF1E2429))
            val gapColor = ColorProvider(day = Color(0xFF0A0C0E), night = Color(0xFF0A0C0E)) // Very dark for depth
            val alertRed = ColorProvider(day = Color(0xFFD32F2F), night = Color(0xFFD32F2F))
            val white = ColorProvider(day = Color.White, night = Color.White)

            // 1. MAIN CARD (The Housing)
            Box(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .background(borderGray)
                    .cornerRadius(16.dp)
                    .padding(1.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .background(bgDark)
                        .cornerRadius(15.dp)
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // 2. HEADER (System Alert Style)
                        Text(
                            text = "E M E R G E N C Y", // Simulated tracking
                            style = TextStyle(
                                color = headerText,
                                fontSize = 9.sp, // Smaller, tighter
                                fontWeight = FontWeight.Bold
                            )
                        )

                        Spacer(modifier = GlanceModifier.height(12.dp))

                        // 3. THE BUTTON ASSEMBLY
                        // Layer 1: The Outer Bezel (The mount)
                        Box(
                            modifier = GlanceModifier
                                .size(84.dp)
                                .background(bezelColor)
                                .cornerRadius(42.dp)
                                .clickable(actionStartActivity(intent)),
                            contentAlignment = Alignment.Center
                        ) {
                            // Layer 2: The "Gap" (Creates 3D depth/shadow effect)
                            Box(
                                modifier = GlanceModifier
                                    .size(82.dp) // 1dp gap all around
                                    .background(gapColor)
                                    .cornerRadius(41.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                // Layer 3: The Actual Button (Red)
                                Box(
                                    modifier = GlanceModifier
                                        .size(68.dp)
                                        .background(alertRed)
                                        .cornerRadius(34.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    // Layer 4: The Icon/Text
                                    Text(
                                        text = "SOS",
                                        style = TextStyle(
                                            color = white,
                                            fontSize = 22.sp,
                                            fontWeight = FontWeight.Bold,
                                            textAlign = TextAlign.Center
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

class EmergencyShieldWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = EmergencyShieldWidget()
}