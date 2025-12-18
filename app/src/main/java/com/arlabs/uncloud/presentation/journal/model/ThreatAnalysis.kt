package com.arlabs.uncloud.presentation.journal.model


enum class RiskLevel {
    LOW, MODERATE, CRITICAL, UNKNOWN
}

data class ThreatAnalysis(
    val riskLevel: RiskLevel = RiskLevel.UNKNOWN,
    val topTriggers: List<String> = emptyList(),
    val riskTrend: List<Int> = emptyList(), // 7-day trend (Oldest -> Newest)
    val peakRiskHour: String? = null,
    val timeDistribution: Map<String, Int> = emptyMap(), // For Heatmap
    val entryCountLast7Days: Int = 0,
    val recentTrend: Trend = Trend.STABLE
)

enum class Trend {
    IMPROVING, DEGRADING, STABLE
}
