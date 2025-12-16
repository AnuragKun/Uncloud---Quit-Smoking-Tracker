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
import androidx.glance.layout.width
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

        val trackingStartTimestamp = userConfig?.trackingStartDate ?: quitTimestamp
        val trackingStartDate = if (trackingStartTimestamp > 0L) {
             java.time.Instant.ofEpochMilli(trackingStartTimestamp)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
        } else {
            java.time.Instant.ofEpochMilli(quitTimestamp) // Fallback
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
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

            CommitmentGridContent(daysSinceQuit, totalMoneySaved, currentRank?.title, userConfig?.currency ?: "$", breachDates, trackingStartDate)
        }
    }

    @SuppressLint("RestrictedApi")
    @Composable
    private fun CommitmentGridContent(daysSinceQuit: Long, moneySaved: Double, rankTitle: String?, currencySymbol: String, breachDates: Set<LocalDate>, trackingStartDate: LocalDate) {
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
                .background(cyanNeon) // Border
                .cornerRadius(12.dp)
                .padding(2.dp)
                .clickable(actionStartActivity(intent)), // Navigate on tap
            contentAlignment = Alignment.Center
        ) {
            // 2. INNER BOX (Main Background)
            Box(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .background(bgMatte)
                    .cornerRadius(10.dp)
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                if (daysSinceQuit < 0) {
                    // Empty State
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "AWAITING PROTOCOL...",
                            style = TextStyle(color = textDim, fontSize = 12.sp, fontWeight = FontWeight.Bold)
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
                                text = "COMMAND :: VISUAL_LOG",
                                style = TextStyle(
                                    color = cyanNeon,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )

                            Spacer(modifier = GlanceModifier.defaultWeight())

                            // Rank Badge
                            if (rankTitle != null) {
                                Box(
                                    modifier = GlanceModifier
                                        .background(ColorProvider(day = Color(0xFF1E2429), night = Color(0xFF1E2429))) // Dark pill
                                        .cornerRadius(4.dp)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = rankTitle.uppercase(),
                                        style = TextStyle(
                                            color = greenNeon,
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
                            GridRow(startIndex = 13, count = 7, daysSinceQuit = daysSinceQuit, breachDates = breachDates, hasHistory = (daysSinceQuit > 0) || (moneySaved > 0), trackingStartDate = trackingStartDate)
                        }

                        Spacer(modifier = GlanceModifier.height(4.dp))

                        // Row 2 (Recent: 6 days ago to Today)
                        Row(
                            modifier = GlanceModifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            GridRow(startIndex = 6, count = 7, daysSinceQuit = daysSinceQuit, breachDates = breachDates, hasHistory = (daysSinceQuit > 0) || (moneySaved > 0), trackingStartDate = trackingStartDate)
                        }

                        Spacer(modifier = GlanceModifier.defaultWeight())

                        // Footer (Money Saved Pill + Streak)
                        Row(
                            modifier = GlanceModifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Money Saved
                             Column(modifier = GlanceModifier.defaultWeight()) {
                                Text(
                                    text = "RESOURCES",
                                    style = TextStyle(color = textDim, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "$currencySymbol${String.format("%.0f", moneySaved)}",
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

                            // Streak
                            Column(modifier = GlanceModifier.defaultWeight()) {
                                Text(
                                    text = "UPTIME",
                                    style = TextStyle(color = textDim, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                )
                                val dayLabel = if (daysSinceQuit == 1L) "DAY" else "DAYS"
                                Text(
                                    text = "$daysSinceQuit $dayLabel",
                                    style = TextStyle(
                                        color = textBright,
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
        val textDim = ColorProvider(day = Color(0xFF546E7A), night = Color(0xFF546E7A))

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
                        color = textDim,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }

    @Composable
    private fun RowScope.GridRow(startIndex: Int, count: Int, daysSinceQuit: Long, breachDates: Set<LocalDate>, hasHistory: Boolean, trackingStartDate: LocalDate) {
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
            

            
            // Only consider past success if the day is AFTER the tracking start date
            val isAfterStart = !date.isBefore(trackingStartDate)
            
            val isPastSuccess = !isToday && !isBreach && isAfterStart && (isSmokeFree || hasHistory)

            val dateNumber = date.dayOfMonth.toString()

            val rawColor = when {
                isBreach -> Color(0xFFFF2B2B) // RED
                isToday -> Color(0xFF00E5FF) // CYAN
                isPastSuccess || isSmokeFree -> Color(0xFF00E676) // GREEN
                else -> Color(0xFF1E2429) // DARK GREY
            }
            val boxColor = ColorProvider(day = rawColor, night = rawColor)
            val textColor = if (isToday || isSmokeFree || isBreach) Color.Black else Color.Gray
            val textCP = ColorProvider(day = textColor, night = textColor)

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