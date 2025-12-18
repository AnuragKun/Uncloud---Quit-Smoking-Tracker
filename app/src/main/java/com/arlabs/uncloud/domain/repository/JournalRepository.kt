package com.arlabs.uncloud.domain.repository

import com.arlabs.uncloud.data.local.entity.JournalEntry
import kotlinx.coroutines.flow.Flow

interface JournalRepository {
    fun getAllEntries(): Flow<List<JournalEntry>>
    suspend fun insertEntry(content: String, status: com.arlabs.uncloud.data.local.entity.SystemStatus, triggers: List<String>, timestamp: Long)
    suspend fun deleteEntry(entry: JournalEntry)
    suspend fun deleteAll()
}
