package com.arlabs.uncloud.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "breaches")
data class Breach(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long,
    val trigger: String,
    val notes: String? = null
)
