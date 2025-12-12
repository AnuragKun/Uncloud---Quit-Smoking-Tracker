package com.arlabs.uncloud.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.arlabs.uncloud.domain.model.Achievement
import com.arlabs.uncloud.domain.model.AchievementType

@Entity(tableName = "achievements")
data class AchievementEntity(
        @PrimaryKey val id: String,
        val title: String,
        val description: String,
        val threshold: Int,
        val type: AchievementType,
        val isUnlocked: Boolean,
        val unlockedDate: Long?
) {
    fun toDomain(): Achievement {
        return Achievement(
                id = id,
                title = title,
                description = description,
                threshold = threshold,
                type = type,
                isUnlocked = isUnlocked,
                unlockedDate = unlockedDate
        )
    }

    companion object {
        fun fromDomain(achievement: Achievement): AchievementEntity {
            return AchievementEntity(
                    id = achievement.id,
                    title = achievement.title,
                    description = achievement.description,
                    threshold = achievement.threshold,
                    type = achievement.type,
                    isUnlocked = achievement.isUnlocked,
                    unlockedDate = achievement.unlockedDate
            )
        }
    }
}
