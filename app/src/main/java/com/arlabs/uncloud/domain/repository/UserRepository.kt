package com.arlabs.uncloud.domain.repository

import com.arlabs.uncloud.data.local.NotificationConfig
import com.arlabs.uncloud.domain.model.UserConfig
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    val userConfig: Flow<UserConfig?>
    val isDarkTheme: Flow<Boolean>
    suspend fun saveUserConfig(config: UserConfig)
    suspend fun setDarkTheme(isDark: Boolean)
    suspend fun clearUserData()

    val quoteState: Flow<Pair<String?, Int>>
    suspend fun saveQuoteState(date: String, index: Int)

    val notificationSettings: Flow<NotificationConfig>
    suspend fun saveNotificationSettings(config: NotificationConfig)
    suspend fun resetProgress()
}
