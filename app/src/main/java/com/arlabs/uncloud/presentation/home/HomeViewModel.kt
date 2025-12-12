package com.arlabs.uncloud.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arlabs.uncloud.domain.manager.QuoteManager
import com.arlabs.uncloud.domain.model.HealthMilestone
import com.arlabs.uncloud.domain.model.Quote
import com.arlabs.uncloud.domain.model.UserConfig
import com.arlabs.uncloud.domain.repository.HealthRepository
import com.arlabs.uncloud.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Duration
import java.time.Instant
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel
@Inject
constructor(
        private val userRepository: UserRepository,
        private val healthRepository: HealthRepository,
        private val quoteManager: QuoteManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    private val milestones by lazy { healthRepository.getMilestones() }

    init {
        loadDailyQuote()
        observeUserConfig()
        startTimer()
    }

    private fun loadDailyQuote() {
        viewModelScope.launch {
            val quote = quoteManager.getDailyQuote()
            _uiState.update { it.copy(quote = quote) }
        }
    }

    fun refreshQuote() {
        viewModelScope.launch {
            val quote = quoteManager.forceRefreshQuote()
            _uiState.update { it.copy(quote = quote) }
        }
    }

    private fun observeUserConfig() {
        viewModelScope.launch {
            userRepository.userConfig.collect { config ->
                _uiState.update { it.copy(isLoading = false, userConfig = config) }
                if (config != null) {
                    updateStats(config)
                }
            }
        }
    }

    private fun startTimer() {
        viewModelScope.launch {
            while (true) {
                val config = _uiState.value.userConfig
                if (config != null) {
                    updateStats(config)
                }
                delay(1000) // Update every second
            }
        }
    }

    private fun updateStats(config: UserConfig) {
        val now = Instant.now().toEpochMilli()
        val quitTime = config.quitTimestamp
        val diffMillis = now - quitTime

        if (diffMillis < 0) return // Future date?

        val duration = Duration.ofMillis(diffMillis)
        val days = duration.toDays()
        val hours = duration.toHours() % 24
        val minutes = duration.toMinutes() % 60
        val seconds = duration.seconds % 60
        val totalSeconds = duration.seconds

        // Calculate Projections
        val cigsPerDaySafeguard = if (config.cigarettesPerDay > 0) config.cigarettesPerDay else 1
        val millisPerCigarette = (24 * 60 * 60 * 1000L) / cigsPerDaySafeguard
        val cigsAvoided = diffMillis / millisPerCigarette

        val packs = if (config.cigarettesInPack > 0) config.cigarettesInPack else 20
        val costPerCig = config.costPerPack / packs
        val moneySaved = cigsAvoided * costPerCig

        val scheduleMinutes = cigsAvoided * config.minutesPerCigarette
        val scheduleTime = formatDuration(scheduleMinutes)

        val biologicalMinutes = cigsAvoided * 11
        val biologicalTime = formatDuration(biologicalMinutes)

        // Health Milestones
        val nextMilestone = milestones.firstOrNull { it.durationSeconds > totalSeconds }
        val achievedMilestone = milestones.lastOrNull { it.durationSeconds <= totalSeconds }

        // Progress for next milestone
        val previousMilestoneSeconds = achievedMilestone?.durationSeconds ?: 0
        val targetSeconds = nextMilestone?.durationSeconds ?: 1L // avoid div/0

        val progress =
                if (nextMilestone != null) {
                    val totalRange = targetSeconds - previousMilestoneSeconds
                    val elapsedInRange = totalSeconds - previousMilestoneSeconds
                    (elapsedInRange.toDouble() / totalRange).coerceIn(0.0, 1.0).toFloat()
                } else {
                    1.0f // All done
                }

        _uiState.update {
            it.copy(
                    daysSinceQuit = days,
                    hoursSinceQuit = hours,
                    minutesSinceQuit = minutes,
                    secondsSinceQuit = seconds,
                    cigarettesAvoided = cigsAvoided,
                    moneySaved = moneySaved,
                    scheduleTimeRegained = scheduleTime,
                    biologicalTimeRegained = biologicalTime,
                    currentMilestone = nextMilestone ?: milestones.last(),
                    milestoneProgress = progress,
                    completedMilestones = achievedMilestone?.let { milestones.indexOf(it) + 1 }
                                    ?: 0,
                    totalMilestones = milestones.size
            )
        }
    }

    fun selectStat(type: StatType) {
        val config = _uiState.value.userConfig ?: return

        val dailyValue: Double =
                when (type) {
                    StatType.CIGARETTES -> config.cigarettesPerDay.toDouble()
                    StatType.MONEY -> {
                        val packs = if (config.cigarettesInPack > 0) config.cigarettesInPack else 20
                        val costPerCig = config.costPerPack / packs
                        config.cigarettesPerDay * costPerCig
                    }
                    StatType.TIME -> {
                        (config.cigarettesPerDay * config.minutesPerCigarette).toDouble()
                    }
                }

        val projections =
                ProjectedStats(
                        oneDay = dailyValue,
                        oneWeek = dailyValue * 7,
                        oneMonth = dailyValue * 30,
                        oneYear = dailyValue * 365
                )

        _uiState.update { it.copy(selectedStatType = type, projectedStats = projections) }
    }

    fun dismissDialog() {
        _uiState.update { it.copy(selectedStatType = null, projectedStats = null) }
    }

    private fun formatDuration(totalMinutes: Long): String {
        val days = totalMinutes / (24 * 60)
        val hours = (totalMinutes % (24 * 60)) / 60
        val minutes = totalMinutes % 60

        return when {
            days > 0 -> "${days}d ${hours}h"
            hours > 0 -> "${hours}h ${minutes}m"
            else -> "${minutes}m"
        }
    }
}

enum class StatType {
    CIGARETTES,
    MONEY,
    TIME
}

data class ProjectedStats(
        val oneDay: Double,
        val oneWeek: Double,
        val oneMonth: Double,
        val oneYear: Double
)

data class HomeUiState(
        val isLoading: Boolean = true,
        val userConfig: UserConfig? = null,
        val daysSinceQuit: Long = 0,
        val hoursSinceQuit: Long = 0,
        val minutesSinceQuit: Long = 0,
        val secondsSinceQuit: Long = 0,
        val cigarettesAvoided: Long = 0,
        val moneySaved: Double = 0.0,
        val scheduleTimeRegained: String = "0m",
        val biologicalTimeRegained: String = "0m",
        val selectedStatType: StatType? = null,
        val projectedStats: ProjectedStats? = null,
        val currentMilestone: HealthMilestone? = null,
        val milestoneProgress: Float = 0f,
        val completedMilestones: Int = 0,
        val totalMilestones: Int = 0,
        val quote: Quote? = null
)
