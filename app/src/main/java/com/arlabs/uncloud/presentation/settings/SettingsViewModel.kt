package com.arlabs.uncloud.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arlabs.uncloud.data.local.NotificationConfig
import com.arlabs.uncloud.domain.model.UserConfig
import com.arlabs.uncloud.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel
@Inject
constructor(
        private val userRepository: UserRepository,
        private val milestoneScheduler: com.arlabs.uncloud.domain.manager.MilestoneScheduler,
        private val dailyMotivationScheduler: com.arlabs.uncloud.domain.manager.DailyMotivationScheduler
) : ViewModel() {

        val userConfig: StateFlow<UserConfig?> =
                userRepository.userConfig.stateIn(
                        scope = viewModelScope,
                        started = SharingStarted.WhileSubscribed(5000),
                        initialValue = null
                )

        val notificationSettings: StateFlow<NotificationConfig> =
                userRepository.notificationSettings.stateIn(
                        scope = viewModelScope,
                        started = SharingStarted.WhileSubscribed(5000),
                        initialValue = NotificationConfig(true, true, 9, 0)
                )

        val isDarkTheme: StateFlow<Boolean> =
                userRepository.isDarkTheme.stateIn(
                        scope = viewModelScope,
                        started = SharingStarted.WhileSubscribed(5000),
                        initialValue = true // Default to dark
                )

        fun updateUserConfig(
                costPerPack: Double,
                cigsPerDay: Int,
                cigsInPack: Int,
                currency: String
        ) {
                val currentConfig = userConfig.value ?: return
                val newConfig =
                        currentConfig.copy(
                                costPerPack = costPerPack,
                                cigarettesPerDay = cigsPerDay,
                                cigarettesInPack = cigsInPack,
                                currency = currency
                        )
                viewModelScope.launch { userRepository.saveUserConfig(newConfig) }
        }

        fun updateQuitDate(timestamp: Long) {
                val currentConfig = userConfig.value ?: return
                // When correcting the date, we assume this is the NEW "Start Date" for the journey.
                val newConfig = currentConfig.copy(quitTimestamp = timestamp, trackingStartDate = timestamp)
                viewModelScope.launch {
                        userRepository.saveUserConfig(newConfig)
                        userRepository.clearBreachesBefore(timestamp)
                        milestoneScheduler.scheduleMilestones(timestamp)
                }
        }

        fun updateNotificationSettings(
                dailyMotivation: Boolean,
                healthMilestones: Boolean,
                hour: Int,
                minute: Int
        ) {
                val newConfig =
                        NotificationConfig(
                                dailyMotivationEnabled = dailyMotivation,
                                healthMilestonesEnabled = healthMilestones,
                                notificationHour = hour,
                                notificationMinute = minute
                        )
                viewModelScope.launch { 
                    userRepository.saveNotificationSettings(newConfig)
                    if (dailyMotivation) {
                        dailyMotivationScheduler.schedule(hour, minute)
                    } else {
                        dailyMotivationScheduler.cancel()
                    }
                }
        }

        fun toggleTheme(isDark: Boolean) {
                viewModelScope.launch { userRepository.setDarkTheme(isDark) }
        }

        fun resetProgress() {
                viewModelScope.launch {
                        userRepository.resetProgress()
                        milestoneScheduler.cancelAll()
                }
        }
}
