package com.arlabs.uncloud.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.compose.ui.graphics.Color

@Entity(tableName = "journal_entries")
data class JournalEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long, // Store as epoch millis
    val content: String,
    val status: SystemStatus,
    val triggers: List<String> = emptyList()
)

enum class SystemStatus(val label: String, val colorHex: Long) {
    NOMINAL("NOMINAL", 0xFF00FF9D), // Green
    STABLE("STABLE", 0xFF00E5FF),   // Cyan
    VOLATILE("VOLATILE", 0xFFFFAB40), // Orange
    CRITICAL("CRITICAL", 0xFFFF5252);  // Red
    
    val color: Color
        get() = Color(colorHex)
}
