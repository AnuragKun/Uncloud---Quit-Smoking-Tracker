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

                    // Progress for in-progress: Only relevant for the *first* uncompleted item?
                    // Or maybe calculate percentage progress for *all*?
                    // Let's stick to the list logic:
                    // 1. Completed
                    // 2. In Progress (The one we are currently working on)
                    // 3. Locked (Future)

                    // Wait, mapping is purely state based. The distinction between
                    // InProgress/Locked depends on list order.
                    // But we can determine if it's the *active* one by checking if it's the first
                    // uncompleted one.

                    HealthMilestoneUiModel(
                            milestone = milestone,
                            isCompleted = isCompleted,
                            achievedDate = achievedDate,
                            dueTimeText = dueTimeText,
                            progress = 0f // Will verify logic in next step
                    )
                }

        // Logic to determine "In Progress" and its progress
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
        val days = seconds / (24 * 3600)
        val hours = (seconds % (24 * 3600)) / 3600
        val minutes = (seconds % 3600) / 60

        return when {
            days > 0 -> "Due in $days days"
            hours > 0 -> "Due in $hours hours"
            else -> "Due in $minutes mins"
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
