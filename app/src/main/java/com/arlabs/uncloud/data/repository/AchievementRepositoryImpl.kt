package com.arlabs.uncloud.data.repository

import com.arlabs.uncloud.data.local.dao.AchievementDao
import com.arlabs.uncloud.data.local.entity.AchievementEntity
import com.arlabs.uncloud.domain.model.Achievement
import com.arlabs.uncloud.domain.repository.AchievementRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AchievementRepositoryImpl @Inject constructor(private val achievementDao: AchievementDao) :
        AchievementRepository {

    override fun getAllAchievements(): Flow<List<Achievement>> {
        return achievementDao.getAllAchievements().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getUnlockedAchievements(): Flow<List<Achievement>> {
        return achievementDao.getUnlockedAchievements().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertAchievements(achievements: List<Achievement>) {
        achievementDao.insertAchievements(achievements.map { AchievementEntity.fromDomain(it) })
    }

    override suspend fun updateAchievement(achievement: Achievement) {
        achievementDao.updateAchievement(AchievementEntity.fromDomain(achievement))
    }
}
