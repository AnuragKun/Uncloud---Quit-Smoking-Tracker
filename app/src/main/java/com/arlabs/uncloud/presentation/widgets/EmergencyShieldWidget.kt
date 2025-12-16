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
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
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
            // --- CYBERPUNK PALETTE ---
            val bgMatte = ColorProvider(day = Color(0xFF090B0F), night = Color(0xFF090B0F))
            val cyanNeon = ColorProvider(day = Color(0xFF00E5FF), night = Color(0xFF00E5FF))
            val redAlert = ColorProvider(day = Color(0xFFFF2B2B), night = Color(0xFFFF2B2B))
            val white = ColorProvider(day = Color.White, night = Color.White)
            val textDim = ColorProvider(day = Color(0xFF546E7A), night = Color(0xFF546E7A))

            // Button Colors
            val bezelColor = ColorProvider(day = Color(0xFF1E2429), night = Color(0xFF1E2429))

            // 1. TERMINAL CONTAINER
            Box(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .background(cyanNeon) // Border
                    .cornerRadius(12.dp)
                    .padding(2.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .background(bgMatte)
                        .cornerRadius(10.dp)
                        .padding(12.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Column(
                        // FIX: Added fillMaxSize() so the spacers inside have room to expand
                        modifier = GlanceModifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // 2. HEADER
                        Row(
                            modifier = GlanceModifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "COMMAND :: PANIC_PROTOCOL",
                                style = TextStyle(
                                    color = cyanNeon,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )

                            // Horizontal Spacer for the Row
                            Spacer(modifier = GlanceModifier.defaultWeight())

                            // Blinking Red Dot
                            Box(
                                modifier = GlanceModifier
                                    .width(6.dp)
                                    .height(6.dp)
                                    .background(redAlert)
                                    .cornerRadius(3.dp)
                            ) {}
                        }

                        // Spacer 1: Pushes button down from header
                        Spacer(modifier = GlanceModifier.defaultWeight())

                        // 3. THE BUTTON ASSEMBLY
                        Box(
                            modifier = GlanceModifier
                                .size(80.dp)
                                .background(bezelColor)
                                .cornerRadius(40.dp)
                                .clickable(actionStartActivity(intent)),
                            contentAlignment = Alignment.Center
                        ) {
                            // Layer 2: The Red Core
                            Box(
                                modifier = GlanceModifier
                                    .size(60.dp)
                                    .background(redAlert)
                                    .cornerRadius(30.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                // Layer 3: The Icon/Text
                                Text(
                                    text = "SOS",
                                    style = TextStyle(
                                        color = white,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center
                                    )
                                )
                            }
                        }

                        // Spacer 2: Pushes footer down (Balancing the button in the middle)
                        Spacer(modifier = GlanceModifier.defaultWeight())

                        Text(
                            text = "STATUS: STANDBY",
                            style = TextStyle(
                                color = textDim,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }
    }
}

class EmergencyShieldWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = EmergencyShieldWidget()
}