package com.arlabs.uncloud.presentation.journal

import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.Canvas
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.geometry.Offset
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.material.icons.rounded.Warning
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.text.SimpleDateFormat
import androidx.compose.foundation.lazy.rememberLazyListState
import com.arlabs.uncloud.data.local.entity.JournalEntry
import com.arlabs.uncloud.data.local.entity.SystemStatus
import com.arlabs.uncloud.presentation.journal.model.RiskLevel
import com.arlabs.uncloud.presentation.journal.model.ThreatAnalysis
import com.arlabs.uncloud.domain.model.Breach


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
        colors = listOf(MaterialTheme.colorScheme.background, Color(0xFF000000))
    )

    val currentFilter by viewModel.currentFilter.collectAsState()
    val threatAnalysis by viewModel.threatAnalysis.collectAsState()

    var showHelpDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val listState = rememberLazyListState()
    
    // Delete Confirmation State
    var entryToDelete by remember { mutableStateOf<JournalEntry?>(null) }

    // Auto-scroll to top when new entry is added
    LaunchedEffect(entries.size) {
        if (entries.isNotEmpty()) {
            listState.animateScrollToItem(0)
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.Black
            ) {
                Icon(Icons.Rounded.Add, contentDescription = "New Log")
            }
        },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "GLITCH LOGGER",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp,
                            color = MaterialTheme.colorScheme.primary
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
                .padding(top = padding.calculateTopPadding()) // Fix: Only apply top padding to container
            ) {
                // OBJECTIVE 2: Diagnostic HUD (Visual Header)
                // Persistent: Show if there is ANY data in the system, even if filtered out
                if (threatAnalysis.entryCountLast7Days > 0) {
                    ThreatMatrix(threatAnalysis)
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
                        state = listState,
                        contentPadding = PaddingValues(
                            bottom = padding.calculateBottomPadding() + 80.dp, // Fix: Handle bottom padding here
                            start = 16.dp, 
                            end = 16.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(entries, key = { it.id }) { entry ->
                            SwipeToDeleteContainer(
                                item = entry,
                                onDelete = { entryToDelete = it } // Set entryToDelete instead of immediate deletion
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
            onConfirm = { content, status, triggers, timestamp ->
                viewModel.addEntry(content, status, triggers, timestamp)
                showAddDialog = false
            }
        )
    }

    if (showHelpDialog) {
        AlertDialog(
            onDismissRequest = { showHelpDialog = false },
            containerColor = MaterialTheme.colorScheme.background,
            title = {
                Text(
                    "GLITCH LOGGER PROTOCOL",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text(
                        "PROTOCOL: Use (+) to Log. Tags improve accuracy. Matrix updates in real-time.",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        "The Threat Matrix analyzes your logs to predict behavior patterns.",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Text("DEFCON (RISK LEVEL)", style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.tertiary, fontWeight = FontWeight.Bold))
                    Text("Real-time analysis of your stability based on log volatility. Keep this Low.", style = MaterialTheme.typography.bodySmall.copy(color = Color.White))
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text("HOSTILES (TOP TRIGGERS)", style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.tertiary, fontWeight = FontWeight.Bold))
                    Text("The 3 most frequent variables compromising your system. Know your enemy.", style = MaterialTheme.typography.bodySmall.copy(color = Color.White))

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("HIGH RISK TIME", style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.tertiary, fontWeight = FontWeight.Bold))
                    Text("The 4-hour temporal window where you are statistically most vulnerable.", style = MaterialTheme.typography.bodySmall.copy(color = Color.White))
                }
            },
            confirmButton = {
                TextButton(onClick = { showHelpDialog = false }) {
                    Text("ACKNOWLEDGE", color = MaterialTheme.colorScheme.primary)
                }
            }
        )
    }
    
    // DELETE CONFIRMATION DIALOG
    if (entryToDelete != null) {
        AlertDialog(
            onDismissRequest = { entryToDelete = null },
            containerColor = MaterialTheme.colorScheme.background,
            title = {
                Text("DELETE LOG?", style = MaterialTheme.typography.titleMedium.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error))
            },
            text = {
                Text(
                    "This record will be permanently purged from the system. This action cannot be undone.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        entryToDelete?.let { viewModel.deleteEntry(it) }
                        entryToDelete = null
                    }
                ) {
                    Text("PURGE", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { entryToDelete = null }) {
                    Text("CANCEL", color = Color.Gray)
                }
            }
        )
    }
}

// --- EXTENSIONS ---
@Composable
fun SystemStatus.themeColor(): Color = when (this) {
    SystemStatus.NOMINAL -> MaterialTheme.colorScheme.secondary
    SystemStatus.STABLE -> MaterialTheme.colorScheme.primary
    SystemStatus.VOLATILE -> MaterialTheme.colorScheme.tertiary
    SystemStatus.CRITICAL -> MaterialTheme.colorScheme.error
}

// --- COMPONENT: THREAT MATRIX (HEATMAP TIMELINE) ---
// --- COMPONENT: THREAT MATRIX (HOLOGRAPHIC CARDS) ---
@Composable
fun ThreatMatrix(analysis: ThreatAnalysis) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // HEADER
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "THREAT MATRIX",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
            )
        }

        
        Spacer(modifier = Modifier.height(12.dp))

        // ROW 1: RISK CARD (Full Width)
        val riskColor = when (analysis.riskLevel) {
            RiskLevel.CRITICAL -> MaterialTheme.colorScheme.error
            RiskLevel.MODERATE -> MaterialTheme.colorScheme.tertiary
            RiskLevel.LOW -> MaterialTheme.colorScheme.secondary
            RiskLevel.UNKNOWN -> Color.Gray
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = riskColor.copy(alpha = 0.1f)),
            border = BorderStroke(1.dp, riskColor.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize()
                .clickable { expanded = !expanded }
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            "CURRENT DEFCON",
                            style = MaterialTheme.typography.labelSmall.copy(color = riskColor.copy(alpha = 0.7f), fontFamily = FontFamily.Monospace)
                        )
                        Text(
                            text = analysis.riskLevel.name,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                color = riskColor,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 4.sp
                            )
                        )
                    }
                    Icon(
                        imageVector = Icons.Rounded.Warning, // Or similar warning icon
                        contentDescription = null,
                        tint = riskColor,
                        modifier = Modifier.size(32.dp)
                    )
                }
                
                // EXPANDABLE GRAPH
                AnimatedVisibility(visible = expanded) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "7-DAY STABILITY PROJECTION",
                            style = MaterialTheme.typography.labelSmall.copy(color = riskColor.copy(alpha = 0.7f), fontFamily = FontFamily.Monospace)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        SparklineGraph(analysis.riskTrend, riskColor)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // ROW 2: ENEMY + TIME (Split)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // ENEMY CARD
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f).height(100.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "PRIMARY TRIGGERS",
                        style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (analysis.topTriggers.isNotEmpty()) {
                            analysis.topTriggers.joinToString("\n")
                        } else "NONE",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            fontSize = 14.sp
                        )
                    )
                }
            }

            // TIME CARD
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f).height(100.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(12.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "HIGH RISK TIME",
                        style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = analysis.peakRiskHour ?: "ANALYZING",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
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
                selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                selectedLabelColor = MaterialTheme.colorScheme.primary,
                containerColor = MaterialTheme.colorScheme.surface,
                labelColor = Color.Gray
            ),
            border = FilterChipDefaults.filterChipBorder(
                enabled = true,
                selected = selectedFilter == null,
                borderColor = if (selectedFilter == null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
            )
        )

        SystemStatus.values().forEach { status ->
            FilterChip(
                selected = selectedFilter == status,
                onClick = { onSelect(if (selectedFilter == status) null else status) },
                label = { Text(status.label, fontFamily = FontFamily.Monospace) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = status.themeColor().copy(alpha = 0.2f),
                    selectedLabelColor = status.themeColor(),
                    containerColor = MaterialTheme.colorScheme.surface,
                    labelColor = Color.Gray
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = selectedFilter == status,
                    borderColor = if (selectedFilter == status) status.themeColor() else MaterialTheme.colorScheme.outline
                )
            )
        }
    }
}

// --- COMPONENT: SWIPE TO DELETE ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeToDeleteContainer(
    item: JournalEntry, // Changed type to JournalEntry for onDelete lambda
    onDelete: (JournalEntry) -> Unit, // Changed onDelete to accept JournalEntry
    content: @Composable () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            if (it == SwipeToDismissBoxValue.EndToStart) {
                onDelete(item) // Pass the item to onDelete
                true
            } else false
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            val color = if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) MaterialTheme.colorScheme.error else Color.Transparent
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
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, entry.status.themeColor().copy(alpha = 0.3f)),
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
                    color = entry.status.themeColor().copy(alpha = 0.1f),
                    shape = RoundedCornerShape(4.dp),
                    border = BorderStroke(1.dp, entry.status.themeColor().copy(alpha = 0.5f))
                ) {
                    Text(
                        text = entry.status.label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = entry.status.themeColor()
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
                            color = MaterialTheme.colorScheme.background,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                        ) {
                            Text(
                                text = "#$tag",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
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
@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AddLogDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, SystemStatus, List<String>, Long) -> Unit
) {
    var text by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf(SystemStatus.NOMINAL) }
    var selectedTags by remember { mutableStateOf(emptySet<String>()) }
    var showValidationError by remember { mutableStateOf(false) }
    
    // Time/Date Picker State
    var logTime by remember { mutableStateOf(Calendar.getInstance()) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    
    val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())
    val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    val context = LocalContext.current

    // Deduped and Organized Tags
    val availableTags = listOf(
        // Situational
        "WORK", "SOCIAL", "DRIVING", "ALCOHOL", "COFFEE", "AFTER_MEAL", "WAKING_UP", "WORK_BREAK",
        // Emotional/Internal
        "STRESS", "ANXIETY", "ANGER", "BOREDOM", "SADNESS", "HAPPINESS", "LONELINESS", "FATIGUE",
        // Physical
        "HUNGER", "PAIN", "URGE", "CRAVING"
    ).distinct()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            shape = RoundedCornerShape(4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .wrapContentHeight() // Allow height to adapt
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp) // Internal padding matching CombinedDateTimeDialog style
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // TITLE
                Text(
                    "NEW ENTRY",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 2.sp
                    ),
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                // SCROLLABLE CONTENT
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false) // Allow scrolling but don't force full height
                        .verticalScroll(rememberScrollState())
                ) {
                    // DATE & TIME ROW
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // DATE SELECTOR
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.5f))
                                .clickable { showDatePicker = true }
                                .padding(12.dp)
                        ) {
                            Column {
                                Text("DATE", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f), fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                                Text(
                                    dateFormatter.format(logTime.time).uppercase(),
                                    style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace, color = MaterialTheme.colorScheme.onSurface)
                                )
                            }
                        }
                        
                        // TIME SELECTOR
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.5f))
                                .clickable { showTimePicker = true }
                                .padding(12.dp)
                        ) {
                            Column {
                                Text("TIME", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f), fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                                Text(
                                    timeFormatter.format(logTime.time),
                                    style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace, color = MaterialTheme.colorScheme.onSurface)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // STATUS SELECTOR (Refactored to List/Grid)
                    Text("SYSTEM STATUS:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f), fontFamily = FontFamily.Monospace)
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SystemStatus.values().forEach { status ->
                            val isSelected = selectedStatus == status
                            val color = status.themeColor()
                            
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = if (isSelected) color.copy(alpha = 0.2f) else Color.Transparent,
                                border = BorderStroke(1.dp, if (isSelected) color else MaterialTheme.colorScheme.primary.copy(alpha = 0.7f).copy(alpha = 0.3f)),
                                modifier = Modifier.clickable { selectedStatus = status }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // Dot
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .background(color, androidx.compose.foundation.shape.CircleShape)
                                    )
                                    // Name
                                    Text(
                                        text = status.label,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) color else MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                                        )
                                    )
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))

                    // EXTERNAL VARIABLES (Tags) - Deduped and Split
                    Text(
                        "EXTERNAL VARIABLES:", 
                        style = MaterialTheme.typography.labelSmall, 
                        fontFamily = FontFamily.Monospace,
                        color = if (showValidationError && selectedTags.isEmpty()) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Horizontal scrolling rows for density
                    val mid = (availableTags.size + 1) / 2
                    val row1 = availableTags.take(mid)
                    val row2 = availableTags.drop(mid)
                    
                    Column {
                        Row(
                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            for (tag in row1) {
                                val isSelected = selectedTags.contains(tag)
                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        selectedTags = if (isSelected) selectedTags - tag else selectedTags + tag
                                        if (selectedTags.isNotEmpty()) showValidationError = false
                                    },
                                    label = { Text(tag, fontFamily = FontFamily.Monospace, fontSize = 10.sp) },
                                    shape = RoundedCornerShape(4.dp), // Tech shape
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                        selectedLabelColor = MaterialTheme.colorScheme.primary,
                                        selectedLeadingIconColor = MaterialTheme.colorScheme.primary,
                                        containerColor = Color.Transparent,
                                        labelColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(
                                        enabled = true,
                                        selected = isSelected,
                                        borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f).copy(alpha = 0.3f),
                                        selectedBorderColor = MaterialTheme.colorScheme.primary,
                                        borderWidth = 1.dp,
                                        selectedBorderWidth = 1.dp
                                    )
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            for (tag in row2) {
                                val isSelected = selectedTags.contains(tag)
                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        selectedTags = if (isSelected) selectedTags - tag else selectedTags + tag
                                        if (selectedTags.isNotEmpty()) showValidationError = false
                                    },
                                    label = { Text(tag, fontFamily = FontFamily.Monospace, fontSize = 10.sp) },
                                    shape = RoundedCornerShape(4.dp),
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                        selectedLabelColor = MaterialTheme.colorScheme.primary,
                                        selectedLeadingIconColor = MaterialTheme.colorScheme.primary,
                                        containerColor = Color.Transparent,
                                        labelColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(
                                        enabled = true,
                                        selected = isSelected,
                                        borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f).copy(alpha = 0.3f),
                                        selectedBorderColor = MaterialTheme.colorScheme.primary
                                    )
                                )
                            }
                        }
                    }
                    
                    if (showValidationError && selectedTags.isEmpty()) {
                        Text(
                            "REQUIRED: IDENTIFY AT LEAST ONE TRIGGER VARIABLE.",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.error, 
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            ),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // OBSERVATIONS (Note)
                    OutlinedTextField(
                        value = text,
                        onValueChange = { text = it },
                        label = { Text("OBSERVATIONS (OPTIONAL)", fontFamily = FontFamily.Monospace, fontSize = 10.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                            focusedLabelColor = MaterialTheme.colorScheme.primary,
                            unfocusedLabelColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                            cursorColor = MaterialTheme.colorScheme.primary
                        ),
                        textStyle = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
                        maxLines = 3,
                        shape = RoundedCornerShape(4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // BUTTONS
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(
                            "CANCEL", 
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f), 
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { 
                            if (selectedTags.isEmpty()) {
                                showValidationError = true
                            } else {
                                onConfirm(text, selectedStatus, selectedTags.toList(), logTime.timeInMillis) 
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            "SAVE RECORD", 
                            color = MaterialTheme.colorScheme.onPrimary, 
                            fontFamily = FontFamily.Monospace, 
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }

    // UNIFIED TIME/DATE PICKER (Cyberpunk Theme)
    if (showTimePicker) {
        com.arlabs.uncloud.presentation.settings.components.CombinedDateTimeDialog(
            initialMillis = logTime.timeInMillis,
            initialTab = 1, // Start on Time
            onDismiss = { showTimePicker = false },
            onConfirm = { millis ->
                logTime.timeInMillis = millis
                showTimePicker = false
            }
        )
    }

    if (showDatePicker) {
        com.arlabs.uncloud.presentation.settings.components.CombinedDateTimeDialog(
            initialMillis = logTime.timeInMillis,
            initialTab = 0, // Start on Date
            onDismiss = { showDatePicker = false },
            onConfirm = { millis ->
                logTime.timeInMillis = millis
                showDatePicker = false
            }
        )
    }
}
// --- COMPONENT: SPARKLINE ---
@Composable
fun SparklineGraph(data: List<Int>, color: Color) {
    if (data.isEmpty()) {
        Text("No Data", color = Color.Gray, style = MaterialTheme.typography.labelSmall)
        return
    }

    val density = LocalDensity.current
    val strokeWidth = with(density) { 3.dp.toPx() }
    val pointRadius = with(density) { 4.dp.toPx() }
    val innerRadius = with(density) { 2.dp.toPx() }

    Canvas(modifier = Modifier.fillMaxWidth().height(100.dp)) {
        val maxVal = 5f // Max risk is 5
        val xStep = size.width / (data.size - 1).coerceAtLeast(1)
        val h = size.height
        
        val points = data.mapIndexed { index, value ->
            Offset(
                x = index * xStep,
                y = h - (value.coerceIn(0, 5) / maxVal * h)
            )
        }
        
        val path = Path().apply {
            if (points.isNotEmpty()) {
                moveTo(points.first().x, points.first().y)
                points.drop(1).forEach { lineTo(it.x, it.y) }
            }
        }
        
        // Draw Line
        drawPath(path, color, style = Stroke(width = strokeWidth))
        
        // Draw Points
        for (point in points) {
            drawCircle(color = color, radius = pointRadius, center = point)
            drawCircle(color = Color.Black, radius = innerRadius, center = point)
        }
    }
}
