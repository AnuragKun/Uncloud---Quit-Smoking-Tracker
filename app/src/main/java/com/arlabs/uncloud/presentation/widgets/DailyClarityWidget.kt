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
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
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
            // Theme Colors (Safe Providers)
            val bgDark = ColorProvider(day = Color(0xFF0F1216), night = Color(0xFF0F1216))
            val borderGray = ColorProvider(day = Color(0xFF252A30), night = Color(0xFF252A30))
            val textWhite = ColorProvider(day = Color(0xFFE0E0E0), night = Color(0xFFE0E0E0))
            val textGray = ColorProvider(day = Color(0xFF8B9BB4), night = Color(0xFF8B9BB4))
            val accentCyan = ColorProvider(day = Color(0xFF00E5FF), night = Color(0xFF00E5FF))

            val context = LocalContext.current
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP 
            }

            // 1. OUTER BOX (Acts as the Border)
            Box(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .background(borderGray) // Border Color
                    .cornerRadius(16.dp)    // Outer Radius
                    .padding(1.dp)          // Border Thickness (1dp)
                    .clickable(actionStartActivity(intent)), // Navigate on tap
                contentAlignment = Alignment.Center
            ) {
                // 2. INNER BOX (Acts as the Content Background)
                Box(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .background(bgDark)   // Main Background
                        .cornerRadius(15.dp), // Inner Radius (slightly less to look smooth)
                    contentAlignment = Alignment.Center
                ) {
                    // 3. CONTENT COLUMN
                    Column(
                        modifier = GlanceModifier
                            .padding(16.dp), // Actual padding for text
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        // 1. Elegant Header
                        Text(
                            text = "DAILY CLARITY",
                            style = TextStyle(
                                color = textGray,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        )

                        Spacer(modifier = GlanceModifier.height(8.dp))

                        // 2. The Uncloud Accent Line (Visual Separation)
                        Box(
                            modifier = GlanceModifier
                                .height(2.dp)
                                .width(24.dp)
                                .background(accentCyan)
                                .cornerRadius(2.dp)
                        ) {}

                        Spacer(modifier = GlanceModifier.height(12.dp))

                        // 3. The Quote (Serif for "Book" feel)
                        Text(
                            text = "\"${quote.text}\"",
                            style = TextStyle(
                                color = textWhite,
                                fontSize = 15.sp,
                                fontFamily = FontFamily.Serif,
                                fontStyle = FontStyle.Italic,
                                textAlign = TextAlign.Center
                            )
                        )

                        Spacer(modifier = GlanceModifier.height(12.dp))

                        // 4. Author
                        Text(
                            text = "— ${quote.author}",
                            style = TextStyle(
                                color = textGray,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Serif,
                                textAlign = TextAlign.Center
                            )
                        )
                    }
                }
            }
        }
    }
}

class DailyClarityWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = DailyClarityWidget()
}