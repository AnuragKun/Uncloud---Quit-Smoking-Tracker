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
import com.arlabs.uncloud.domain.manager.BackupManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import android.net.Uri

@HiltViewModel
class SettingsViewModel
@Inject
constructor(
        private val userRepository: UserRepository,
        private val milestoneScheduler: com.arlabs.uncloud.domain.manager.MilestoneScheduler,
        private val dailyMotivationScheduler: com.arlabs.uncloud.domain.manager.DailyMotivationScheduler,
        private val backupManager: BackupManager
) : ViewModel() {

    private val _backupState = MutableStateFlow<BackupState>(BackupState.Idle)
    val backupState = _backupState.asStateFlow()

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

    fun exportData(uri: Uri) {
        viewModelScope.launch {
            _backupState.value = BackupState.Loading
            try {
                val success = backupManager.exportToUri(uri)
                if (success) {
                    _backupState.value = BackupState.Success("Backup saved successfully.")
                } else {
                    _backupState.value = BackupState.Error("Export failed.")
                }
            } catch (e: Exception) {
                _backupState.value = BackupState.Error(e.message ?: "Export failed")
            }
        }
    }

    fun importData(uri: Uri) {
        viewModelScope.launch {
            _backupState.value = BackupState.Loading
            try {
                val success = backupManager.restoreBackup(uri)
                if (success) {
                    _backupState.value = BackupState.Success("Data restored. Restarting recommended.")
                } else {
                    _backupState.value = BackupState.Error("Import failed: Invalid file.")
                }
            } catch (e: Exception) {
                _backupState.value = BackupState.Error(e.message ?: "Import failed")
            }
        }
    }

    fun acknowledgeBackupState() {
        _backupState.value = BackupState.Idle
    }
}

sealed class BackupState {
    object Idle : BackupState()
    object Loading : BackupState()
    data class Success(val message: String) : BackupState()
    data class Error(val message: String) : BackupState()
}
