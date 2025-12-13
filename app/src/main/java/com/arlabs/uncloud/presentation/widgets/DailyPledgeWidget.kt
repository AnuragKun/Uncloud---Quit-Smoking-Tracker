package com.arlabs.uncloud.presentation.widgets

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.ActionParameters
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.color.ColorProvider
import androidx.glance.text.TextAlign
import com.arlabs.uncloud.domain.repository.UserRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import kotlinx.coroutines.flow.first

import androidx.glance.appwidget.action.actionStartActivity
import com.arlabs.uncloud.presentation.MainActivity
import android.content.Intent
import androidx.glance.LocalContext

class DailyPledgeWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val entryPoint =
            EntryPointAccessors.fromApplication(context, WidgetEntryPoint::class.java)
        val repo = entryPoint.userRepository

        // 1. Fetch Data (Pledge State + User Config for Streak)
        val lastPledgeDate = repo.pledgeState.first()
        val userConfig = repo.userConfig.first()
        
        val today = LocalDate.now().toString()
        val isPledged = (lastPledgeDate == today)

        // Calculate Streak
        val quitTimestamp = userConfig?.quitTimestamp ?: 0L
        val streakDayCount = if (quitTimestamp > 0L) {
             val quitDate = java.time.Instant.ofEpochMilli(quitTimestamp)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
            val todayDate = LocalDate.now()
            val diff = ChronoUnit.DAYS.between(quitDate, todayDate)
            if (diff < 0) 0L else diff
        } else {
            0L
        }

        provideContent {
            DailyPledgeContent(isPledged, streakDayCount)
        }
    }

    @Composable
    private fun DailyPledgeContent(isPledged: Boolean, streakDayCount: Long) {
        // --- COLORS ---
        val bgDark = ColorProvider(day = Color(0xFF0F1216), night = Color(0xFF0F1216))
        val borderGray = ColorProvider(day = Color(0xFF252A30), night = Color(0xFF252A30))
        val textGray = ColorProvider(day = Color(0xFF8B9BB4), night = Color(0xFF8B9BB4))
        
        // Brand Colors
        val cyanColor = ColorProvider(day = Color(0xFF00E5FF), night = Color(0xFF00E5FF))
        val greenColor = ColorProvider(day = Color(0xFF2E8B57), night = Color(0xFF2E8B57))
        val whiteColor = ColorProvider(day = Color.White, night = Color.White)
        val blackColor = ColorProvider(day = Color.Black, night = Color.Black)
        val alertRed = ColorProvider(day = Color(0xFFEF5350), night = Color(0xFFEF5350))

        // Border Logic
        val borderColor = if (isPledged) greenColor else borderGray

        val context = LocalContext.current
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        // 1. CONTAINER
        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(borderColor)
                .cornerRadius(16.dp)
                .padding(1.dp)
                .clickable(actionStartActivity(intent)), // Navigate on tap
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .background(bgDark)
                    .cornerRadius(15.dp)
                    .padding(16.dp), // Increased Padding for internal breathing room
                contentAlignment = Alignment.TopCenter
            ) {
                 Column(
                    modifier = GlanceModifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                 ) {
                     // 2. HEADER
                     Text(
                         text = "DAILY PLEDGE",
                         style = TextStyle(
                             color = textGray,
                             fontSize = 10.sp,
                             fontWeight = FontWeight.Bold
                         )
                     )

                     Spacer(modifier = GlanceModifier.defaultWeight())

                     // 3. MAIN CONTENT (Interactive Zone)
                     if (isPledged) {
                         // --- PLEDGED STATE ---
                         Column(horizontalAlignment = Alignment.CenterHorizontally) {
                             Box(
                                 modifier = GlanceModifier
                                     .background(greenColor)
                                     .cornerRadius(24.dp)
                                     .padding(horizontal = 16.dp, vertical = 8.dp)
                             ) {
                                 Text(
                                     text = "✓ DAY SECURED",
                                     style = TextStyle(
                                         color = whiteColor,
                                         fontWeight = FontWeight.Bold,
                                         fontSize = 14.sp,
                                         textAlign = TextAlign.Center
                                     )
                                 )
                             }
                             Spacer(modifier = GlanceModifier.height(8.dp))
                             Text(
                                 text = "Freedom Chosen.",
                                 style = TextStyle(color = textGray, fontSize = 11.sp)
                             )
                         }
                     } else {
                         // --- UNPLEDGED STATE ---
                         Column(horizontalAlignment = Alignment.CenterHorizontally) {
                             Text(
                                 text = "Make the choice.",
                                 style = TextStyle(
                                     color = textGray,
                                     fontSize = 12.sp
                                 ),
                                 modifier = GlanceModifier.padding(bottom = 12.dp)
                             )
                             
                             // Big Action Button
                             Box(
                                 modifier = GlanceModifier
                                     .fillMaxWidth()
                                     .height(44.dp) // Taller button
                                     .background(cyanColor)
                                     .cornerRadius(12.dp)
                                     .clickable(actionRunCallback<PledgeAction>()),
                                 contentAlignment = Alignment.Center
                             ) {
                                  Text(
                                     text = "I COMMIT",
                                     style = TextStyle(
                                         color = blackColor,
                                         fontWeight = FontWeight.Bold,
                                         fontSize = 14.sp
                                     )
                                 )
                             }
                         }
                     }

                     Spacer(modifier = GlanceModifier.defaultWeight())

                     // 4. FOOTER (Streak Info)
                     // A nice divider or subtle container for the streak
                     Column(horizontalAlignment = Alignment.CenterHorizontally) {
                         // Divider Line
                         Box(
                             modifier = GlanceModifier
                                 .width(40.dp) // Fixed width for center line
                                 .height(1.dp)
                                 .background(borderGray)
                         ) {}
                         
                         Spacer(modifier = GlanceModifier.height(8.dp))
                         
                         val streakColor = if (isPledged) greenColor else whiteColor
                         Text(
                             text = "🎗️ $streakDayCount Days Unclouded",
                             style = TextStyle(
                                 color = streakColor,
                                 fontSize = 12.sp,
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

class DailyPledgeWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = DailyPledgeWidget()
}

class PledgeAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val entryPoint =
            EntryPointAccessors.fromApplication(context, WidgetEntryPoint::class.java)
        val repo = entryPoint.userRepository

        // Save State
        val today = LocalDate.now().toString()
        repo.savePledgeState(today)

        // Force update
        DailyPledgeWidget().update(context, glanceId)
    }
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface WidgetEntryPoint {
    val userRepository: UserRepository
    val quoteManager: com.arlabs.uncloud.domain.manager.QuoteManager
}