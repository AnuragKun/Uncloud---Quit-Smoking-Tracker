package com.arlabs.uncloud.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.arlabs.uncloud.data.local.dao.AchievementDao
import com.arlabs.uncloud.data.local.entity.AchievementEntity

@Database(entities = [AchievementEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun achievementDao(): AchievementDao
}
