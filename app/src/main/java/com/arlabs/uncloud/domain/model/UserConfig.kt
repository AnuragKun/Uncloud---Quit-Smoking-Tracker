package com.arlabs.uncloud.domain.model

data class UserConfig(
        val cigarettesPerDay: Int,
        val costPerPack: Double,
        val cigarettesInPack: Int,
        val minutesPerCigarette: Int,
        val quitTimestamp: Long,
        val currency: String = "$",
        val lifetimeCigarettes: Int = 0,
        val lifetimeMoney: Double = 0.0,
        val trackingStartDate: Long = quitTimestamp // Defaults to quitTimestamp for new/existing users
)
