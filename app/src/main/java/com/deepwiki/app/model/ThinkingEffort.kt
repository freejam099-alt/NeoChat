package com.deepwiki.app.model

enum class ThinkingEffort(
    val code: String,
    val label: String,
    val maxBudgetTokens: Int
) {
    OFF("off", "OFF", 0),
    LOW("low", "LOW", 1024),
    MEDIUM("medium", "MED", 4096),
    HIGH("high", "HIGH", 16384)
}
