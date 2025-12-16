package com.arlabs.uncloud.presentation.home

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.arlabs.uncloud.R
import com.arlabs.uncloud.domain.model.Rank
import com.arlabs.uncloud.presentation.home.components.*

@Composable
fun HomeScreen(
        viewModel: HomeViewModel = hiltViewModel(),
        onNavigateToHealth: () -> Unit,
        onNavigateToSettings: () -> Unit,
        onNavigateToRanking: () -> Unit,
        onNavigateToPanic: () -> Unit
) {
        val state by viewModel.uiState.collectAsState()
        val scrollState = rememberScrollState()
        val context = LocalContext.current

        // Cyberpunk Background Brush
        val backgroundBrush = Brush.verticalGradient(
                colors = listOf(
                        Color(0xFF0D1117), // Deep Space Blue/Black
                        Color(0xFF000000)
                )
        )

        Scaffold(
                containerColor = Color.Black,
                topBar = {
                        HomeTopBar(
                                onSettingsClick = onNavigateToSettings,
                                onShareClick = {
                                        val currency = state.userConfig?.currency ?: "$"
                                        val moneySavedFormatted = String.format("%.2f", state.moneySaved)

                                        // MIXED THEME: Tech formatting + Human words
                                        val message = "// SYSTEM STATUS: SMOKE-FREE\n\n" +
                                                "• Current Streak: ${state.daysSinceQuit} Days\n" +
                                                "• Cigarettes Blocked: ${state.cigarettesAvoided}\n" +
                                                "• Money Saved: $currency$moneySavedFormatted\n\n" +
                                                "Clear the air with Uncloud: https://play.google.com/store/apps/details?id=com.arlabs.uncloud"

                                        val sendIntent = Intent().apply {
                                                action = Intent.ACTION_SEND
                                                putExtra(Intent.EXTRA_TEXT, message)
                                                type = "text/plain"
                                        }
                                        // "Log Entry" keeps the theme without being aggressive
                                        context.startActivity(Intent.createChooser(sendIntent, "Share Log Entry"))
                                }
                        )
                }
        ) { padding ->
                Box(
                        modifier = Modifier
                                .fillMaxSize()
                                .background(backgroundBrush)
                .background(backgroundBrush)
                                // .padding(padding) // Background extends behind bars
                ) {
                        Column(
                                modifier = Modifier
                                        .fillMaxSize()
                                        .verticalScroll(scrollState)
                                        .padding(padding) // Content respects bars
                                        .padding(horizontal = 20.dp), // slightly tighter padding
                                horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                                Spacer(modifier = Modifier.height(12.dp))

                                // 1. PRIMARY STATUS MODULE (Timer + Rank)
                                HeroTimer(
                                        years = state.yearsSinceQuit,
                                        months = state.monthsSinceQuit,
                                        days = state.timerDays,
                                        hours = state.hoursSinceQuit,
                                        minutes = state.minutesSinceQuit,
                                        seconds = state.secondsSinceQuit
                                )

                                Spacer(modifier = Modifier.height(24.dp))

                                // Rank Chip (Clickable for full timeline)
                                state.currentRank?.let { rank ->
                                        IdentityChip(rank = rank, onClick = onNavigateToRanking)
                                }

                                Spacer(modifier = Modifier.height(32.dp))

                                // 2. EMERGENCY OVERRIDE (Panic Button)
                                // Critical feature that needs to be accessible
                                PanicActionButton(onClick = onNavigateToPanic)

                                Spacer(modifier = Modifier.height(32.dp))

                                // 3. DAILY CLARITY (Motivation)
                                MotivationCard(
                                        quote = state.quote,
                                        onRefresh = viewModel::refreshQuote
                                )

                                Spacer(modifier = Modifier.height(24.dp))

                                // 4. SYSTEM MODULES (Stats Grid)
                                StatGrid(
                                        cigarettesAvoided = state.cigarettesAvoided,
                                        moneySaved = state.moneySaved,
                                        scheduleTimeRegained = state.scheduleTimeRegained,
                                        biologicalTimeRegained = state.biologicalTimeRegained,
                                        completedMilestones = state.completedMilestones,
                                        totalMilestones = state.totalMilestones,
                                        currency = state.userConfig?.currency ?: "$",
                                        onStatClick = viewModel::selectStat,
                                        onHealthClick = onNavigateToHealth
                                )

                                Spacer(modifier = Modifier.height(24.dp))

                                // 5. DIAGNOSTICS (Health)
                                HealthPreviewCard(
                                        milestone = state.currentMilestone,
                                        progress = state.milestoneProgress,
                                        onClick = onNavigateToHealth
                                )

                                Spacer(modifier = Modifier.height(48.dp)) // Bottom padding
                        }
                }

                // Render Dialogs
                if (state.selectedStatType != null && state.projectedStats != null) {
                        com.arlabs.uncloud.presentation.home.components.ProjectionDetailDialog(
                                statType = state.selectedStatType!!,
                                stats = state.projectedStats!!,
                                currency = state.userConfig?.currency ?: "$",
                                onDismiss = viewModel::dismissDialog
                        )
                }
        }
}

// --- SUB-COMPONENTS ---

@Composable
fun HomeTopBar(onSettingsClick: () -> Unit, onShareClick: () -> Unit) {
        Row(
                modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0D1117)) // Matches gradient top
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
        ) {
                // App Logo / Title
                Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                                painter = painterResource(id = R.drawable.icon_app_logo),
                                contentDescription = null,
                                tint = Color.Unspecified,
                                modifier = Modifier
                                        .size(32.dp)
                                        .clip(RoundedCornerShape(8.dp))
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                                text = "UNCLOUD // PROTOCOL",
                                style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 2.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = Color.Gray
                                )
                        )
                }

                // Actions
                Row {
                        IconButton(onClick = onShareClick) {
                                Icon(
                                        imageVector = Icons.Rounded.Share,
                                        contentDescription = "Share",
                                        tint = Color(0xFF8B949E)
                                )
                        }
                        IconButton(onClick = onSettingsClick) {
                                Icon(
                                        imageVector = Icons.Rounded.Settings,
                                        contentDescription = "Config",
                                        tint = Color(0xFF8B949E)
                                )
                        }
                }
        }
}

@Composable
fun IdentityChip(
        rank: Rank,
        onClick: () -> Unit
) {
        Surface(
                onClick = onClick,
                shape = RoundedCornerShape(50),
                color = Color(0xFF161B22),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF30363D))
        ) {
                Row(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                        Icon(
                                painter = painterResource(R.drawable.icon_shield),
                                contentDescription = null,
                                tint = rank.color,
                                modifier = Modifier.size(16.dp)
                        )

                        Column {
                                Text(
                                        text = "CURRENT CLEARANCE",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 8.sp,
                                                color = Color.Gray
                                        )
                                )
                                Text(
                                        text = rank.title.uppercase(),
                                        color = rank.color,
                                        style = MaterialTheme.typography.labelMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                letterSpacing = 1.sp,
                                                fontFamily = FontFamily.Monospace
                                        )
                                )
                        }

                        Icon(
                                imageVector = Icons.Filled.ArrowForward,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(14.dp)
                        )
                }
        }
}

@Composable
fun PanicActionButton(onClick: () -> Unit) {
        Button(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF21262D),
                        contentColor = Color(0xFFFF5252)
                ),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF5252).copy(alpha = 0.3f))
        ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                                imageVector = Icons.Filled.Warning,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                                text = "INITIATE PANIC DEFENSE",
                                style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                )
                        )
                }
        }
}