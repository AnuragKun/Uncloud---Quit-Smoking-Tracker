package com.arlabs.uncloud.presentation.home

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.arlabs.uncloud.R
import com.arlabs.uncloud.presentation.home.components.HealthPreviewCard
import com.arlabs.uncloud.presentation.home.components.HeroTimer
import com.arlabs.uncloud.presentation.home.components.MotivationCard
import com.arlabs.uncloud.presentation.home.components.StatGrid

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
        viewModel: HomeViewModel = hiltViewModel(),
        onNavigateToHealth: () -> Unit,
        onNavigateToSettings: () -> Unit
) {
        val state by viewModel.uiState.collectAsState()
        val scrollState = rememberScrollState()
        val context = LocalContext.current

        // Background Gradient Scaffold
        Scaffold(
                containerColor = Color.Black, // Fallback
                topBar = {
                        Row(
                                modifier =
                                        Modifier.fillMaxWidth()
                                                .padding(horizontal = 8.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                        ) {
                                Icon(
                                        painter = painterResource(id = R.drawable.icon_app_logo),
                                        contentDescription = "App Logo",
                                        tint = Color.Unspecified,
                                        modifier =
                                                Modifier.padding(start = 16.dp)
                                                        .size(48.dp)
                                                        // Add clip AFTER setting the size to round
                                                        // the 32dp box
                                                        .clip(RoundedCornerShape(8.dp))
                                )
                                Row {
                                        IconButton(
                                                onClick = {
                                                        val currency =
                                                                state.userConfig?.currency ?: "$"
                                                        val moneySavedFormatted =
                                                                String.format(
                                                                        "%.2f",
                                                                        state.moneySaved
                                                                )
                                                        val message =
                                                                "I’m officially ${state.daysSinceQuit} days smoke-free! ☁️\n\n" +
                                                                        "So far I have:\n" +
                                                                        "🚫 Avoided ${state.cigarettesAvoided} cigarettes\n" +
                                                                        "⏳ Reclaimed ${state.scheduleTimeRegained} of life\n" +
                                                                        "💸 Saved $currency$moneySavedFormatted\n\n" +
                                                                        "Uncloud is helping me track my recovery. Join me here: https://play.google.com/store/apps/details?id=com.example.quitsmoking"

                                                        val sendIntent: Intent =
                                                                Intent().apply {
                                                                        action = Intent.ACTION_SEND
                                                                        putExtra(
                                                                                Intent.EXTRA_TEXT,
                                                                                message
                                                                        )
                                                                        type = "text/plain"
                                                                }
                                                        val shareIntent =
                                                                Intent.createChooser(
                                                                        sendIntent,
                                                                        "Share your achievement"
                                                                )
                                                        context.startActivity(shareIntent)
                                                }
                                        ) {
                                                Icon(
                                                        imageVector = Icons.Default.Share,
                                                        contentDescription = "Share Streak",
                                                        tint = Color.White
                                                )
                                        }
                                        IconButton(onClick = onNavigateToSettings) {
                                                Icon(
                                                        imageVector = Icons.Default.Settings,
                                                        contentDescription = "Settings",
                                                        tint = Color.White
                                                )
                                        }
                                }
                        }
                }
        ) { padding ->
                Box(
                        modifier =
                                Modifier.fillMaxSize()
                                        .background(
                                                brush =
                                                        Brush.verticalGradient(
                                                                colors =
                                                                        listOf(
                                                                                Color(0xFF0F1216),
                                                                                Color(0xFF000000)
                                                                        )
                                                        )
                                        )
                                        .padding(padding)
                ) {
                        Column(
                                modifier =
                                        Modifier.fillMaxSize()
                                                .verticalScroll(scrollState)
                                                .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                                // Header
                                Spacer(modifier = Modifier.height(16.dp))
                                HeroTimer(
                                        days = state.daysSinceQuit,
                                        hours = state.hoursSinceQuit,
                                        minutes = state.minutesSinceQuit,
                                        seconds = state.secondsSinceQuit
                                )

                                Spacer(modifier = Modifier.height(24.dp))

                                MotivationCard(
                                        quote = state.quote,
                                        onRefresh = viewModel::refreshQuote
                                )

                                Spacer(modifier = Modifier.height(24.dp))

                                // Stats
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

                                HealthPreviewCard(
                                        milestone = state.currentMilestone,
                                        progress = state.milestoneProgress,
                                        onClick = onNavigateToHealth
                                )

                                if (state.selectedStatType != null && state.projectedStats != null
                                ) {
                                        com.arlabs.uncloud.presentation.home.components
                                                .ProjectionDetailDialog(
                                                        statType = state.selectedStatType!!,
                                                        stats = state.projectedStats!!,
                                                        currency = state.userConfig?.currency
                                                                        ?: "$",
                                                        onDismiss = viewModel::dismissDialog
                                                )

                                        Spacer(modifier = Modifier.height(32.dp))
                                }
                        }
                }
        }
}
