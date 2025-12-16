package com.arlabs.uncloud.presentation.ranking

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.arlabs.uncloud.domain.model.Rank
import com.arlabs.uncloud.domain.model.rankSystem
import com.arlabs.uncloud.presentation.home.HomeViewModel

// Theme Constants
private val SysDark = Color(0xFF0D1117)
private val SysPanel = Color(0xFF161B22)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RankingScreen(
    onNavigateBack: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val daysSinceQuit = state.daysSinceQuit

    // Background Brush
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(SysDark, Color.Black)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "CLEARANCE LEVELS",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp,
                            color = Color(0xFF00E5FF)
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Gray
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color.Transparent
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundBrush)
                .background(backgroundBrush)
                // .padding(padding) // Background extends
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(padding) // Content respects bars
                    .padding(24.dp)
            ) {
                // Header Stats
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val daysLabel = if (daysSinceQuit == 1L) "DAY" else "DAYS"
                    StatBadge(label = "CURRENT STREAK", value = "$daysSinceQuit $daysLabel")
                    val level = rankSystem.indexOf(state.currentRank) + 1
                    StatBadge(label = "CURRENT CLEARANCE", value = "LEVEL $level")
                }

                // The List
                rankSystem.forEachIndexed { index, rank ->
                    val isCompleted = daysSinceQuit >= rank.daysRequired
                    // Check if this is the active rank (Completed, but next one isn't)
                    val nextRankDays = rankSystem.getOrNull(index + 1)?.daysRequired ?: Int.MAX_VALUE
                    val isCurrent = daysSinceQuit >= rank.daysRequired && daysSinceQuit < nextRankDays

                    val isLocked = !isCompleted

                    RankTimelineItem(
                        rank = rank,
                        index = index,
                        totalRanks = rankSystem.size,
                        daysSinceQuit = daysSinceQuit,
                        isCompleted = isCompleted,
                        isCurrent = isCurrent,
                        isLocked = isLocked,
                        nextRankRequirement = nextRankDays
                    )
                }
            }
        }
    }
}

@Composable
fun RankTimelineItem(
    rank: Rank,
    index: Int,
    totalRanks: Int,
    daysSinceQuit: Long,
    isCompleted: Boolean,
    isCurrent: Boolean,
    isLocked: Boolean,
    nextRankRequirement: Int
) {
    IntrinsicHeightRow {
        // --- 1. TIMELINE TRACK (Left) ---
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(40.dp)
        ) {
            // Upper Line
            if (index > 0) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .weight(1f)
                        .background(
                            if (isCompleted || isCurrent) rank.color.copy(alpha = 0.5f)
                            else Color(0xFF30363D)
                        )
                )
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }

            // The Node (Circle)
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            isCurrent -> rank.color.copy(alpha = 0.2f) // Glowing halo
                            isCompleted -> rank.color
                            else -> SysPanel
                        }
                    )
                    .border(
                        width = 2.dp,
                        color = if (isLocked) Color(0xFF30363D) else rank.color,
                        shape = CircleShape
                    )
            ) {
                if (isCompleted && !isCurrent) {
                    Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(14.dp)
                    )
                } else if (isLocked) {
                    Icon(
                        imageVector = Icons.Rounded.Lock,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(12.dp)
                    )
                } else if (isCurrent) {
                    Box(modifier = Modifier.size(8.dp).background(rank.color, CircleShape))
                }
            }

            // Lower Line
            if (index < totalRanks - 1) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .weight(1f)
                        .background(
                            if (isCompleted) rankSystem[index + 1].color.copy(alpha = 0.5f)
                            else Color(0xFF30363D)
                        )
                )
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }
        }

        // --- 2. CONTENT CARD (Right) ---
        Column(
            modifier = Modifier
                .padding(start = 12.dp, bottom = 24.dp)
                .weight(1f)
        ) {
            if (isCurrent) {
                // ACTIVE HERO CARD (Always expanded, detailed)
                CurrentRankCard(rank, daysSinceQuit, nextRankRequirement)
            } else {
                // EXPANDABLE ROW (For Completed or Locked)
                ExpandableRankRow(rank, isLocked)
            }
        }
    }
}

@Composable
fun CurrentRankCard(rank: Rank, currentDays: Long, nextGoal: Int) {
    // Calculate progress to next rank
    val prevGoal = rank.daysRequired
    val progress = if (nextGoal == Int.MAX_VALUE) 1f else {
        (currentDays - prevGoal).toFloat() / (nextGoal - prevGoal).toFloat()
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = SysPanel),
        border = androidx.compose.foundation.BorderStroke(1.dp, rank.color),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "STATUS: ACTIVE",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        color = rank.color,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        color = Color.Gray
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = rank.title.uppercase(),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = Color.White
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = rank.description,
                style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF8B949E))
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Progress Bar
            Column {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth().height(4.dp),
                    color = rank.color,
                    trackColor = Color(0xFF0D1117),
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("$currentDays Days", fontSize = 10.sp, color = Color.Gray, fontFamily = FontFamily.Monospace)
                    if (nextGoal != Int.MAX_VALUE) {
                        Text("Next: $nextGoal", fontSize = 10.sp, color = Color.Gray, fontFamily = FontFamily.Monospace)
                    } else {
                        Text("MAX", fontSize = 10.sp, color = Color.Gray, fontFamily = FontFamily.Monospace)
                    }
                }
            }
        }
    }
}

@Composable
fun ExpandableRankRow(rank: Rank, isLocked: Boolean) {
    var expanded by remember { mutableStateOf(false) }
    val rotationState by animateFloatAsState(targetValue = if (expanded) 180f else 0f, label = "rotation")

    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isLocked) Color.Transparent else SysPanel.copy(alpha = 0.5f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isLocked) Color(0xFF30363D) else Color.Transparent
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // -- HEADER ROW --
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = rank.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = if (isLocked) FontWeight.Normal else FontWeight.Bold,
                            color = if (isLocked) Color.Gray else Color.White,
                            fontFamily = FontFamily.Monospace
                        )
                    )

                    // Subtitle/Status
                    if (isLocked) {
                        Text(
                            text = "REQUIRES ${rank.daysRequired} DAYS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.DarkGray,
                                fontFamily = FontFamily.Monospace
                            )
                        )
                    } else {
                        Text(
                            text = "PROTOCOL COMPLETE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = rank.color,
                                fontFamily = FontFamily.Monospace
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Chevron or Lock
                if (isLocked) {
                    // Even if locked, we show a chevron to indicate you can peek details
                    Icon(
                        imageVector = Icons.Rounded.KeyboardArrowDown,
                        contentDescription = "Expand",
                        tint = Color.DarkGray,
                        modifier = Modifier.rotate(rotationState)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Rounded.KeyboardArrowDown,
                        contentDescription = "Expand",
                        tint = Color.Gray,
                        modifier = Modifier.rotate(rotationState)
                    )
                }
            }

            // -- EXPANDED CONTENT --
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = Color(0xFF30363D), thickness = 1.dp)
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = rank.description,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = if (isLocked) Color.Gray else Color(0xFFC9D1D9),
                            lineHeight = 20.sp
                        )
                    )

                    if (isLocked) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "ACCESS DENIED. CONTINUE PROGRESSION.",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFFEF5350), // Red warning color
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatBadge(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                color = Color.Gray,
                fontFamily = FontFamily.Monospace
            )
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontFamily = FontFamily.Monospace
            )
        )
    }
}

// Helper to make the timeline line stretch properly
@Composable
fun IntrinsicHeightRow(content: @Composable RowScope.() -> Unit) {
    Row(
        modifier = Modifier.height(IntrinsicSize.Min),
        content = content
    )
}