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
        private val journalRepository: com.arlabs.uncloud.domain.repository.JournalRepository,
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
        breachDao.deleteAll()
        journalRepository.deleteAll()
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
        
        // 2. Calculate Stats to Add to Lifetime (PRECISELY)
        val now = System.currentTimeMillis()
        val quitTime = currentConfig.quitTimestamp
        val diffMillis = if (quitTime > 0) now - quitTime else 0L

        // Calculate cigarettes avoided in this session
        val cigsPerDay = if (currentConfig.cigarettesPerDay > 0) currentConfig.cigarettesPerDay else 1
        val millisPerCigarette = (24 * 60 * 60 * 1000L) / cigsPerDay
        val cigsSavedDouble = diffMillis.toDouble() / millisPerCigarette
        val cigsSaved = cigsSavedDouble.toLong() // Truncate for safety, or keep double? Int is safer for DB.

        // Calculate money saved in this session
        val packs = if (currentConfig.cigarettesInPack > 0) currentConfig.cigarettesInPack else 20
        val costPerCig = currentConfig.costPerPack / packs
        val moneySaved = cigsSavedDouble * costPerCig // Use double for precision

        // 3. Update Lifetime Stats & Reset Timestamp
        // We add the stats from the *current streak* to the *lifetime total*
        val newLifetimeCigs = currentConfig.lifetimeCigarettes + cigsSaved.toInt()
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
    override suspend fun clearBreachesBefore(timestamp: Long) {
        breachDao.deleteBreachesBefore(timestamp)
    }
}
