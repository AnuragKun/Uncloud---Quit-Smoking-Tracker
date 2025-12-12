package com.arlabs.uncloud.domain.repository

import com.arlabs.uncloud.domain.model.Achievement
import kotlinx.coroutines.flow.Flow

interface AchievementRepository {
    fun getAllAchievements(): Flow<List<Achievement>>
    fun getUnlockedAchievements(): Flow<List<Achievement>>
    suspend fun insertAchievements(achievements: List<Achievement>)
    suspend fun updateAchievement(achievement: Achievement)
}
