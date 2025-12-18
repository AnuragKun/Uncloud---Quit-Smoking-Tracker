package com.arlabs.uncloud.domain.manager

import android.content.Context
import android.net.Uri
import com.arlabs.uncloud.data.local.entity.JournalEntry
import com.arlabs.uncloud.domain.model.AchievementState
import com.arlabs.uncloud.domain.model.BackupData
import com.arlabs.uncloud.domain.model.Breach
import com.arlabs.uncloud.domain.repository.AchievementRepository
import com.arlabs.uncloud.domain.repository.JournalRepository
import com.arlabs.uncloud.domain.repository.UserRepository
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.inject.Inject

class BackupManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userRepository: UserRepository,
    private val journalRepository: JournalRepository,
    private val achievementRepository: AchievementRepository
) {
    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()

    suspend fun exportToUri(uri: Uri): Boolean = withContext(Dispatchers.IO) {
        try {
            val json = createBackupJson()
            context.contentResolver.openOutputStream(uri)?.use { output ->
                output.write(json.toByteArray())
            }
            return@withContext true
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext false
        }
    }

    private suspend fun createBackupJson(): String = withContext(Dispatchers.IO) {
        val config = userRepository.userConfig.first()
        val entries = journalRepository.getAllEntries().first()
        val breaches = userRepository.breaches.first()
        val achievements = achievementRepository.getAllAchievements().first()

        val achievementStates = achievements.map { 
            AchievementState(it.id, it.isUnlocked, it.unlockedDate) 
        }

        val backup = BackupData(
            userConfig = config,
            journalEntries = entries,
            achievements = achievementStates,
            breaches = breaches
        )

        return@withContext gson.toJson(backup)
    }

    suspend fun restoreBackup(uri: Uri): Boolean = withContext(Dispatchers.IO) {
        try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return@withContext false
            val json = BufferedReader(InputStreamReader(inputStream)).use { it.readText() }
            
            val backup = gson.fromJson(json, BackupData::class.java)

            // 1. Restore Config
            backup.userConfig?.let { userRepository.saveUserConfig(it) }

            // 2. Restore Journal
            // Clear existing logic handled by Reset? No, we should likely clear before restore or merge.
            // Plan: Wipe & Replace.
            userRepository.resetProgress() // This wipes everything (Config, Achievements, Breaches, Logs).
            
            // Re-save Config (since reset wiped it)
            backup.userConfig?.let { userRepository.saveUserConfig(it) }

            // Re-insert Journal
            backup.journalEntries.forEach { entry ->
                journalRepository.insertEntry(entry.content, entry.status, entry.triggers, entry.timestamp)
            }

            // Restore Breaches (Need to expose insertBreach in Repository? Or use REPORT?)
            // Repository only has 'reportBreach' which calculates logic. 
            // We need 'insertBreach' simply to restore history.
            // Currently UserRepository does NOT expose raw insertBreach.
            // We might need to add it, or use a workaround.
            // Workaround: We can't use reportBreach because it modifies config.
            // We MUST add `restoreBreaches` to UserRepository.
            
            // Restore Achievements
            backup.achievements.filter { it.isUnlocked }.forEach { state ->
                // Check if achievement exists in repo (we iterate local achievements and match ID)
                // Actually repository has `updateAchievement`. 
                // We need to fetch domain object, update state, save.
                // Simplified: We assume IDs match.
                // We need a way to get the full Achievement object to update it.
                // Iterate all current achievements, find match, update.
                val allAchievements = achievementRepository.getAllAchievements().first()
                val target = allAchievements.find { it.id == state.id }
                target?.let {
                    val updated = it.copy(isUnlocked = true, unlockedDate = state.unlockedDate)
                    achievementRepository.updateAchievement(updated)
                }
            }
            
            // Restore Breaches (Pending Repo Update)
             userRepository.restoreBreaches(backup.breaches)

            return@withContext true
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext false
        }
    }
}
