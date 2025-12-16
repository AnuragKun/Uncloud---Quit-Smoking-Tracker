package com.arlabs.uncloud.presentation.journal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arlabs.uncloud.data.local.entity.JournalEntry
import com.arlabs.uncloud.data.local.entity.SystemStatus
import com.arlabs.uncloud.domain.repository.JournalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JournalViewModel @Inject constructor(
    private val journalRepository: JournalRepository
) : ViewModel() {

    private val _entries = journalRepository.getAllEntries()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _filter = kotlinx.coroutines.flow.MutableStateFlow<SystemStatus?>(null)

    val entries = kotlinx.coroutines.flow.combine(_entries, _filter) { entries, filter ->
        if (filter == null) entries else entries.filter { it.status == filter }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val statusCounts = _entries.map { list ->
        val total = list.size.coerceAtLeast(1)
        val limit = 20
        val recent = list.take(limit)
        // Calculate percentages based on recent 20 or all? Brief said "recent history".
        // Let's do breakdown of recent 20
        recent.groupingBy { it.status }.eachCount()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    val currentFilter = _filter.asStateFlow()

    fun setFilter(status: SystemStatus?) {
        _filter.value = status
    }

    fun addEntry(content: String, status: SystemStatus, triggers: List<String>) {
        viewModelScope.launch {
            journalRepository.insertEntry(content, status, triggers)
        }
    }

    fun deleteEntry(entry: JournalEntry) {
        viewModelScope.launch {
            journalRepository.deleteEntry(entry)
        }
    }
}
