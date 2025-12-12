package com.arlabs.uncloud.domain.repository

import com.arlabs.uncloud.domain.model.HealthMilestone

interface HealthRepository {
    fun getMilestones(): List<HealthMilestone>
}
