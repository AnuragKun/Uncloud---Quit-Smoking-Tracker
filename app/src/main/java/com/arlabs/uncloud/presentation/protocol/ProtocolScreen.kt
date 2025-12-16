package com.arlabs.uncloud.presentation.protocol

import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.arlabs.uncloud.R
import com.arlabs.uncloud.presentation.home.HomeViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

// --- THEME CONSTANTS ---
private val SysCyan = Color(0xFF00E5FF)
private val SysRed = Color(0xFFFF5252)
private val SysDark = Color(0xFF0D1117)
private val SysPanel = Color(0xFF161B22)
private val SysBorder = Color(0xFF30363D)

@Composable
fun ProtocolScreen(
    onNavigateToBreach: () -> Unit,
    onNavigateToRanking: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val breaches = uiState.breaches

    // Cyberpunk Gradient Background
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(SysDark, Color.Black)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .statusBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- HEADER ---
            Spacer(modifier = Modifier.height(24.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "SYSTEM MANAGEMENT",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color.Gray,
                        letterSpacing = 2.sp,
                        fontFamily = FontFamily.Monospace
                    )
                )
                Text(
                    text = "PROTOCOL STATUS",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    ),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- RANKING DISPLAY (Clickable) ---
            val daysSinceQuit = uiState.daysSinceQuit
            val currentRank = com.arlabs.uncloud.domain.model.rankSystem.lastOrNull {
                it.daysRequired <= daysSinceQuit
            } ?: com.arlabs.uncloud.domain.model.rankSystem.first()

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SysPanel),
                border = androidx.compose.foundation.BorderStroke(1.dp, currentRank.color.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToRanking() }
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Rank Icon/Indicator
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(currentRank.color.copy(alpha = 0.1f), androidx.compose.foundation.shape.CircleShape)
                            .border(1.dp, currentRank.color, androidx.compose.foundation.shape.CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.icon_shield),
                            contentDescription = null,
                            tint = currentRank.color,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(20.dp))

                    Column {
                        Text(
                            text = "CURRENT IDENTITY // LEVEL ${com.arlabs.uncloud.domain.model.rankSystem.indexOf(currentRank) + 1}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.Gray,
                                fontFamily = FontFamily.Monospace
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = currentRank.title.uppercase(),
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = currentRank.description,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF8B9BB4),
                                lineHeight = 16.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- STATUS CARD / LOGS ---
            // This fills the remaining space
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SysPanel),
                border = androidx.compose.foundation.BorderStroke(1.dp, SysBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Log Header
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "INCIDENT LOG",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = SysRed,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 1.sp
                            )
                        )

                        if (breaches.isNotEmpty()) {
                            Text(
                                text = "${breaches.size} RECORDS",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.Gray,
                                    fontFamily = FontFamily.Monospace
                                )
                            )
                        }
                    }

                    // Content
                    if (breaches.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "SYSTEM NOMINAL",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = SysCyan,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "No active incidents detected.",
                                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(breaches.sortedByDescending { it.timestamp }) { breach ->
                                // Single Log Item
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFF0F1216), RoundedCornerShape(4.dp))
                                        .border(1.dp, SysBorder, RoundedCornerShape(4.dp))
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "TRIGGER EVENT",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = Color.Gray,
                                                fontSize = 8.sp,
                                                fontFamily = FontFamily.Monospace
                                            )
                                        )
                                        Text(
                                            text = breach.trigger.uppercase(),
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                color = SysRed, // Red for danger
                                                fontWeight = FontWeight.Bold,
                                                fontFamily = FontFamily.Monospace
                                            )
                                        )
                                    }

                                    val date = Instant.ofEpochMilli(breach.timestamp)
                                        .atZone(ZoneId.systemDefault())
                                        .format(DateTimeFormatter.ofPattern("MM/dd HH:mm"))

                                    Text(
                                        text = date,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = Color.Gray,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- DANGER ZONE FOOTER ---
            Text(
                text = "EMERGENCY OVERRIDE",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = SysRed.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                ),
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onNavigateToBreach,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E0F0F)), // Dark Red BG
                shape = RoundedCornerShape(4.dp), // Tech corners
                border = androidx.compose.foundation.BorderStroke(1.dp, SysRed.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Warning,
                    contentDescription = null,
                    tint = SysRed,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "REPORT PROTOCOL BREACH",
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = SysRed,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                )
            }


            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "INITIATE ONLY UPON CONFIRMED RELAPSE (SMOKING EVENT)",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = SysRed.copy(alpha = 0.7f),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    textAlign = TextAlign.Center
                ),
            )

            Spacer(modifier = Modifier.height(64.dp)) // Bottom Nav spacing
        }
    }
}