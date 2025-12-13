package com.arlabs.uncloud.presentation.protocol

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.arlabs.uncloud.presentation.home.HomeViewModel
import java.time.Instant
import java.time.ZoneId
import com.arlabs.uncloud.R
import java.time.format.DateTimeFormatter

@Composable
fun ProtocolScreen(
    onNavigateToBreach: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val breaches = uiState.breaches

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F1216))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- HEADER ---
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "SYSTEM MANAGEMENT",
            color = Color.Gray,
            fontSize = 12.sp,
            letterSpacing = 1.sp
        )
        Text(
            text = "PROTOCOL STATUS",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // --- RANKING DISPLAY ---
        val daysSinceQuit = uiState.daysSinceQuit
        val currentRank = com.arlabs.uncloud.domain.model.rankSystem.lastOrNull { 
            it.daysRequired <= daysSinceQuit 
        } ?: com.arlabs.uncloud.domain.model.rankSystem.first()

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF161B22), RoundedCornerShape(12.dp))
                .border(1.dp, currentRank.color.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Rank Icon/Indicator
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(currentRank.color.copy(alpha = 0.2f), androidx.compose.foundation.shape.CircleShape)
                        .border(1.dp, currentRank.color, androidx.compose.foundation.shape.CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.icon_shield),
                        contentDescription = null,
                        tint = currentRank.color,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = "CURRENT IDENTITY",
                        color = Color.Gray,
                        fontSize = 10.sp,
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = currentRank.title.uppercase(),
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = currentRank.description,
                        color = Color(0xFF8B9BB4),
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- STATUS CARD / LOGS ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color(0xFF161B22), RoundedCornerShape(16.dp))
                .border(1.dp, Color(0xFF252A30), RoundedCornerShape(16.dp))
        ) {
            if (breaches.isEmpty()) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "System Nominal",
                        color = Color(0xFF00E5FF), // Cyan
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No active incidents logged.",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                         Text(
                            text = "INCIDENT LOG",
                            color = Color(0xFFEF5350),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    items(breaches.sortedByDescending { it.timestamp }) { breach ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF0F1216), RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = breach.trigger.uppercase(),
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                val date = Instant.ofEpochMilli(breach.timestamp)
                                    .atZone(ZoneId.systemDefault())
                                    .format(DateTimeFormatter.ofPattern("MMM dd, HH:mm"))
                                Text(
                                    text = date,
                                    color = Color.Gray,
                                    fontSize = 12.sp
                                )
                            }
                            // Notes if we had them
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- DANGER ZONE FOOTER ---
        Text(
            text = "EMERGENCY OVERRIDE",
            color = Color(0xFFEF5350).copy(alpha = 0.7f),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
             onClick = onNavigateToBreach,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E0F0F)), // Dark Red BG
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF5350).copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Warning,
                contentDescription = null,
                tint = Color(0xFFEF5350),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "REPORT PROTOCOL BREACH",
                color = Color(0xFFEF5350),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                letterSpacing = 1.sp
            )
        }

        Spacer(modifier = Modifier.height(80.dp)) // Bottom Nav spacing
    }
}