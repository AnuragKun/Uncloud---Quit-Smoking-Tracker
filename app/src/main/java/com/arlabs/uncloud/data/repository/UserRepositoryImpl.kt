package com.arlabs.uncloud.data.repository

import com.arlabs.uncloud.data.local.NotificationConfig
import com.arlabs.uncloud.data.local.UserPreferences
import com.arlabs.uncloud.data.local.dao.AchievementDao
import com.arlabs.uncloud.domain.model.UserConfig
import com.arlabs.uncloud.domain.repository.UserRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class UserRepositoryImpl
@Inject
constructor(
        private val userPreferences: UserPreferences,
        private val achievementDao: AchievementDao
) : UserRepository {

    override val userConfig: Flow<UserConfig?> = userPreferences.userConfig

    override val isDarkTheme: Flow<Boolean> = userPreferences.isDarkTheme

    override suspend fun saveUserConfig(config: UserConfig) {
        userPreferences.saveUserConfig(config)
    }

    override suspend fun setDarkTheme(isDark: Boolean) {
        userPreferences.setDarkTheme(isDark)
    }

    override suspend fun clearUserData() {
        userPreferences.clear()
    }

    override val quoteState: Flow<Pair<String?, Int>> = userPreferences.quoteState

    override suspend fun saveQuoteState(date: String, index: Int) {
        userPreferences.saveQuoteState(date, index)
    }

    override val notificationSettings: Flow<NotificationConfig> =
            userPreferences.notificationSettings

    override suspend fun saveNotificationSettings(config: NotificationConfig) {
        userPreferences.saveNotificationSettings(config)
    }

    override suspend fun resetProgress() {
        userPreferences.clear()
        achievementDao.resetAchievements()
    }
}
