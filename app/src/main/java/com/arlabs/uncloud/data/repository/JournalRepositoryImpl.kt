package com.arlabs.uncloud.data.repository

import com.arlabs.uncloud.data.local.dao.JournalDao
import com.arlabs.uncloud.data.local.entity.JournalEntry
import com.arlabs.uncloud.data.local.entity.SystemStatus
import com.arlabs.uncloud.domain.repository.JournalRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class JournalRepositoryImpl @Inject constructor(
    private val journalDao: JournalDao
) : JournalRepository {

    override fun getAllEntries(): Flow<List<JournalEntry>> {
        return journalDao.getAllEntries()
    }

    override suspend fun insertEntry(content: String, status: SystemStatus, triggers: List<String>, timestamp: Long) {
        val entry = JournalEntry(
            timestamp = timestamp,
            content = content,
            status = status,
            triggers = triggers
        )
        journalDao.insertEntry(entry)
    }

    override suspend fun deleteEntry(entry: JournalEntry) {
        journalDao.deleteEntry(entry)
    }

    override suspend fun deleteAll() {
        journalDao.deleteAll()
    }
}
