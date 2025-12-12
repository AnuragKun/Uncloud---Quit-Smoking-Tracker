package com.arlabs.uncloud.presentation.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.arlabs.uncloud.presentation.components.CustomNumericKeyboard
import com.arlabs.uncloud.presentation.settings.CurrencySelectionDialog
import com.arlabs.uncloud.presentation.theme.QuitSmokingTheme
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import com.arlabs.uncloud.R

@Composable
fun OnboardingScreen(
        onNavigateToHome: () -> Unit,
        viewModel: OnboardingViewModel = hiltViewModel()
) {
        val state by viewModel.uiState.collectAsState()

        LaunchedEffect(true) { viewModel.navigationEvent.collect { _ -> onNavigateToHome() } }

        OnboardingContent(state = state, onEvent = viewModel::onEvent)
}

@Composable
fun OnboardingContent(state: OnboardingUiState, onEvent: (OnboardingEvent) -> Unit) {
        Scaffold(
                topBar = {
                        if (state.currentStep > 0) {
                                IconButton(onClick = { onEvent(OnboardingEvent.PreviousStep) }) {
                                        Icon(
                                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                                contentDescription = "Back",
                                                tint = Color.White
                                        )
                                }
                        }
                },
                containerColor = Color(0xFF0D1117) // Dark background per design
        ) { padding ->
                Column(
                        modifier =
                                Modifier.fillMaxSize().padding(padding).padding(horizontal = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                ) {
                        Spacer(modifier = Modifier.weight(1f))

                        AnimatedContent(
                                targetState = state.currentStep,
                                transitionSpec = {
                                        if (targetState > initialState) {
                                                        (slideInHorizontally { width -> width } +
                                                                        fadeIn())
                                                                .togetherWith(
                                                                        slideOutHorizontally { width
                                                                                ->
                                                                                -width
                                                                        } + fadeOut()
                                                                )
                                                } else {
                                                        (slideInHorizontally { width -> -width } +
                                                                        fadeIn())
                                                                .togetherWith(
                                                                        slideOutHorizontally { width
                                                                                ->
                                                                                width
                                                                        } + fadeOut()
                                                                )
                                                }
                                                .using(SizeTransform(clip = false))
                                },
                                label = "OnboardingStep"
                        ) { step ->
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

                        Spacer(modifier = Modifier.weight(1f))

                        // Navigation / Keyboard area
                        // Navigation / Keyboard area
                        if (state.currentStep in 2..5) {
                                CustomNumericKeyboard(
                                        onNumberClick = {
                                                onEvent(OnboardingEvent.AppendInput(it))
                                        },
                                        onBackspaceClick = {
                                                onEvent(OnboardingEvent.BackspaceInput)
                                        }
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                        onClick = { onEvent(OnboardingEvent.NextStep) },
                                        modifier = Modifier.fillMaxWidth().height(56.dp),
                                        colors =
                                                ButtonDefaults.buttonColors(
                                                        containerColor = Color(0xFF2E8B57)
                                                )
                                ) { Text("Next", fontSize = 18.sp, fontWeight = FontWeight.Bold) }
                        } else if (state.currentStep == 1) {
                                Button(
                                        onClick = { onEvent(OnboardingEvent.NextStep) },
                                        modifier = Modifier.fillMaxWidth().height(56.dp),
                                        colors =
                                                ButtonDefaults.buttonColors(
                                                        containerColor = Color(0xFF2E8B57)
                                                )
                                ) { Text("Next", fontSize = 18.sp, fontWeight = FontWeight.Bold) }
                        } else if (state.currentStep == 6) {
                                val permissionLauncher =
                                        androidx.activity.compose.rememberLauncherForActivityResult(
                                                contract =
                                                        androidx.activity.result.contract
                                                                .ActivityResultContracts
                                                                .RequestPermission(),
                                                onResult = { onEvent(OnboardingEvent.Submit) }
                                        )

                                Button(
                                        onClick = {
                                                if (android.os.Build.VERSION.SDK_INT >=
                                                                android.os.Build.VERSION_CODES
                                                                        .TIRAMISU
                                                ) {
                                                        permissionLauncher.launch(
                                                                android.Manifest.permission
                                                                        .POST_NOTIFICATIONS
                                                        )
                                                } else {
                                                        onEvent(OnboardingEvent.Submit)
                                                }
                                        },
                                        modifier = Modifier.fillMaxWidth().height(56.dp),
                                        colors =
                                                ButtonDefaults.buttonColors(
                                                        containerColor = Color(0xFF2E8B57)
                                                )
                                ) {
                                        Text(
                                                "Start My Journey",
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Bold
                                        )
                                }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Pagination dots
                        Row(
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                        ) {
                                repeat(7) { index ->
                                        Box(
                                                modifier =
                                                        Modifier.padding(horizontal = 4.dp)
                                                                .size(
                                                                        if (index ==
                                                                                        state.currentStep
                                                                        )
                                                                                10.dp
                                                                        else 6.dp
                                                                )
                                                                .background(
                                                                        color =
                                                                                if (index ==
                                                                                                state.currentStep
                                                                                )
                                                                                        Color.White
                                                                                else Color.Gray,
                                                                        shape =
                                                                                RoundedCornerShape(
                                                                                        50
                                                                                )
                                                                )
                                        )
                                }
                        }
                }
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuitDateStep(state: OnboardingUiState, onEvent: (OnboardingEvent) -> Unit) {
        var showDatePicker by remember { mutableStateOf(false) }
        var showTimePicker by remember { mutableStateOf(false) }

        // Date Picker
        val datePickerState =
                rememberDatePickerState(
                        initialSelectedDateMillis =
                                state.quitDate
                                        .atStartOfDay(java.time.ZoneId.systemDefault())
                                        .toInstant()
                                        .toEpochMilli()
                )

        // Time Picker
        val timePickerState =
                rememberTimePickerState(
                        initialHour = state.quitTime.hour,
                        initialMinute = state.quitTime.minute
                )

        if (showDatePicker) {
                DatePickerDialog(
                        onDismissRequest = { showDatePicker = false },
                        confirmButton = {
                                TextButton(
                                        onClick = {
                                                datePickerState.selectedDateMillis?.let { millis ->
                                                        val date =
                                                                java.time.Instant.ofEpochMilli(
                                                                                millis
                                                                        )
                                                                        .atZone(
                                                                                java.time.ZoneId
                                                                                        .systemDefault()
                                                                        )
                                                                        .toLocalDate()
                                                        onEvent(
                                                                OnboardingEvent.QuitDateChanged(
                                                                        date
                                                                )
                                                        )
                                                }
                                                showDatePicker = false
                                        }
                                ) { Text("OK") }
                        }
                ) { DatePicker(state = datePickerState) }
        }

        if (showTimePicker) {
                Dialog(onDismissRequest = { showTimePicker = false }) {
                        Surface(
                                shape = MaterialTheme.shapes.extraLarge,
                                tonalElevation = 6.dp,
                                color = MaterialTheme.colorScheme.surface
                        ) {
                                Column(
                                        modifier = Modifier.padding(24.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                        TimePicker(state = timePickerState)
                                        Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.End
                                        ) {
                                                TextButton(onClick = { showTimePicker = false }) {
                                                        Text("Cancel")
                                                }
                                                TextButton(
                                                        onClick = {
                                                                onEvent(
                                                                        OnboardingEvent
                                                                                .QuitTimeChanged(
                                                                                        LocalTime
                                                                                                .of(
                                                                                                        timePickerState
                                                                                                                .hour,
                                                                                                        timePickerState
                                                                                                                .minute
                                                                                                )
                                                                                )
                                                                )
                                                                showTimePicker = false
                                                        }
                                                ) { Text("OK") }
                                        }
                                }
                        }
                }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                        text = "When did you\nquit smoking?",
                        style =
                                MaterialTheme.typography.headlineLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                ),
                        textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                        text = "If you haven't quit yet,\npick when you will do it",
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray),
                        textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Display selected date/time
                val formatter = DateTimeFormatter.ofPattern("MMMM d 'at' HH:mm")
                val dateTimeString =
                        java.time.LocalDateTime.of(state.quitDate, state.quitTime).format(formatter)

                Text(
                        text = dateTimeString,
                        style = MaterialTheme.typography.headlineSmall.copy(color = Color.White),
                        textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        OutlinedButton(
                                onClick = { showDatePicker = true },
                                shape = RoundedCornerShape(50),
                                colors =
                                        ButtonDefaults.outlinedButtonColors(
                                                contentColor = Color(0xFF2E8B57)
                                        )
                        ) { Text("Pick date") }
                        OutlinedButton(
                                onClick = { showTimePicker = true },
                                shape = RoundedCornerShape(50),
                                colors =
                                        ButtonDefaults.outlinedButtonColors(
                                                contentColor = Color(0xFF2E8B57)
                                        )
                        ) { Text("Pick time") }
                }

                Spacer(modifier = Modifier.height(48.dp))

                Image(
                        painter = painterResource(id = R.drawable.calendar),
                        contentDescription = "Illustration",
                        modifier = Modifier.size(200.dp),
                        contentScale = ContentScale.Fit
                )
        }
}

@Composable
fun CigarettesPerDayStep(state: OnboardingUiState) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                        text = "How many\ncigarettes did you\nsmoke per day?",
                        style =
                                MaterialTheme.typography.headlineLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                ),
                        textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(48.dp))
                Text(
                        text = state.cigarettesPerDay.ifEmpty { "0" },
                        style = MaterialTheme.typography.displayMedium.copy(color = Color.White),
                )
                // Cursor effect
                Box(modifier = Modifier.height(40.dp).width(2.dp).background(Color(0xFF2E8B57)))
        }
}

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
                Text(
                        text = "What's the price\nof the pack?",
                        style =
                                MaterialTheme.typography.headlineLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                ),
                        textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(48.dp))
                Text(
                        text = "${state.currency}${state.costPerPack.ifEmpty { "0" }}",
                        style = MaterialTheme.typography.displayMedium.copy(color = Color.White),
                )
                Text(
                        text = "Change the currency",
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF2E8B57)),
                        modifier =
                                Modifier.padding(top = 16.dp).clickable {
                                        showCurrencyDialog = true
                                }
                )
        }
}

@Composable
fun CigarettesInPackStep(state: OnboardingUiState) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                        text = "How many cigarettes\nwere in the pack?",
                        style =
                                MaterialTheme.typography.headlineLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                ),
                        textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(48.dp))
                Text(
                        text = state.cigarettesInPack.ifEmpty { "0" },
                        style = MaterialTheme.typography.displayMedium.copy(color = Color.White),
                )
        }
}

@Composable
fun MinutesPerCigaretteStep(state: OnboardingUiState) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                        text = "How many minutes\ndid you take to\nsmoke one cigarette?",
                        style =
                                MaterialTheme.typography.headlineLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                ),
                        textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(48.dp))
                Text(
                        text = state.minutesPerCigarette.ifEmpty { "0" },
                        style = MaterialTheme.typography.displayMedium.copy(color = Color.White),
                )
        }
}

@Composable
fun ProjectionStep(state: OnboardingUiState) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                        text = "Your first month\nsmoke-free",
                        style =
                                MaterialTheme.typography.headlineLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                ),
                        textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        // First card: Cigarettes avoided
                        ProjectionCard(
                                iconRes = R.drawable.icon_flame,
                                value = state.projectedCigarettesAvoided.toString(),
                                label = "cigarettes\navoided",
                                color = Color(0xFFE57373)
                        )
                        // Second card: Money saved
                        ProjectionCard(
                                iconRes = R.drawable.icon_money,
                                value =
                                        "${state.currency}${String.format("%.2f", state.projectedMoneySaved)}",
                                label = "money\nsaved",
                                color = Color(0xFFFFD54F)
                        )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                        text =
                                "Your body will have improved in 4 different areas according to the WHO",
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 24.dp)
                )
        }
}

@Composable
fun ProjectionCard(iconRes: Int, value: String, label: String, color: Color) {
        Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C1E)),
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
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                                text = value,
                                style =
                                        MaterialTheme.typography.titleLarge.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                        )
                        )
                        Text(
                                text = label,
                                style =
                                        MaterialTheme.typography.bodyMedium.copy(
                                                color = Color.Gray
                                        ),
                                textAlign = TextAlign.Center
                        )
                }
        }
}

@Preview
@Composable
fun OnboardingPreview() {
        QuitSmokingTheme { OnboardingContent(state = OnboardingUiState(), onEvent = {}) }
}
