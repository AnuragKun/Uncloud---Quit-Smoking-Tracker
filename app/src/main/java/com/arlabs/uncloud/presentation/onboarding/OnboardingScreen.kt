package com.arlabs.uncloud.presentation.onboarding

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.arlabs.uncloud.presentation.components.CustomNumericKeyboard
import com.arlabs.uncloud.presentation.settings.CurrencySelectionDialog
import kotlinx.coroutines.delay
import java.time.LocalTime
import java.time.format.DateTimeFormatter

// --- THEME COLORS ---
private val SysCyan = Color(0xFF00E5FF)
private val SysDark = Color(0xFF0D1117)
private val SysPanel = Color(0xFF161B22)
private val SysBorder = Color(0xFF30363D)
private val SysGreen = Color(0xFF00FF9D)

@Composable
fun OnboardingScreen(
        onNavigateToHome: () -> Unit,
        viewModel: OnboardingViewModel = hiltViewModel()
) {
        val state by viewModel.uiState.collectAsState()

        LaunchedEffect(true) {
                viewModel.navigationEvent.collect { _ -> onNavigateToHome() }
        }

        OnboardingContent(state = state, onEvent = viewModel::onEvent)
}

@Composable
fun OnboardingContent(state: OnboardingUiState, onEvent: (OnboardingEvent) -> Unit) {
        // Cyberpunk Gradient Background
        val backgroundBrush = Brush.verticalGradient(
                colors = listOf(SysDark, Color.Black)
        )

        Scaffold(
                containerColor = Color.Transparent,
                topBar = {
                        // TOP BAR: Navigation & Progress
                        Column(modifier = Modifier.fillMaxWidth()) {
                                // 1. Back Button (Only if past Pledge screen)
                                if (state.currentStep > 0) {
                                        Box(modifier = Modifier.padding(top = 16.dp, start = 8.dp)) {
                                                IconButton(onClick = { onEvent(OnboardingEvent.PreviousStep) }) {
                                                        Icon(
                                                                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                                                contentDescription = "Back",
                                                                tint = Color.Gray
                                                        )
                                                }
                                        }
                                }

                                // 2. Segmented "Boot Loader" Progress Bar (Top)
                                if (state.currentStep > 0) {
                                        Row(
                                                modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(horizontal = 24.dp, vertical = 8.dp),
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                                repeat(6) { index -> // Steps 1 to 6
                                                        // Offset by 1 because Step 0 is Pledge
                                                        val visualIndex = index + 1
                                                        val isActive = visualIndex <= state.currentStep
                                                        Box(
                                                                modifier = Modifier
                                                                        .weight(1f)
                                                                        .height(4.dp)
                                                                        .clip(RoundedCornerShape(2.dp))
                                                                        .background(
                                                                                if (isActive) SysCyan else SysBorder
                                                                        )
                                                        )
                                                }
                                        }
                                }
                        }
                }
        ) { padding ->
                Box(
                        modifier = Modifier
                                .fillMaxSize()
                                .background(backgroundBrush)
                                .background(backgroundBrush)
                                // .padding(padding)
                ) {
                        Column(
                                modifier = Modifier
                                        .fillMaxSize()
                                        .padding(padding)
                                        .padding(horizontal = 24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                                // Spacer(modifier = Modifier.weight(0.3f)) <-- REMOVED to fix layout bug

                                // --- ANIMATED CONTENT AREA ---
                                // Using weight(1f) ensures this takes up all remaining space,
                                // allowing PledgeScreen to be full height.
                                AnimatedContent(
                                        targetState = state.currentStep,
                                        transitionSpec = {
                                                if (targetState > initialState) {
                                                        (slideInHorizontally { width -> width } + fadeIn())
                                                                .togetherWith(slideOutHorizontally { width -> -width } + fadeOut())
                                                } else {
                                                        (slideInHorizontally { width -> -width } + fadeIn())
                                                                .togetherWith(slideOutHorizontally { width -> width } + fadeOut())
                                                }
                                                        .using(SizeTransform(clip = false))
                                        },
                                        label = "OnboardingStep",
                                        modifier = Modifier.weight(1f)
                                ) { step ->
                                        // Wrap step content. For Step 0 (Pledge), we let it fill.
                                        // For others, we center vertically.
                                        Box(
                                                modifier = Modifier.fillMaxSize(),
                                                contentAlignment = if(step == 0) Alignment.TopCenter else Alignment.Center
                                        ) {
                                                when (step) {
                                                        0 -> PledgeScreen { onEvent(OnboardingEvent.NextStep) }
                                                        1 -> QuitDateStep(state, onEvent)
                                                        2 -> CigarettesPerDayStep(state)
                                                        3 -> CostPerPackStep(state, onEvent)
                                                        4 -> CigarettesInPackStep(state)
                                                        5 -> MinutesPerCigaretteStep(state)
                                                        6 -> ProjectionStep(state)
                                                }
                                        }
                                }

                                // --- CONTROLS AREA (Keyboard / Buttons) ---
                                // Only show these for Steps > 0. Step 0 has button inside PledgeScreen.
                                if (state.currentStep > 0) {
                                        Column(
                                                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                                // Numeric Keyboard (Steps 2-5)
                                                if (state.currentStep in 2..5) {
                                                        CustomNumericKeyboard(
                                                                onNumberClick = { onEvent(OnboardingEvent.AppendInput(it)) },
                                                                onBackspaceClick = { onEvent(OnboardingEvent.BackspaceInput) }
                                                        )
                                                        Spacer(modifier = Modifier.height(24.dp))

                                                        PrimaryActionButton(
                                                                text = "CONFIRM PARAMETER",
                                                                onClick = { onEvent(OnboardingEvent.NextStep) }
                                                        )
                                                }
                                                // Date Step Button
                                                else if (state.currentStep == 1) {
                                                        PrimaryActionButton(
                                                                text = "INITIALIZE PROTOCOL",
                                                                onClick = { onEvent(OnboardingEvent.NextStep) }
                                                        )
                                                }
                                                // Final Projection Step Button
                                                else if (state.currentStep == 6) {
                                                        val permissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
                                                                contract = androidx.activity.result.contract.ActivityResultContracts.RequestPermission(),
                                                                onResult = { onEvent(OnboardingEvent.Submit) }
                                                        )

                                                        PrimaryActionButton(
                                                                text = "EXECUTE SYSTEM",
                                                                onClick = {
                                                                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                                                                                permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                                                                        } else {
                                                                                onEvent(OnboardingEvent.Submit)
                                                                        }
                                                                }
                                                        )
                                                }
                                        }
                                }
                        }
                }
        }
}

// ==========================================
//        HELPER UI COMPONENTS
// ==========================================

@Composable
fun PrimaryActionButton(text: String, onClick: () -> Unit) {
        Button(
                onClick = onClick,
                modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SysCyan),
                shape = RoundedCornerShape(4.dp)
        ) {
                Text(
                        text = text,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = Color.Black,
                        letterSpacing = 1.sp
                )
        }
}

@Composable
fun SystemInputDisplay(value: String, prefix: String = "", suffix: String = "") {
        // Blinking Cursor Logic
        var cursorVisible by remember { mutableStateOf(true) }
        LaunchedEffect(Unit) {
                while (true) {
                        delay(500)
                        cursorVisible = !cursorVisible
                }
        }

        Box(
                modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .background(SysPanel, RoundedCornerShape(12.dp))
                        .border(1.dp, SysBorder, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
        ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                        if (prefix.isNotEmpty()) {
                                Text(
                                        text = prefix,
                                        style = MaterialTheme.typography.headlineSmall,
                                        color = Color.Gray,
                                        fontFamily = FontFamily.Monospace
                                )
                        }

                        // The Value
                        Text(
                                text = value.ifEmpty { "0" },
                                style = MaterialTheme.typography.displayMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        color = SysCyan
                                )
                        )

                        // Blinking Cursor
                        if (cursorVisible) {
                                Box(
                                        modifier = Modifier
                                                .padding(start = 4.dp)
                                                .width(4.dp)
                                                .height(32.dp)
                                                .background(SysCyan)
                                )
                        }

                        if (suffix.isNotEmpty()) {
                                Text(
                                        text = suffix,
                                        style = MaterialTheme.typography.headlineSmall,
                                        color = Color.Gray,
                                        fontFamily = FontFamily.Monospace,
                                        modifier = Modifier.padding(start = 8.dp)
                                )
                        }
                }
        }
}

@Composable
fun SystemQuestionHeader(text: String) {
        Text(
                text = text.uppercase(),
                style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp,
                        lineHeight = 32.sp
                ),
                color = Color.White,
                textAlign = TextAlign.Center
        )
}

@Composable
fun ProjectionCard(iconRes: Int, value: String, label: String, color: Color) {
        Card(
                colors = CardDefaults.cardColors(containerColor = SysPanel),
                border = BorderStroke(1.dp, SysBorder),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.size(150.dp)
        ) {
                Column(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                ) {
                        Icon(
                                painter = painterResource(id = iconRes),
                                contentDescription = null,
                                tint = color,
                                modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                                text = value,
                                style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        color = Color.White
                                )
                        )
                        Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color.Gray,
                                        letterSpacing = 1.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                ),
                                textAlign = TextAlign.Center
                        )
                }
        }
}

// ==========================================
//        STEP COMPOSABLES
// ==========================================

// --- STEP 1: QUIT DATE ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuitDateStep(state: OnboardingUiState, onEvent: (OnboardingEvent) -> Unit) {
        var showDatePicker by remember { mutableStateOf(false) }
        var showTimePicker by remember { mutableStateOf(false) }

        val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = state.quitDate.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli(),
                selectableDates = object : SelectableDates {
                        override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                                val selectedDate = java.time.Instant.ofEpochMilli(utcTimeMillis)
                                        .atZone(java.time.ZoneId.of("UTC"))
                                        .toLocalDate()
                                val today = java.time.LocalDate.now()
                                return !selectedDate.isAfter(today) // Allow today and past
                        }

                        override fun isSelectableYear(year: Int): Boolean {
                                return year <= java.time.LocalDate.now().year
                        }
                }
        )
        val timePickerState = rememberTimePickerState(
                initialHour = state.quitTime.hour,
                initialMinute = state.quitTime.minute
        )

        if (showDatePicker) {
                DatePickerDialog(
                        onDismissRequest = { showDatePicker = false },
                        confirmButton = {
                                TextButton(onClick = {
                                        datePickerState.selectedDateMillis?.let { millis ->
                                                val date = java.time.Instant.ofEpochMilli(millis)
                                                        .atZone(java.time.ZoneId.systemDefault()).toLocalDate()
                                                onEvent(OnboardingEvent.QuitDateChanged(date))
                                        }
                                        showDatePicker = false
                                }) { Text("CONFIRM", color = SysCyan) }
                        },
                        colors = DatePickerDefaults.colors(containerColor = SysDark)
                ) { DatePicker(state = datePickerState) }
        }

        if (showTimePicker) {
                androidx.compose.ui.window.Dialog(onDismissRequest = { showTimePicker = false }) {
                        Surface(shape = RoundedCornerShape(16.dp), color = SysDark) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
                                        TimePicker(state = timePickerState, colors = TimePickerDefaults.colors(
                                                clockDialColor = SysPanel,
                                                selectorColor = SysCyan,
                                                timeSelectorSelectedContainerColor = SysPanel,
                                                timeSelectorSelectedContentColor = SysCyan
                                        ))
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                                TextButton(onClick = { showTimePicker = false }) { Text("CANCEL", color = Color.Gray) }
                                                TextButton(onClick = {
                                                        onEvent(OnboardingEvent.QuitTimeChanged(LocalTime.of(timePickerState.hour, timePickerState.minute)))
                                                        showTimePicker = false
                                                }) { Text("CONFIRM", color = SysCyan) }
                                        }
                                }
                        }
                }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                SystemQuestionHeader("When was your\nlast cigarette?")

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                        text = "Establish timeline entry point",
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray, fontFamily = FontFamily.Monospace),
                )

                Spacer(modifier = Modifier.height(48.dp))

                // Digital Clock Display Panel
                Column(
                        modifier = Modifier
                                .fillMaxWidth()
                                .background(SysPanel, RoundedCornerShape(16.dp))
                                .border(1.dp, SysBorder, RoundedCornerShape(16.dp))
                                .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                ) {
                        val dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
                        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

                        // Date Row
                        Row(
                                modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { showDatePicker = true },
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                        ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Rounded.CalendarToday, null, tint = SysCyan, modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text("DATE", color = Color.Gray, fontFamily = FontFamily.Monospace)
                                }
                                Text(
                                        state.quitDate.format(dateFormatter).uppercase(),
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 18.sp
                                )
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = SysBorder)

                        // Time Row
                        Row(
                                modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { showTimePicker = true },
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                        ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Rounded.Schedule, null, tint = SysCyan, modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text("TIME", color = Color.Gray, fontFamily = FontFamily.Monospace)
                                }
                                Text(
                                        state.quitTime.format(timeFormatter),
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 18.sp
                                )
                        }
                }
        }
}

// --- STEP 2: CIGARETTES PER DAY ---
@Composable
fun CigarettesPerDayStep(state: OnboardingUiState) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                SystemQuestionHeader("Daily intake\nvolume?")
                Spacer(modifier = Modifier.height(48.dp))
                SystemInputDisplay(value = state.cigarettesPerDay, suffix = "CIGS")
        }
}

// --- STEP 3: COST PER PACK ---
@Composable
fun CostPerPackStep(state: OnboardingUiState, onEvent: (OnboardingEvent) -> Unit) {
        var showCurrencyDialog by remember { mutableStateOf(false) }

        if (showCurrencyDialog) {
                CurrencySelectionDialog(
                        onDismiss = { showCurrencyDialog = false },
                        onCurrencySelected = { currency ->
                                onEvent(OnboardingEvent.CurrencyChanged(currency.symbol))
                                showCurrencyDialog = false
                        }
                )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                SystemQuestionHeader("Cost per\nunit pack?")
                Spacer(modifier = Modifier.height(48.dp))

                // Custom Input with clickable currency box inside
                Box(
                        modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .background(SysPanel, RoundedCornerShape(12.dp))
                                .border(1.dp, SysBorder, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                                // Currency Selector Button
                                Box(
                                        modifier = Modifier
                                                .padding(start = 16.dp)
                                                .clickable { showCurrencyDialog = true }
                                                .background(SysDark, RoundedCornerShape(8.dp))
                                                .border(1.dp, SysBorder, RoundedCornerShape(8.dp))
                                                .padding(horizontal = 12.dp, vertical = 8.dp)
                                ) {
                                        Text(
                                                text = state.currency,
                                                color = SysCyan,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 24.sp,
                                                fontFamily = FontFamily.Monospace
                                        )
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                Text(
                                        text = state.costPerPack.ifEmpty { "0" },
                                        style = MaterialTheme.typography.displayMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontFamily = FontFamily.Monospace,
                                                color = Color.White
                                        )
                                )
                        }
                }
        }
}

// --- STEP 4: CIGARETTES IN PACK ---
@Composable
fun CigarettesInPackStep(state: OnboardingUiState) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                SystemQuestionHeader("Pack capacity?")
                Spacer(modifier = Modifier.height(48.dp))
                SystemInputDisplay(value = state.cigarettesInPack, suffix = "/ PACK")
        }
}

// --- STEP 5: MINUTES PER CIGARETTE ---
@Composable
fun MinutesPerCigaretteStep(state: OnboardingUiState) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                SystemQuestionHeader("Time spent per\ncigarette?")
                Spacer(modifier = Modifier.height(48.dp))
                SystemInputDisplay(value = state.minutesPerCigarette, suffix = "MIN")
        }
}

// --- STEP 6: PROJECTION ---
@Composable
fun ProjectionStep(state: OnboardingUiState) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                SystemQuestionHeader("Projected\nResults")

                Text(
                        text = "Based on 30-day extrapolation",
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray, fontFamily = FontFamily.Monospace),
                        modifier = Modifier.padding(top = 8.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        // First card: Cigarettes avoided
                        ProjectionCard(
                                iconRes = R.drawable.icon_flame,
                                value = state.projectedCigarettesAvoided.toString(),
                                label = "AVOIDED",
                                color = Color(0xFFE57373)
                        )
                        // Second card: Money saved
                        ProjectionCard(
                                iconRes = R.drawable.icon_money,
                                value = "${state.currency}${String.format("%.0f", state.projectedMoneySaved)}",
                                label = "SAVED",
                                color = SysGreen
                        )
                }

                Spacer(modifier = Modifier.height(48.dp))

                // Final Status Block
                Box(
                        modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, SysCyan.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                .background(SysCyan.copy(alpha = 0.05f), RoundedCornerShape(8.dp))
                                .padding(16.dp)
                ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                        text = "SYSTEM STATUS",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                                color = SysCyan,
                                                letterSpacing = 2.sp,
                                                fontWeight = FontWeight.Bold,
                                                fontFamily = FontFamily.Monospace
                                        )
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                        text = "Ready to initiate biological recovery protocols.",
                                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.LightGray),
                                        textAlign = TextAlign.Center
                                )
                        }
                }
        }
}