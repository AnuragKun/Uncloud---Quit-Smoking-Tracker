package com.arlabs.uncloud.domain.model

import com.arlabs.uncloud.data.local.entity.JournalEntry
import com.arlabs.uncloud.data.local.entity.SystemStatus

data class BackupData(
    val version: Int = 1,
    val timestamp: Long = System.currentTimeMillis(),
    val userConfig: UserConfig?,
    val journalEntries: List<JournalEntry>,
    val achievements: List<AchievementState>, // Minimal state
    val breaches: List<Breach>
)

data class AchievementState(
    val id: String,
    val isUnlocked: Boolean,
    val unlockedDate: Long?
)
