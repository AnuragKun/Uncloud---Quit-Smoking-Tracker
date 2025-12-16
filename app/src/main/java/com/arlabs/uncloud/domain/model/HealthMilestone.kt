package com.arlabs.uncloud.domain.model

enum class MilestoneSource {
    WHO,
    LIFESTYLE
}

data class HealthMilestone(
    val durationSeconds: Long,
    val title: String,
    val description: String,
    val source: MilestoneSource = MilestoneSource.LIFESTYLE
)
