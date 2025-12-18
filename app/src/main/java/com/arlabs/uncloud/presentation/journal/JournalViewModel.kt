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
import com.arlabs.uncloud.presentation.journal.model.RiskLevel
import com.arlabs.uncloud.presentation.journal.model.ThreatAnalysis
import com.arlabs.uncloud.presentation.journal.model.Trend
import java.util.Calendar
import java.util.Date

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
        val limit = 20
        val recent = list.take(limit)
        recent.groupingBy { it.status }.eachCount()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    val threatAnalysis = _entries.map { analyzeThreats(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ThreatAnalysis())

    val currentFilter = _filter.asStateFlow()

    fun setFilter(status: SystemStatus?) {
        _filter.value = status
    }

    fun addEntry(content: String, status: SystemStatus, triggers: List<String>, timestamp: Long) {
        viewModelScope.launch {
            journalRepository.insertEntry(content, status, triggers, timestamp)
        }
    }

    fun deleteEntry(entry: JournalEntry) {
        viewModelScope.launch {
            journalRepository.deleteEntry(entry)
        }
    }

    private fun analyzeThreats(entries: List<JournalEntry>): ThreatAnalysis {
        if (entries.isEmpty()) return ThreatAnalysis()

        // 1. Top 3 Triggers
        val allTriggers = entries.flatMap { it.triggers }
        val topTriggers = if (allTriggers.isNotEmpty()) {
            allTriggers.groupingBy { it }
                .eachCount()
                .entries
                .sortedByDescending { it.value }
                .take(3)
                .mapIndexed { index, entry -> "${index + 1}. ${entry.key}" }
        } else emptyList()

        // 2. Peak Risk Hour & Heatmap Distribution
        // Group by 4-hour blocks:
        // 04-08 DAWN, 08-12 MORNING, 12-16 AFTERNOON, 16-20 EVENING, 20-00 NIGHT, 00-04 VOID
        val timeBlocks = entries.map { entry ->
            val cal = Calendar.getInstance()
            cal.time = Date(entry.timestamp)
            val hour = cal.get(Calendar.HOUR_OF_DAY)
            when (hour) {
                in 4..7 -> "DAWN"
                in 8..11 -> "MORNING"
                in 12..15 -> "AFTERNOON"
                in 16..19 -> "EVENING"
                in 20..23 -> "NIGHT"
                else -> "THE VOID"
            }
        }
        
        val distribution = timeBlocks.groupingBy { it }.eachCount()
        val peakTime = if (distribution.isNotEmpty()) {
            distribution.maxByOrNull { it.value }?.key
        } else null

        // 3. Risk Trend (Last 7 Days)
        val calendar = Calendar.getInstance()
        val today = calendar.timeInMillis
        val riskTrend = (0..6).map { daysAgo ->
            calendar.timeInMillis = today
            calendar.add(Calendar.DAY_OF_YEAR, -daysAgo)
            
            // Start of day
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            val start = calendar.timeInMillis
            
            // End of day
            calendar.set(Calendar.HOUR_OF_DAY, 23)
            calendar.set(Calendar.MINUTE, 59)
            val end = calendar.timeInMillis
            
            val dailyEntries = entries.filter { it.timestamp in start..end }
            dailyEntries.sumOf { 
                when(it.status) {
                    com.arlabs.uncloud.data.local.entity.SystemStatus.NOMINAL -> 0
                    com.arlabs.uncloud.data.local.entity.SystemStatus.STABLE -> 1
                    com.arlabs.uncloud.data.local.entity.SystemStatus.VOLATILE -> 3
                    com.arlabs.uncloud.data.local.entity.SystemStatus.CRITICAL -> 5
                }.toInt() // explicit cast if needed, sumOf infers Int
            }
        }.reversed() // Oldest to Newest

        // 4. Risk Level (Simple heuristic: High Critical/Volatile count = Critical)
        val recent = entries.take(10)
        val criticalCount = recent.count { it.status == SystemStatus.CRITICAL || it.status == SystemStatus.VOLATILE }
        val riskLevel = when {
            criticalCount >= 5 -> RiskLevel.CRITICAL
            criticalCount >= 2 -> RiskLevel.MODERATE
            else -> RiskLevel.LOW
        }

        // 4. Trend (Last 3 days vs Previous 3 days)
        // Simple placeholder logic for now
        val trend = Trend.STABLE 

        return ThreatAnalysis(
            riskLevel = riskLevel,
            topTriggers = topTriggers,
            riskTrend = riskTrend,
            peakRiskHour = peakTime,
            timeDistribution = distribution,
            entryCountLast7Days = entries.size, // Simplified
            recentTrend = trend
        )
    }
}
