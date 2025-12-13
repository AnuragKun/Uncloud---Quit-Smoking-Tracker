package com.arlabs.uncloud.domain.model

import androidx.compose.ui.graphics.Color

data class Rank(
    val title: String,
    val daysRequired: Int,
    val description: String, // The biological/mental milestone
    val color: Color
)

// The Full Clarity Protocol
val rankSystem = listOf(
    Rank("The Initiate", 0, "Protocol started. Carbon Monoxide still present.", Color(0xFF757575)),
    Rank("The Breather", 3, "Nicotine 100% eliminated. Bronchial tubes relaxing.", Color(0xFF64B5F6)),
    Rank("The Resilient", 7, "Physical cravings peak and drop. Blood oxygen normal.", Color(0xFF4DB6AC)),
    Rank("The Reclaimer", 14, "Circulation returns to gums & teeth. Senses recovering.", Color(0xFF00E5FF)), // Brand Cyan
    Rank("The Vanguard", 30, "Lung function up 30%. The monthly cycle is broken.", Color(0xFF2E7D32)),
    Rank("The Architect", 90, "Dopamine receptors normalized. Reward system rebuilt.", Color(0xFFFFD700)),
    Rank("The Sovereign", 180, "Cilia regrown. Lung cleaning restored.", Color(0xFF9C27B0)),
    Rank("The Unclouded", 365, "Heart disease risk halved. Total autonomy.", Color(0xFFFFFFFF))
)
