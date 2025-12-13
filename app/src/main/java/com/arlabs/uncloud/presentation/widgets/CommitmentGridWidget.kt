package com.arlabs.uncloud.presentation.widgets

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.Composable
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
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.RowScope
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.arlabs.uncloud.domain.model.rankSystem
import dagger.hilt.android.EntryPointAccessors
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.TextStyle as JavaTextStyle
import java.util.Locale
import java.time.temporal.ChronoUnit
import kotlinx.coroutines.flow.first

import androidx.glance.action.clickable
import androidx.glance.appwidget.action.actionStartActivity
import com.arlabs.uncloud.presentation.MainActivity
import android.content.Intent
import androidx.glance.LocalContext

class CommitmentGridWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val entryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            WidgetEntryPoint::class.java
        )
        val repo = entryPoint.userRepository
        val userConfig = repo.userConfig.first()
        // Fetch breaches for history check
        val breaches = repo.breaches.first() 
        val quitTimestamp = userConfig?.quitTimestamp ?: 0L

        val daysSinceQuit = if (quitTimestamp > 0L) {
            val quitDate = java.time.Instant.ofEpochMilli(quitTimestamp)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
            val today = LocalDate.now()
            ChronoUnit.DAYS.between(quitDate, today)
        } else {
            -1L
        }

        // Calculate Money Saved (Lifetime + Current)
        // Note: The UI currently shows "TOTAL Saved", which logic says should be lifetime + current streak.
        // If we want to show JUST current streak money, we stick to logic.
        // User requirements say "Lifetime Stats: Keep showing Money Saved". So we should sum them up?
        // Or "Money Saved ... since they started using the app".
        // Let's use lifetimeMoney + currentStreakMoney for the widget display if available.
        
        val currentStreakMoney = if (daysSinceQuit > 0 && userConfig != null) {
             val costPerCig = if (userConfig.cigarettesInPack > 0) userConfig.costPerPack / userConfig.cigarettesInPack else 0.0
             val dailyCost = userConfig.cigarettesPerDay * costPerCig
             daysSinceQuit * dailyCost
        } else 0.0
        
        val totalMoneySaved = (userConfig?.lifetimeMoney ?: 0.0) + currentStreakMoney

        // Calculate Rank
        val totalDaysSaved = (userConfig?.lifetimeCigarettes ?: 0) // Rank is based on clean time usually...
        // Wait, rank description says "Rank (The Identity): DOWNGRADES". 
        // So rank follows current streak.
        val currentRank = if (daysSinceQuit >= 0) {
            rankSystem.lastOrNull { it.daysRequired <= daysSinceQuit } ?: rankSystem.first()
        } else {
            null
        }

        provideContent {
            // Convert breaches to LocalDate set for easier checking
            val breachDates = breaches.map { 
                java.time.Instant.ofEpochMilli(it.timestamp)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
            }.toSet()
            
            CommitmentGridContent(daysSinceQuit, totalMoneySaved, currentRank?.title, userConfig?.currency ?: "$", breachDates)
        }
    }

    @SuppressLint("RestrictedApi")
    @Composable
    private fun CommitmentGridContent(daysSinceQuit: Long, moneySaved: Double, rankTitle: String?, currencySymbol: String, breachDates: Set<LocalDate>) {
        // Safe Colors
        val bgDark = ColorProvider(day = Color(0xFF0F1216), night = Color(0xFF0F1216))
        val borderGray = ColorProvider(day = Color(0xFF252A30), night = Color(0xFF252A30))
        val textGray = ColorProvider(day = Color(0xFF8B9BB4), night = Color(0xFF8B9BB4))
        val accentCyan = ColorProvider(day = Color(0xFF00E5FF), night = Color(0xFF00E5FF))
        val pillBg = ColorProvider(day = Color(0xFF1E2429), night = Color(0xFF1E2429))
        val whiteColor = ColorProvider(day = Color.White, night = Color.White)

        val context = LocalContext.current
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        // 1. OUTER BOX (Border)
        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(borderGray)
                .cornerRadius(16.dp)
                .padding(1.dp)
                .clickable(actionStartActivity(intent)), // Navigate on tap
            contentAlignment = Alignment.Center
        ) {
            // 2. INNER BOX (Main Background)
            Box(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .background(bgDark)
                    .cornerRadius(15.dp)
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                if (daysSinceQuit < 0) {
                    // Empty State
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Start Your Journey",
                            style = TextStyle(color = whiteColor, fontSize = 14.sp)
                        )
                    }
                } else {
                    // The Grid Visualization
                    Column(
                        modifier = GlanceModifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        // Header (Last 14 Days + Rank Badge)
                        Row(
                            modifier = GlanceModifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "LAST 14 DAYS",
                                style = TextStyle(
                                    color = textGray,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            
                            Spacer(modifier = GlanceModifier.defaultWeight())

                            // Rank Badge
                            if (rankTitle != null) {
                                Box(
                                    modifier = GlanceModifier
                                        .background(pillBg)
                                        .cornerRadius(4.dp)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = rankTitle.uppercase(),
                                        style = TextStyle(
                                            color = accentCyan,
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = GlanceModifier.defaultWeight())

                        // Day Header Row (M T W T F S S)
                        Row(
                            modifier = GlanceModifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            DayHeaderRow(startIndex = 6, count = 7)
                        }

                        Spacer(modifier = GlanceModifier.height(4.dp))

                        // Row 1 (History: 13 days ago to 7 days ago)
                        Row(
                            modifier = GlanceModifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            GridRow(startIndex = 13, count = 7, daysSinceQuit = daysSinceQuit, breachDates = breachDates)
                        }

                        Spacer(modifier = GlanceModifier.height(4.dp))

                        // Row 2 (Recent: 6 days ago to Today)
                        Row(
                            modifier = GlanceModifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            GridRow(startIndex = 6, count = 7, daysSinceQuit = daysSinceQuit, breachDates = breachDates)
                        }

                        Spacer(modifier = GlanceModifier.defaultWeight())

                        // Footer (Money Saved Pill + Streak)
                        Row(
                            modifier = GlanceModifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Money Saved Pill
                            Box(
                                modifier = GlanceModifier
                                    .background(ColorProvider(day = Color(0xFF1A1D21), night = Color(0xFF1A1D21))) 
                                    .cornerRadius(12.dp)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Saved ",
                                        style = TextStyle(color = textGray, fontSize = 10.sp)
                                    )
                                    Text(
                                        text = "$currencySymbol${String.format("%.0f", moneySaved)}",
                                        style = TextStyle(
                                            color = whiteColor,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                            
                            Spacer(modifier = GlanceModifier.defaultWeight())

                            // Streak
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Streak: ",
                                    style = TextStyle(color = textGray, fontSize = 12.sp)
                                )
                                Text(
                                    text = "$daysSinceQuit Days",
                                    style = TextStyle(
                                        color = accentCyan,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun RowScope.DayHeaderRow(startIndex: Int, count: Int) {
        val textGray = ColorProvider(day = Color(0xFF8B9BB4), night = Color(0xFF8B9BB4))
        
        repeat(count) { i ->
            val daysAgo = startIndex - i
            // Calculate Day Letter (M, T, W...)
            val date = LocalDate.now().minusDays(daysAgo.toLong())
            val dayInitial = date.dayOfWeek.getDisplayName(JavaTextStyle.NARROW, Locale.getDefault()).take(1)

            Box(
                 modifier = GlanceModifier
                    .defaultWeight()
                    .padding(horizontal = 2.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = dayInitial,
                    style = TextStyle(
                        color = textGray,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }

    @Composable
    private fun RowScope.GridRow(startIndex: Int, count: Int, daysSinceQuit: Long, breachDates: Set<LocalDate>) {
        repeat(count) { i ->
            val daysAgo = startIndex - i
            val date = LocalDate.now().minusDays(daysAgo.toLong())
            val isBreach = breachDates.contains(date)
            
            // Logic:
            // If Breach -> RED
            // If Today -> CYAN (unless breach)
            // If Past & Covered by streak -> GREEN (unless breach)
            // Else -> DARK GREY (Unachieved)
            
            val isToday = (daysAgo == 0)
            val isSmokeFree = (daysAgo <= daysSinceQuit)
            
            val dateNumber = date.dayOfMonth.toString()

            val rawColor = when {
                isBreach -> Color(0xFFD32F2F) // RED
                isToday -> Color(0xFF00E5FF)
                isSmokeFree -> Color(0xFF2E8B57)
                else -> Color(0xFF252A30)
            }
            val boxColor = ColorProvider(day = rawColor, night = rawColor)
            val textColor = if (isToday || isSmokeFree || isBreach) Color.Black else Color.Gray
            val textCP = ColorProvider(day = textColor, night = textColor)
            
            // Text to show: Date normally, but maybe "X" for breach?
            // User request: "Mark the specific day of the breach as RED (or an "X")."
            // Let's use "X" for breach, Date for others.
            val displayText = if (isBreach) "X" else dateNumber

            // The Box for the Day
            Box(
                modifier = GlanceModifier
                    .defaultWeight()
                    .height(36.dp)
                    .padding(2.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .background(boxColor)
                        .cornerRadius(6.dp),
                    contentAlignment = Alignment.Center
                ) {
                     Text(
                        text = displayText,
                        style = TextStyle(
                            color = textCP,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}

class CommitmentGridWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = CommitmentGridWidget()
}