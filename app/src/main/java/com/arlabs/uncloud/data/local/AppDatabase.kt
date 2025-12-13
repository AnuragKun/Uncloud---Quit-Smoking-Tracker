package com.arlabs.uncloud.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.arlabs.uncloud.data.local.dao.AchievementDao
import com.arlabs.uncloud.data.local.entity.AchievementEntity

@Database(entities = [AchievementEntity::class, com.arlabs.uncloud.domain.model.Breach::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun achievementDao(): AchievementDao
    abstract fun breachDao(): com.arlabs.uncloud.data.local.dao.BreachDao
}
