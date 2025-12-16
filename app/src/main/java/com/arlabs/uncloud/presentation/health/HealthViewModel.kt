package com.arlabs.uncloud.presentation.health

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arlabs.uncloud.domain.model.HealthMilestone
import com.arlabs.uncloud.domain.model.UserConfig
import com.arlabs.uncloud.domain.repository.HealthRepository
import com.arlabs.uncloud.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class HealthViewModel
@Inject
constructor(
    private val userRepository: UserRepository,
    private val healthRepository: HealthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HealthUiState())
    val uiState = _uiState.asStateFlow()

    private val milestones by lazy { healthRepository.getMilestones() }
    private val dateFormatter =
        DateTimeFormatter.ofPattern("MMM dd, yyyy").withZone(ZoneId.systemDefault())

    init {
        observeUserConfig()
        startTimer()
    }

    private fun observeUserConfig() {
        viewModelScope.launch {
            userRepository.userConfig.collect { config ->
                if (config != null) {
                    updateMilestones(config)
                }
            }
        }
    }

    private fun startTimer() {
        viewModelScope.launch {
            while (true) {
                val config = uiState.value.userConfig
                if (config != null) {
                    updateMilestones(config)
                }
                delay(1000)
            }
        }
    }

    private fun updateMilestones(config: UserConfig) {
        val now = Instant.now().toEpochMilli()
        val quitTime = config.quitTimestamp
        val diffSeconds = (now - quitTime) / 1000

        val uiModels =
            milestones.map { milestone ->
                val isCompleted = diffSeconds >= milestone.durationSeconds

                // If completed, show achieved date
                val achievedDate =
                    if (isCompleted) {
                        val achievedInstant =
                            Instant.ofEpochMilli(
                                quitTime + (milestone.durationSeconds * 1000)
                            )
                        dateFormatter.format(achievedInstant)
                    } else null

                // If not completed, show due time text
                val dueTimeText =
                    if (!isCompleted) {
                        val remainingSeconds = milestone.durationSeconds - diffSeconds
                        formatRemainingTime(remainingSeconds)
                    } else null

                HealthMilestoneUiModel(
                    milestone = milestone,
                    isCompleted = isCompleted,
                    achievedDate = achievedDate,
                    dueTimeText = dueTimeText,
                    progress = 0f
                )
            }

        // Logic to determine "In Progress"
        val firstUncompletedIndex = uiModels.indexOfFirst { !it.isCompleted }

        val finalModels =
            uiModels.mapIndexed { index, model ->
                if (index == firstUncompletedIndex) {
                    // Calculate progress
                    val previousSeconds =
                        if (index > 0) milestones[index - 1].durationSeconds else 0L
                    val targetSeconds = model.milestone.durationSeconds
                    val totalRange = targetSeconds - previousSeconds
                    val elapsedInRange = diffSeconds - previousSeconds
                    val progress =
                        (elapsedInRange.toDouble() / totalRange)
                            .coerceIn(0.0, 1.0)
                            .toFloat()

                    model.copy(status = MilestoneStatus.IN_PROGRESS, progress = progress)
                } else if (model.isCompleted) {
                    model.copy(status = MilestoneStatus.COMPLETED, progress = 1f)
                } else {
                    model.copy(status = MilestoneStatus.LOCKED, progress = 0f)
                }
            }

        _uiState.update { it.copy(userConfig = config, milestones = finalModels) }
    }

    private fun formatRemainingTime(seconds: Long): String {
        // Constants for estimation
        val secPerDay = 24 * 3600L
        val secPerMonth = 30 * secPerDay // Approx 30 days
        val secPerYear = 365 * secPerDay // Approx 365 days

        val years = seconds / secPerYear
        val remainingAfterYears = seconds % secPerYear

        val months = remainingAfterYears / secPerMonth
        val remainingAfterMonths = remainingAfterYears % secPerMonth

        val days = remainingAfterMonths / secPerDay
        val hours = (seconds % secPerDay) / 3600
        val minutes = (seconds % 3600) / 60

        return when {
            years > 0 -> {
                // Example: "1 YEAR 2 MOS 5 DAYS"
                val yStr = "$years YEAR${if (years > 1) "S" else ""}"
                val mStr = if (months > 0) " $months MO${if (months > 1) "S" else ""}" else ""
                val dStr = if (days > 0) " $days DAY${if (days > 1) "S" else ""}" else ""
                "$yStr$mStr$dStr"
            }
            months > 0 -> {
                // Example: "5 MOS 2 DAYS"
                val mStr = "$months MO${if (months > 1) "S" else ""}"
                val dStr = if (days > 0) " $days DAY${if (days > 1) "S" else ""}" else ""
                "$mStr$dStr"
            }
            days > 0 -> "$days DAY${if (days > 1) "S" else ""}"
            hours > 0 -> "$hours HR${if (hours > 1) "S" else ""}"
            else -> "$minutes MIN${if (minutes > 1) "S" else ""}"
        }
    }
}

data class HealthUiState(
    val userConfig: UserConfig? = null,
    val milestones: List<HealthMilestoneUiModel> = emptyList()
)

data class HealthMilestoneUiModel(
    val milestone: HealthMilestone,
    val status: MilestoneStatus = MilestoneStatus.LOCKED,
    val isCompleted: Boolean,
    val achievedDate: String? = null,
    val dueTimeText: String? = null,
    val progress: Float = 0f
)

enum class MilestoneStatus {
    COMPLETED,
    IN_PROGRESS,
    LOCKED
}