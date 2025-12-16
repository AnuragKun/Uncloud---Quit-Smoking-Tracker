package com.arlabs.uncloud.presentation.widgets

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.color.ColorProvider
// Removed the invalid .border import
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row // Added
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth // Added
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontFamily
import androidx.glance.text.FontStyle
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import dagger.hilt.android.EntryPointAccessors

import androidx.glance.action.clickable
import androidx.glance.appwidget.action.actionStartActivity
import com.arlabs.uncloud.presentation.MainActivity
import android.content.Intent
import androidx.glance.LocalContext

class DailyClarityWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val entryPoint =
            EntryPointAccessors.fromApplication(context, WidgetEntryPoint::class.java)
        val quoteManager = entryPoint.quoteManager
        val quote = quoteManager.getDailyQuote()

        provideContent {
            // --- CYBERPUNK PALETTE ---
            val bgMatte = ColorProvider(day = Color(0xFF090B0F), night = Color(0xFF090B0F))
            val cyanNeon = ColorProvider(day = Color(0xFF00E5FF), night = Color(0xFF00E5FF))
            val textDim = ColorProvider(day = Color(0xFF546E7A), night = Color(0xFF546E7A))
            val textBright = ColorProvider(day = Color(0xFFE0E0E0), night = Color(0xFFE0E0E0)) // Bright for Quote
            val greenNeon = ColorProvider(day = Color(0xFF00E676), night = Color(0xFF00E676))

            val context = LocalContext.current
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP 
            }

            // 1. TERMINAL CONTAINER
            Box(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .background(cyanNeon) // Border Color
                    .cornerRadius(12.dp)
                    .padding(2.dp) // Border Thickness
                    .clickable(actionStartActivity(intent)), // Navigate on tap
                contentAlignment = Alignment.Center
            ) {
                // 2. INNER BOX
                Box(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .background(bgMatte)   // Main Background
                        .cornerRadius(10.dp)
                        .padding(12.dp),
                    contentAlignment = Alignment.TopStart
                ) {
                    // 3. CONTENT COLUMN
                    Column(
                        modifier = GlanceModifier.fillMaxSize()
                    ) {
                        // Header
                        Row(
                             modifier = GlanceModifier.fillMaxWidth(),
                             verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "COMMAND :: NEURAL_FEED",
                                style = TextStyle(
                                    color = cyanNeon,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Spacer(modifier = GlanceModifier.defaultWeight())
                             Box(
                                modifier = GlanceModifier
                                    .width(6.dp)
                                    .height(6.dp)
                                    .background(greenNeon)
                                    .cornerRadius(3.dp)
                            ) {}
                        }

                        Spacer(modifier = GlanceModifier.defaultWeight())

                        // The Quote
                        Text(
                            text = "\"${quote.text}\"",
                            style = TextStyle(
                                color = textBright,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium, // Less serif, more tech/clean
                                textAlign = TextAlign.Start
                            )
                        )

                        Spacer(modifier = GlanceModifier.height(8.dp))

                        // Author / Source
                        Text(
                            text = "SOURCE: ${quote.author.uppercase()}",
                            style = TextStyle(
                                color = textDim,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.End
                            ),
                            modifier = GlanceModifier.fillMaxWidth()
                        )
                        
                        Spacer(modifier = GlanceModifier.defaultWeight())
                    }
                }
            }
        }
    }
}

class DailyClarityWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = DailyClarityWidget()
}