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
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import com.arlabs.uncloud.domain.model.Rank
import com.arlabs.uncloud.domain.model.rankSystem
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.firstOrNull

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
        observeBreaches()
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

    private fun observeBreaches() {
        viewModelScope.launch {
            userRepository.breaches.collect { breachList ->
                _uiState.update { it.copy(breaches = breachList) }
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
        
        val nowDateTime = LocalDateTime.now()
        val quitDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(quitTime), ZoneId.systemDefault())
        
        var tempDateTime = LocalDateTime.from(quitDateTime)
        
        val years = tempDateTime.until(nowDateTime, ChronoUnit.YEARS)
        tempDateTime = tempDateTime.plusYears(years)
        
        val months = tempDateTime.until(nowDateTime, ChronoUnit.MONTHS)
        tempDateTime = tempDateTime.plusMonths(months)
        
        val days = tempDateTime.until(nowDateTime, ChronoUnit.DAYS)
        tempDateTime = tempDateTime.plusDays(days)
        
        val hours = tempDateTime.until(nowDateTime, ChronoUnit.HOURS)
        tempDateTime = tempDateTime.plusHours(hours)
        
        val minutes = tempDateTime.until(nowDateTime, ChronoUnit.MINUTES)
        tempDateTime = tempDateTime.plusMinutes(minutes)
        
        val seconds = tempDateTime.until(nowDateTime, ChronoUnit.SECONDS)
        
        val totalDuration = Duration.ofMillis(diffMillis)
        val totalDays = totalDuration.toDays()
        val totalSeconds = totalDuration.seconds

        // Calculate Projections
        val cigsPerDaySafeguard = if (config.cigarettesPerDay > 0) config.cigarettesPerDay else 1
        val millisPerCigarette = (24 * 60 * 60 * 1000L) / cigsPerDaySafeguard
        val cigsAvoidedCurrent = diffMillis / millisPerCigarette
        
        // CORRECTION: Add Lifetime Stats
        val totalCigsAvoided = config.lifetimeCigarettes + cigsAvoidedCurrent

        val packs = if (config.cigarettesInPack > 0) config.cigarettesInPack else 20
        val costPerCig = config.costPerPack / packs
        val moneySavedCurrent = cigsAvoidedCurrent * costPerCig
        
        // CORRECTION: Add Lifetime Stats
        val totalMoneySaved = config.lifetimeMoney + moneySavedCurrent

        val scheduleMinutes = totalCigsAvoided * config.minutesPerCigarette
        val scheduleTime = formatDuration(scheduleMinutes)

        val biologicalMinutes = totalCigsAvoided * 11
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
                    yearsSinceQuit = years,
                    monthsSinceQuit = months,
                    daysSinceQuit = totalDays,
                    timerDays = days,
                    hoursSinceQuit = hours,
                    minutesSinceQuit = minutes,
                    secondsSinceQuit = seconds,
                    cigarettesAvoided = totalCigsAvoided,
                    moneySaved = totalMoneySaved,
                    scheduleTimeRegained = scheduleTime,
                    biologicalTimeRegained = biologicalTime,
                    currentMilestone = nextMilestone ?: milestones.last(),
                    milestoneProgress = progress,
                    totalMilestones = milestones.size,
                    currentRank = rankSystem.lastOrNull { it.daysRequired <= totalDays } ?: rankSystem.first()
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

    fun reportBreach(trigger: String) {
        viewModelScope.launch {
            userRepository.reportBreach(trigger, null)
            // Refresh config immediately
            val updatedConfig = userRepository.userConfig.firstOrNull()
            if (updatedConfig != null) {
                _uiState.update { it.copy(userConfig = updatedConfig) }
                updateStats(updatedConfig)
            }
        }
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
        val breaches: List<com.arlabs.uncloud.domain.model.Breach> = emptyList(),
        val yearsSinceQuit: Long = 0,
        val monthsSinceQuit: Long = 0,
        val daysSinceQuit: Long = 0,
        val timerDays: Long = 0,
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
        val quote: Quote? = null,
        val currentRank: Rank? = null
)
