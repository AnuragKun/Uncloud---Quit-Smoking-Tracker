package com.arlabs.uncloud.presentation.ranking

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.arlabs.uncloud.domain.model.Rank
import com.arlabs.uncloud.domain.model.rankSystem
import com.arlabs.uncloud.presentation.home.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RankingScreen(
    onNavigateBack: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel() // Reuse HomeViewModel as it has the config
) {
    val state by viewModel.uiState.collectAsState()
    val daysSinceQuit = state.daysSinceQuit

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("The Protocol", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
            )
        },
        containerColor = Color.Black
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            rankSystem.forEachIndexed { index, rank ->
                val isCompleted = daysSinceQuit >= rank.daysRequired
                val isCurrent = isCompleted && (index == rankSystem.lastIndex || daysSinceQuit < rankSystem[index + 1].daysRequired)
                val isLocked = !isCompleted

                RankItem(
                    rank = rank,
                    isCompleted = isCompleted,
                    isCurrent = isCurrent,
                    isLocked = isLocked,
                    isLast = index == rankSystem.lastIndex
                )
            }
        }
    }
}

@Composable
fun RankItem(
    rank: Rank,
    isCompleted: Boolean,
    isCurrent: Boolean,
    isLocked: Boolean,
    isLast: Boolean
) {
    Row(modifier = Modifier.height(androidx.compose.foundation.layout.IntrinsicSize.Min)) {
        // Timeline Line & Dot
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(if (isLocked) Color.DarkGray else rank.color)
            )
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .fillMaxHeight()
                        .background(Color.DarkGray)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Content
        Column(modifier = Modifier.padding(bottom = 32.dp)) {
            if (isCurrent) {
                // Hero Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = rank.color.copy(alpha = 0.15f)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, rank.color),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "CURRENT RANK",
                            style = MaterialTheme.typography.labelSmall,
                            color = rank.color
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = rank.title,
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = rank.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.LightGray
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = 1f, // Full for current (conceptually) or calculate precise progress to next rank? 
                            // Current design says "Expanded Card... Hero". 
                            // Let's just keep it simple.
                            color = rank.color,
                            trackColor = Color.DarkGray,
                            modifier = Modifier.fillMaxWidth().height(4.dp)
                        )
                    }
                }
            } else {
                // Standard List Item
                Text(
                    text = rank.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isLocked) Color.Gray else rank.color,
                    fontWeight = if (isCompleted) FontWeight.Bold else FontWeight.Normal
                )
                if (isLocked) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Lock,
                            contentDescription = "Locked",
                            tint = Color.Gray,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Unlocks at Day ${rank.daysRequired}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                } else {
                     Text(
                        text = "Completed",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}
