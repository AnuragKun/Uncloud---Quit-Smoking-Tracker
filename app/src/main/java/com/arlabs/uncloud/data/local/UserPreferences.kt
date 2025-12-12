package com.arlabs.uncloud.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.arlabs.uncloud.domain.model.UserConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

@Singleton
class UserPreferences @Inject constructor(@ApplicationContext private val context: Context) {
    private val dataStore = context.dataStore

    companion object {
        val CIGARETTES_PER_DAY = intPreferencesKey("cigarettes_per_day")
        val COST_PER_PACK = doublePreferencesKey("cost_per_pack")
        val CIGS_IN_PACK = intPreferencesKey("cigs_in_pack")
        val MINUTES_PER_CIGARETTE = intPreferencesKey("minutes_per_cigarette")
        val QUIT_TIMESTAMP = longPreferencesKey("quit_timestamp")
        val IS_DARK_THEME = booleanPreferencesKey("is_dark_theme")
        val CURRENCY = stringPreferencesKey("currency")
        val LAST_QUOTE_DATE = stringPreferencesKey("last_quote_date")
        val CURRENT_QUOTE_INDEX = intPreferencesKey("current_quote_index")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val HEALTH_NOTIFICATIONS_ENABLED = booleanPreferencesKey("health_notifications_enabled")
        val NOTIFICATION_HOUR = intPreferencesKey("notification_hour")
        val NOTIFICATION_MINUTE = intPreferencesKey("notification_minute")
    }

    val userConfig: Flow<UserConfig?> =
            dataStore.data.map { preferences ->
                val cigsPerDay = preferences[CIGARETTES_PER_DAY]
                val cost = preferences[COST_PER_PACK]
                val cigsInPack = preferences[CIGS_IN_PACK]
                val minutesPerCig =
                        preferences[MINUTES_PER_CIGARETTE] ?: 10 // Default to 10 if missing
                val timestamp = preferences[QUIT_TIMESTAMP]
                val currency = preferences[CURRENCY] ?: "$"

                if (cigsPerDay != null && cost != null && cigsInPack != null && timestamp != null) {
                    UserConfig(cigsPerDay, cost, cigsInPack, minutesPerCig, timestamp, currency)
                } else {
                    null
                }
            }

    val isDarkTheme: Flow<Boolean> =
            dataStore.data.map { preferences -> preferences[IS_DARK_THEME] ?: false }

    suspend fun saveUserConfig(config: UserConfig) {
        dataStore.edit { preferences ->
            preferences[CIGARETTES_PER_DAY] = config.cigarettesPerDay
            preferences[COST_PER_PACK] = config.costPerPack
            preferences[CIGS_IN_PACK] = config.cigarettesInPack
            preferences[MINUTES_PER_CIGARETTE] = config.minutesPerCigarette
            preferences[QUIT_TIMESTAMP] = config.quitTimestamp
            preferences[CURRENCY] = config.currency
        }
    }

    suspend fun setDarkTheme(isDark: Boolean) {
        dataStore.edit { preferences -> preferences[IS_DARK_THEME] = isDark }
    }

    val quoteState: Flow<Pair<String?, Int>> =
            dataStore.data.map { preferences ->
                val date = preferences[LAST_QUOTE_DATE]
                val index = preferences[CURRENT_QUOTE_INDEX] ?: 0
                Pair(date, index)
            }

    suspend fun saveQuoteState(date: String, index: Int) {
        dataStore.edit { preferences ->
            preferences[LAST_QUOTE_DATE] = date
            preferences[CURRENT_QUOTE_INDEX] = index
        }
    }

    suspend fun clear() {
        dataStore.edit { it.clear() }
    }

    val notificationSettings: Flow<NotificationConfig> =
            dataStore.data.map { preferences ->
                NotificationConfig(
                        dailyMotivationEnabled = preferences[NOTIFICATIONS_ENABLED] ?: true,
                        healthMilestonesEnabled = preferences[HEALTH_NOTIFICATIONS_ENABLED] ?: true,
                        notificationHour = preferences[NOTIFICATION_HOUR] ?: 9,
                        notificationMinute = preferences[NOTIFICATION_MINUTE] ?: 0
                )
            }

    suspend fun saveNotificationSettings(config: NotificationConfig) {
        dataStore.edit { preferences ->
            preferences[NOTIFICATIONS_ENABLED] = config.dailyMotivationEnabled
            preferences[HEALTH_NOTIFICATIONS_ENABLED] = config.healthMilestonesEnabled
            preferences[NOTIFICATION_HOUR] = config.notificationHour
            preferences[NOTIFICATION_MINUTE] = config.notificationMinute
        }
    }
}

data class NotificationConfig(
        val dailyMotivationEnabled: Boolean,
        val healthMilestonesEnabled: Boolean,
        val notificationHour: Int,
        val notificationMinute: Int
)
