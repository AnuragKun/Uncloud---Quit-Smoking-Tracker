package com.arlabs.uncloud.domain.model

data class Achievement(
        val id: String,
        val title: String,
        val description: String,
        val threshold: Int,
        val type: AchievementType,
        val isUnlocked: Boolean = false,
        val unlockedDate: Long? = null
)

enum class AchievementType {
    STREAK_DAYS,
    CIGARETTES_AVOIDED,
    MONEY_SAVED
}
