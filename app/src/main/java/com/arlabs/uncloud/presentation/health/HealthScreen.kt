package com.arlabs.uncloud.presentation.health

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun HealthScreen(
        viewModel: HealthViewModel = hiltViewModel(),
        onNavigateBack: () -> Unit // In case we add a back button later, or handled by system back
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(containerColor = Color.Black) { padding ->
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
            LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
            ) {
                item {
                    Text(
                            text = "Health Timeline",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                items(state.milestones) { item -> HealthMilestoneItem(item) }
            }
        }
    }
}

@Composable
fun HealthMilestoneItem(item: HealthMilestoneUiModel) {
    val containerColor =
            when (item.status) {
                MilestoneStatus.COMPLETED -> Color(0xFF1E2429)
                MilestoneStatus.IN_PROGRESS -> Color(0xFF252A31)
                MilestoneStatus.LOCKED -> Color(0xFF121518)
            }

    val contentAlpha = if (item.status == MilestoneStatus.LOCKED) 0.5f else 1f

    // Border for active item
    val borderColor =
            if (item.status == MilestoneStatus.IN_PROGRESS) Color(0xFF4CAF50) else Color.Transparent
    val borderWidth = if (item.status == MilestoneStatus.IN_PROGRESS) 1.dp else 0.dp

    Card(
            colors = CardDefaults.cardColors(containerColor = containerColor),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(),
            border =
                    if (item.status == MilestoneStatus.IN_PROGRESS)
                            androidx.compose.foundation.BorderStroke(borderWidth, borderColor)
                    else null
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Icon Status
                Box(
                        modifier =
                                Modifier.size(40.dp)
                                        .clip(CircleShape)
                                        .background(
                                                when (item.status) {
                                                    MilestoneStatus.COMPLETED ->
                                                            Color(0xFF4CAF50).copy(alpha = 0.2f)
                                                    MilestoneStatus.IN_PROGRESS ->
                                                            Color(0xFF2196F3).copy(alpha = 0.2f)
                                                    MilestoneStatus.LOCKED ->
                                                            Color.Gray.copy(alpha = 0.1f)
                                                }
                                        ),
                        contentAlignment = Alignment.Center
                ) {
                    when (item.status) {
                        MilestoneStatus.COMPLETED ->
                                Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Completed",
                                        tint = Color(0xFF4CAF50),
                                        modifier = Modifier.size(24.dp)
                                )
                        MilestoneStatus.IN_PROGRESS -> Text(text = "🔥", fontSize = 20.sp)
                        MilestoneStatus.LOCKED ->
                                Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = "Locked",
                                        tint = Color.Gray,
                                        modifier = Modifier.size(20.dp)
                                )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                            text = item.milestone.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White.copy(alpha = contentAlpha),
                            fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                            text = item.milestone.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFB0B3B8).copy(alpha = contentAlpha)
                    )
                }
            }

            // Footer Information (Date or Progress or Due Time)
            Spacer(modifier = Modifier.height(16.dp))

            when (item.status) {
                MilestoneStatus.COMPLETED -> {
                    Text(
                            text = "Achieved on ${item.achievedDate}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF4CAF50)
                    )
                }
                MilestoneStatus.IN_PROGRESS -> {
                    Column {
                        Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                    text = "In Progress",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF2196F3)
                            )
                            Text(
                                    text = "${(item.progress * 100).toInt()}%",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                                progress = { item.progress },
                                modifier = Modifier.fillMaxWidth().height(6.dp),
                                color = Color(0xFF2196F3),
                                trackColor = Color(0xFF1E2429),
                                strokeCap = androidx.compose.ui.graphics.StrokeCap.Round,
                        )
                    }
                }
                MilestoneStatus.LOCKED -> {
                    Text(
                            text = item.dueTimeText ?: "Upcoming",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                    )
                }
            }
        }
    }
}
