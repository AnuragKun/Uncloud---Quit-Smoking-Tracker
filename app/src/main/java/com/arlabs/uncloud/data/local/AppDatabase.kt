package com.arlabs.uncloud.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.arlabs.uncloud.data.local.dao.AchievementDao
import com.arlabs.uncloud.data.local.entity.AchievementEntity

import androidx.room.TypeConverters

@Database(entities = [AchievementEntity::class, com.arlabs.uncloud.domain.model.Breach::class, com.arlabs.uncloud.data.local.entity.JournalEntry::class], version = 4, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun achievementDao(): AchievementDao
    abstract fun breachDao(): com.arlabs.uncloud.data.local.dao.BreachDao
    abstract fun journalDao(): com.arlabs.uncloud.data.local.dao.JournalDao
}
