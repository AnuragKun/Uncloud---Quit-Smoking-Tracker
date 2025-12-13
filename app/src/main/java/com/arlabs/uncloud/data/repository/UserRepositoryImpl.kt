package com.arlabs.uncloud.data.repository

import com.arlabs.uncloud.data.local.NotificationConfig
import com.arlabs.uncloud.data.local.UserPreferences
import com.arlabs.uncloud.data.local.dao.AchievementDao
import com.arlabs.uncloud.domain.model.UserConfig
import com.arlabs.uncloud.domain.repository.UserRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

import com.arlabs.uncloud.domain.manager.WidgetRefresher

class UserRepositoryImpl
@Inject
constructor(
        private val userPreferences: UserPreferences,
        private val achievementDao: AchievementDao,
        private val breachDao: com.arlabs.uncloud.data.local.dao.BreachDao,
        private val widgetRefresher: WidgetRefresher
) : UserRepository {

    override val userConfig: Flow<UserConfig?> = userPreferences.userConfig

    override val isDarkTheme: Flow<Boolean> = userPreferences.isDarkTheme

    override suspend fun saveUserConfig(config: UserConfig) {
        userPreferences.saveUserConfig(config)
        widgetRefresher.refreshWidgets()
    }

    override suspend fun setDarkTheme(isDark: Boolean) {
        userPreferences.setDarkTheme(isDark)
        // Theme change might affect widgets (colors)
        widgetRefresher.refreshWidgets()
    }

    override suspend fun clearUserData() {
        userPreferences.clear()
        widgetRefresher.refreshWidgets()
    }

    override val quoteState: Flow<Pair<String?, Int>> = userPreferences.quoteState

    override suspend fun saveQuoteState(date: String, index: Int) {
        userPreferences.saveQuoteState(date, index)
        widgetRefresher.refreshWidgets()
    }

    override val notificationSettings: Flow<NotificationConfig> =
            userPreferences.notificationSettings

    override suspend fun saveNotificationSettings(config: NotificationConfig) {
        userPreferences.saveNotificationSettings(config)
    }

    override suspend fun resetProgress() {
        userPreferences.clear()
        achievementDao.resetAchievements()
        widgetRefresher.refreshWidgets()
    }

    override val pledgeState: Flow<String?> = userPreferences.pledgeState

    override suspend fun savePledgeState(date: String) {
        userPreferences.savePledgeState(date)
        widgetRefresher.refreshWidgets()
    }

    override val breaches: Flow<List<com.arlabs.uncloud.domain.model.Breach>> = breachDao.getAllBreaches()

    override suspend fun reportBreach(trigger: String, notes: String?) {
        // 1. Get Current Config
        val currentConfig = userConfig.firstOrNull() ?: return
        
        // 2. Calculate Stats to Add to Lifetime
        val now = System.currentTimeMillis()
        val daysSinceQuit = if (currentConfig.quitTimestamp > 0) {
            java.util.concurrent.TimeUnit.MILLISECONDS.toDays(now - currentConfig.quitTimestamp)
        } else 0L

        // Approx calculation for stats
        val cigsSaved = (daysSinceQuit * currentConfig.cigarettesPerDay).toInt()
        val costPerCig = if (currentConfig.cigarettesInPack > 0) currentConfig.costPerPack / currentConfig.cigarettesInPack else 0.0
        val moneySaved = cigsSaved * costPerCig

        // 3. Update Lifetime Stats & Reset Timestamp
        val newLifetimeCigs = currentConfig.lifetimeCigarettes + cigsSaved
        val newLifetimeMoney = currentConfig.lifetimeMoney + moneySaved

        val newConfig = currentConfig.copy(
            quitTimestamp = now, // RESET!
            lifetimeCigarettes = newLifetimeCigs,
            lifetimeMoney = newLifetimeMoney
        )
        saveUserConfig(newConfig)
        // Widget update handled in saveUserConfig

        // 4. Record Breach
        val breach = com.arlabs.uncloud.domain.model.Breach(
            timestamp = now,
            trigger = trigger,
            notes = notes
        )
        breachDao.insertBreach(breach)
        
        // 5. Reset Pledge
        savePledgeState("") 
        // Widget update handled in savePledgeState
        
        // Final refresh to be sure everything is caught
        widgetRefresher.refreshWidgets()
    }
}
