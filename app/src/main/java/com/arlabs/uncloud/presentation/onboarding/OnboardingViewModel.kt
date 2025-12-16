package com.arlabs.uncloud.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arlabs.uncloud.domain.model.UserConfig
import com.arlabs.uncloud.domain.repository.UserRepository
import com.arlabs.uncloud.presentation.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class OnboardingViewModel
@Inject
constructor(
    private val userRepository: UserRepository,
    private val milestoneScheduler: com.arlabs.uncloud.domain.manager.MilestoneScheduler
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState = _uiState.asStateFlow()

    private val _navigationEvent = Channel<String>()
    val navigationEvent = _navigationEvent.receiveAsFlow()

    fun onEvent(event: OnboardingEvent) {
        when (event) {
            is OnboardingEvent.CigarettesPerDayChanged -> {
                // Handled via AppendInput in new logic
            }
            is OnboardingEvent.AppendInput -> handleAppendInput(event.input)
            OnboardingEvent.BackspaceInput -> handleBackspaceInput()
            is OnboardingEvent.QuitDateChanged -> {
                _uiState.update { it.copy(quitDate = event.date) }
            }
            is OnboardingEvent.QuitTimeChanged -> {
                _uiState.update { it.copy(quitTime = event.time) }
            }
            is OnboardingEvent.CurrencyChanged -> {
                _uiState.update { it.copy(currency = event.currency) }
            }
            OnboardingEvent.NextStep -> navigateNextStep()
            OnboardingEvent.PreviousStep -> navigatePreviousStep()
            OnboardingEvent.Submit -> submit()
        }
    }

    private fun handleAppendInput(input: String) {
        _uiState.update { state ->
            when (state.currentStep) {
                2 -> { // Cigarettes Per Day
                    val current = state.cigarettesPerDay
                    if (current.length < 3) {
                        state.copy(cigarettesPerDay = current + input)
                    } else state
                }
                3 -> { // Price Per Pack
                    val current = state.costPerPack
                    if (current.length < 5) {
                        state.copy(costPerPack = current + input)
                    } else state
                }
                4 -> { // Cigarettes In Pack
                    val current = state.cigarettesInPack
                    if (current.length < 3) {
                        state.copy(
                            cigarettesInPack = if (current == "0") input else current + input
                        )
                    } else state
                }
                5 -> { // Minutes Per Cigarette
                    val current = state.minutesPerCigarette
                    if (current.length < 3) {
                        state.copy(
                            minutesPerCigarette = if (current == "0") input else current + input
                        )
                    } else state
                }
                else -> state
            }
        }
    }

    private fun handleBackspaceInput() {
        _uiState.update { state ->
            when (state.currentStep) {
                2 -> state.copy(cigarettesPerDay = state.cigarettesPerDay.dropLast(1))
                3 -> state.copy(costPerPack = state.costPerPack.dropLast(1))
                4 -> state.copy(cigarettesInPack = state.cigarettesInPack.dropLast(1))
                5 -> state.copy(minutesPerCigarette = state.minutesPerCigarette.dropLast(1))
                else -> state
            }
        }
    }

    private fun navigateNextStep() {
        if (_uiState.value.currentStep < 6) {
            _uiState.update { it.copy(currentStep = it.currentStep + 1) }
        }
    }

    private fun navigatePreviousStep() {
        if (_uiState.value.currentStep > 0) {
            _uiState.update { it.copy(currentStep = it.currentStep - 1) }
        }
    }

    private fun submit() {
        val state = _uiState.value
        val cigsPerDay = state.cigarettesPerDay.toIntOrNull()
        val costPath = state.costPerPack.toDoubleOrNull()
        val cigsInPack = state.cigarettesInPack.toIntOrNull()
        val minutesPerCig = state.minutesPerCigarette.toIntOrNull()

        if (cigsPerDay != null && costPath != null && cigsInPack != null && minutesPerCig != null) {
            viewModelScope.launch {
                val quitDateTime = LocalDateTime.of(state.quitDate, state.quitTime)
                val quitTimestamp = quitDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

                val config = UserConfig(
                    cigarettesPerDay = cigsPerDay,
                    costPerPack = costPath,
                    cigarettesInPack = cigsInPack,
                    minutesPerCigarette = minutesPerCig,

                    quitTimestamp = quitTimestamp,
                    currency = state.currency,
                    trackingStartDate = quitTimestamp // Initialize tracking start date
                )
                userRepository.saveUserConfig(config)
                milestoneScheduler.scheduleMilestones(quitTimestamp)
                _navigationEvent.send(Screen.Home.route)
            }
        }
    }
}

data class OnboardingUiState(
    val currentStep: Int = 0,
    val cigarettesPerDay: String = "",
    val costPerPack: String = "",
    val cigarettesInPack: String = "",
    val minutesPerCigarette: String = "",
    val quitDate: LocalDate = LocalDate.now(),
    val quitTime: LocalTime = LocalTime.now(),
    val currency: String = "$",
    val isLoading: Boolean = false
) {
    val projectedCigarettesAvoided: Int
        get() = (cigarettesPerDay.toIntOrNull() ?: 0) * 30

    val projectedMoneySaved: Double
        get() {
            val cigsPerDay = cigarettesPerDay.toDoubleOrNull() ?: 0.0
            val cost = costPerPack.toDoubleOrNull() ?: 0.0
            val packSize = cigarettesInPack.toDoubleOrNull() ?: 1.0
            if (packSize == 0.0) return 0.0
            val costPerCig = cost / packSize
            return cigsPerDay * costPerCig * 30
        }
}

sealed interface OnboardingEvent {
    data class CigarettesPerDayChanged(val value: String) : OnboardingEvent
    data class AppendInput(val input: String) : OnboardingEvent
    data object BackspaceInput : OnboardingEvent
    data class QuitDateChanged(val date: LocalDate) : OnboardingEvent
    data class QuitTimeChanged(val time: LocalTime) : OnboardingEvent
    data class CurrencyChanged(val currency: String) : OnboardingEvent
    data object NextStep : OnboardingEvent
    data object PreviousStep : OnboardingEvent
    data object Submit : OnboardingEvent
}