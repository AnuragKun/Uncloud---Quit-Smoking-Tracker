package com.arlabs.uncloud.presentation.journal

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.arlabs.uncloud.data.local.entity.JournalEntry
import com.arlabs.uncloud.data.local.entity.SystemStatus
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalScreen(
    viewModel: JournalViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {} // Added for consistency, though may not be used if in bottom nav
) {
    val entries by viewModel.entries.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    // Cyberpunk Background
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(Color(0xFF0D1117), Color(0xFF000000))
    )

    val statusCounts by viewModel.statusCounts.collectAsState()
    val currentFilter by viewModel.currentFilter.collectAsState()

    var showHelpDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color.Transparent,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = Color(0xFF00E5FF),
                contentColor = Color.Black
            ) {
                Icon(Icons.Rounded.Add, contentDescription = "New Log")
            }
        },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "SYSTEM ARCHIVE",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp,
                            color = Color(0xFF00E5FF)
                        )
                    )
                },
                actions = {
                    IconButton(onClick = { showHelpDialog = true }) {
                        Icon(
                            imageVector = Icons.Rounded.Info,
                            contentDescription = "Help",
                            tint = Color.Gray
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundBrush)
                .background(backgroundBrush)
                // .padding(padding)
        ) {
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(padding)
            ) {
                // OBJECTIVE 2: Diagnostic HUD (Visual Header)
                if (statusCounts.isNotEmpty()) {
                    DiagnosticHud(statusCounts)
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // OBJECTIVE 1: Data Query Filtration Protocols (Status Filters)
                FilterChips(currentFilter) { status ->
                    viewModel.setFilter(status)
                }
                
                Spacer(modifier = Modifier.height(8.dp))

                if (entries.isEmpty()) {
                    // Empty State
                    Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "NO RECORDS FOUND\nINITIALIZE LOGGING...",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = FontFamily.Monospace,
                                color = Color.Gray,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        )
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(bottom = 80.dp, start = 16.dp, end = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(entries, key = { it.id }) { entry ->
                            SwipeToDeleteContainer(
                                item = entry,
                                onDelete = { viewModel.deleteEntry(entry) }
                            ) {
                                LogEntryCard(entry)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddLogDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { content, status, triggers ->
                viewModel.addEntry(content, status, triggers)
                showAddDialog = false
            }
        )
    }

    if (showHelpDialog) {
        AlertDialog(
            onDismissRequest = { showHelpDialog = false },
            containerColor = Color(0xFF0D1117),
            title = {
                Text(
                    "ARCHIVE PROTOCOL",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            },
            text = {
                Text(
                    "The System Archive is your tactical log for analyzing behavior patterns.\n\n" +
                            "• Record cravings and emotional states to identify triggers.\n" +
                            "• Review past entries to debug your response algorithms.\n" +
                            "• Consistent logging increases protocol success rate by 40%.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                )
            },
            confirmButton = {
                TextButton(onClick = { showHelpDialog = false }) {
                    Text("ACKNOWLEDGE", color = Color(0xFF00E5FF))
                }
            }
        )
    }
}

// --- COMPONENT: DIAGNOSTIC HUD ---
@Composable
fun DiagnosticHud(counts: Map<SystemStatus, Int>) {
    val total = counts.values.sum().toFloat()
    
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            "SYSTEM INTEGRITY MONITOR",
            style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                color = Color.Gray,
                letterSpacing = 1.sp
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(Color(0xFF161B22), RoundedCornerShape(4.dp))
                .clip(RoundedCornerShape(4.dp))
        ) {
            SystemStatus.values().forEach { status ->
                val count = counts[status] ?: 0
                if (count > 0) {
                    val weight = count / total
                    Box(
                        modifier = Modifier
                            .weight(weight)
                            .fillMaxHeight()
                            .background(status.color)
                    )
                }
            }
        }
    }
}

// --- COMPONENT: FILTER CHIPS ---
@Composable
fun FilterChips(selectedFilter: SystemStatus?, onSelect: (SystemStatus?) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // "ALL" Chip
        FilterChip(
            selected = selectedFilter == null,
            onClick = { onSelect(null) },
            label = { Text("ALL", fontFamily = FontFamily.Monospace) },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = Color(0xFF00E5FF).copy(alpha = 0.2f),
                selectedLabelColor = Color(0xFF00E5FF),
                containerColor = Color(0xFF161B22),
                labelColor = Color.Gray
            ),
            border = FilterChipDefaults.filterChipBorder(
                enabled = true,
                selected = selectedFilter == null,
                borderColor = if (selectedFilter == null) Color(0xFF00E5FF) else Color(0xFF30363D)
            )
        )

        SystemStatus.values().forEach { status ->
            FilterChip(
                selected = selectedFilter == status,
                onClick = { onSelect(if (selectedFilter == status) null else status) },
                label = { Text(status.label, fontFamily = FontFamily.Monospace) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = status.color.copy(alpha = 0.2f),
                    selectedLabelColor = status.color,
                    containerColor = Color(0xFF161B22),
                    labelColor = Color.Gray
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = selectedFilter == status,
                    borderColor = if (selectedFilter == status) status.color else Color(0xFF30363D)
                )
            )
        }
    }
}

// --- COMPONENT: SWIPE TO DELETE ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeToDeleteContainer(
    item: Any,
    onDelete: () -> Unit,
    content: @Composable () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            if (it == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
                true
            } else false
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            val color = if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) Color(0xFFFF5252) else Color.Transparent
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color, RoundedCornerShape(4.dp))
                    .padding(end = 16.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Rounded.Delete,
                    contentDescription = "Purge",
                    tint = Color.White
                )
            }
        },
        content = { content() }
    )
}

// --- COMPONENT: THE LOG CARD ---
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LogEntryCard(entry: JournalEntry) {
    val dateFormatter = SimpleDateFormat("yyyy-MM-dd // HH:mm", Locale.getDefault())
    
    Card(
        shape = RoundedCornerShape(4.dp), // Squared "Terminal" look
        colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22)),
        border = BorderStroke(1.dp, entry.status.color.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Date + Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = dateFormatter.format(Date(entry.timestamp)),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        color = Color.Gray
                    )
                )
                
                // Status Badge
                Surface(
                    color = entry.status.color.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(4.dp),
                    border = BorderStroke(1.dp, entry.status.color.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = entry.status.label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = entry.status.color
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Content
            Text(
                text = entry.content,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.White,
                    lineHeight = 22.sp
                )
            )

            // OBJECTIVE 3: Causal Variable Correlation (Tags)
            if (entry.triggers.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (tag in entry.triggers) {
                        Surface(
                            shape = RoundedCornerShape(2.dp),
                            color = Color(0xFF0D1117),
                            border = BorderStroke(1.dp, Color(0xFF30363D))
                        ) {
                            Text(
                                text = "#$tag",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    color = Color(0xFF8B949E),
                                    fontSize = 10.sp
                                ),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// --- COMPONENT: INPUT DIALOG ---
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddLogDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, SystemStatus, List<String>) -> Unit
) {
    var text by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf(SystemStatus.NOMINAL) }
    var selectedTags by remember { mutableStateOf(emptySet<String>()) }
    
    val availableTags = listOf("STRESS", "SOCIAL", "BOREDOM", "CRAVING", "WORK", "FATIGUE", "HAPPINESS")

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF0D1117),
        title = {
            Text(
                "NEW ENTRY",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
        },
        text = {
            Column {
                // Status Selector
                Text("CURRENT STATUS:", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    SystemStatus.values().forEach { status ->
                        val isSelected = status == selectedStatus
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .background(
                                    if (isSelected) status.color else status.color.copy(alpha = 0.2f),
                                    RoundedCornerShape(50)
                                )
                                .clickable { selectedStatus = status }
                        )
                    }
                }
                Text(
                    text = selectedStatus.label,
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = selectedStatus.color, 
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    ),
                    modifier = Modifier.padding(top = 8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // OBJECTIVE 3: EXTERNAL VARIABLES (Tags)
                Text("EXTERNAL VARIABLES:", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (tag in availableTags) {
                        val isSelected = selectedTags.contains(tag)
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedTags = if (isSelected) selectedTags - tag else selectedTags + tag
                            },
                            label = { Text(tag, fontFamily = FontFamily.Monospace, fontSize = 10.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF00E5FF).copy(alpha = 0.2f),
                                selectedLabelColor = Color(0xFF00E5FF),
                                containerColor = Color(0xFF161B22),
                                labelColor = Color.Gray
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = if (isSelected) Color(0xFF00E5FF) else Color(0xFF30363D)
                            ),
                            modifier = Modifier.height(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Text Input
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    placeholder = { Text("Log observation...", color = Color.Gray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF00E5FF),
                        unfocusedBorderColor = Color(0xFF30363D),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth().height(120.dp)
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { if (text.isNotBlank()) onConfirm(text, selectedStatus, selectedTags.toList()) }
            ) {
                Text("SAVE RECORD", color = Color(0xFF00E5FF))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL", color = Color.Gray)
            }
        }
    )
}
