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
            DailyPledgeContent(isPledged, streakDayCount, userConfig)
        }
    }

    @Composable
    private fun DailyPledgeContent(isPledged: Boolean, streakDayCount: Long, userConfig: com.arlabs.uncloud.domain.model.UserConfig?) {
        // --- CYBERPUNK PALETTE ---
        val bgMatte = ColorProvider(day = Color(0xFF090B0F), night = Color(0xFF090B0F))
        val cyanNeon = ColorProvider(day = Color(0xFF00E5FF), night = Color(0xFF00E5FF))
        val greenNeon = ColorProvider(day = Color(0xFF00E676), night = Color(0xFF00E676)) // Bright Green
        val redAlert = ColorProvider(day = Color(0xFFFF2B2B), night = Color(0xFFFF2B2B))
        val textDim = ColorProvider(day = Color(0xFF546E7A), night = Color(0xFF546E7A))
        val textBright = ColorProvider(day = Color(0xFFE0E0E0), night = Color(0xFFE0E0E0))
        val blackColor = ColorProvider(day = Color.Black, night = Color.Black)

        // State Colors
        val statusColor = if (isPledged) greenNeon else redAlert
        val borderColor = cyanNeon // Always Cyan border for the "Terminal" look

        val context = LocalContext.current
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        // 1. TERMINAL CONTAINER
        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(borderColor)
                .padding(2.dp) // Thin Cyan Border
                .cornerRadius(12.dp)
                .clickable(actionStartActivity(intent)),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .background(bgMatte)
                    .cornerRadius(10.dp) // Slightly smaller radius to fit inside
                    .padding(12.dp),
                contentAlignment = Alignment.TopStart
            ) {
                Column(
                    modifier = GlanceModifier.fillMaxSize()
                ) {
                    // 2. HEADER: TERMINAL ID
                    Row(
                        modifier = GlanceModifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "COMMAND :: DAILY_PROTOCOL",
                            style = TextStyle(
                                color = cyanNeon, // Cyan Header
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(modifier = GlanceModifier.defaultWeight())
                        // Blinking Dot or Status Indicator
                         Box(
                            modifier = GlanceModifier
                                .width(6.dp)
                                .height(6.dp)
                                .background(statusColor)
                                .cornerRadius(3.dp)
                        ) {}
                    }

                    Spacer(modifier = GlanceModifier.defaultWeight())

                    // 3. CENTRAL HUD
                    Column(
                        modifier = GlanceModifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (isPledged) {
                            // --- SECURED STATE ---
                            Text(
                                text = "[ SECURED ]",
                                style = TextStyle(
                                    color = greenNeon,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                            )
                            Spacer(modifier = GlanceModifier.height(4.dp))
                            Text(
                                text = "PROTOCOL SUCCESSFUL",
                                style = TextStyle(
                                    color = textDim,
                                    fontSize = 10.sp,
                                    textAlign = TextAlign.Center
                                )
                            )
                        } else {
                            // --- ALERT STATE ---
                            Text(
                                text = "⚠ ATTENTION REQUIRED",
                                style = TextStyle(
                                    color = redAlert,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Spacer(modifier = GlanceModifier.height(8.dp))
                            
                            // INITIATE BUTTON
                            Box(
                                modifier = GlanceModifier
                                    .fillMaxWidth()
                                    .height(40.dp)
                                    .background(cyanNeon)
                                    .cornerRadius(4.dp) // Sharp corners for tech feel
                                    .clickable(actionRunCallback<PledgeAction>()),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = ">> INITIATE PLEDGE",
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

                    // 4. FOOTER: DATA GRID
                    // Simulating a grid with Columns
                    Row(
                        modifier = GlanceModifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // DATA 1: UPTIME
                        Column(modifier = GlanceModifier.defaultWeight()) {
                            Text(
                                text = "UPTIME",
                                style = TextStyle(color = textDim, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            )
                            val dayLabel = if (streakDayCount == 1L) "DAY" else "DAYS"
                            Text(
                                text = "$streakDayCount $dayLabel", // e.g. "124 DAYS"
                                style = TextStyle(color = textBright, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            )
                        }

                        // Vertical Separator
                        Box(
                            modifier = GlanceModifier
                                .width(1.dp)
                                .height(20.dp)
                                .background(textDim)
                        ) {}
                        
                        Spacer(modifier = GlanceModifier.width(12.dp))

                        // DATA 2: RESOURCES
                        Column(modifier = GlanceModifier.defaultWeight()) {
                            Text(
                                text = "RESOURCES",
                                style = TextStyle(color = textDim, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            )
                            
                            val currency = userConfig?.currency ?: "$"
                            val cigsPerDay = userConfig?.cigarettesPerDay ?: 20
                            val costPerPack = userConfig?.costPerPack ?: 10.0
                            val cigsInPack = userConfig?.cigarettesInPack ?: 20
                            
                            // Calculate
                            val cigsSaved = (streakDayCount * cigsPerDay).toLong()
                            val moneySaved = if (cigsInPack > 0) (cigsSaved * (costPerPack / cigsInPack)).toInt() else 0

                            Text(
                                text = "$currency$moneySaved \nSAVED",
                                style = TextStyle(color = textBright, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            )
                        }
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
        val quoteManager = entryPoint.quoteManager

        // Save State
        val today = LocalDate.now().toString()
        repo.savePledgeState(today)

        // Force update
        DailyPledgeWidget().update(context, glanceId)

        // Show Motivation Notification
        val userConfig = repo.userConfig.first()
        if (userConfig != null) {
            val quitDate = java.time.Instant.ofEpochMilli(userConfig.quitTimestamp)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
            val todayDate = LocalDate.now()
            val streakDays = ChronoUnit.DAYS.between(quitDate, todayDate)

            val quote = quoteManager.getDailyQuote()

            val title = if (streakDays > 0) "Day $streakDays Smoke Free! \uD83D\uDD25" else "Pledge Secured! \uD83D\uDD25"
            val content = "\"${quote.text}\" - ${quote.author}"

            val notificationHelper = com.arlabs.uncloud.presentation.util.NotificationHelper(context)
            notificationHelper.createNotificationChannel()
            notificationHelper.showNotification(title, content)
        }
    }
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface WidgetEntryPoint {
    val userRepository: UserRepository
    val quoteManager: com.arlabs.uncloud.domain.manager.QuoteManager
}