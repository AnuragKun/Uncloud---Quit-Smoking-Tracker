package com.arlabs.uncloud.domain.model

data class UserConfig(
        val cigarettesPerDay: Int,
        val costPerPack: Double,
        val cigarettesInPack: Int,
        val minutesPerCigarette: Int,
        val quitTimestamp: Long,
        val currency: String = "$"
)
