package com.deepwiki.app.model

import kotlinx.serialization.Serializable

@Serializable
data class McpServerConfig(
    val id: String,
    val name: String,
    val url: String,
    val isEnabled: Boolean = true,
    val headers: Map<String, String> = emptyMap(),
    val tools: List<McpRemoteTool> = emptyList()
)

@Serializable
data class McpRemoteTool(
    val name: String,
    val description: String,
    val inputSchemaJson: String = "{}"
)

@Serializable
data class McpCheckResult(
    val serverId: String,
    val isOnline: Boolean,
    val latencyMs: Long,
    val protocolVersion: String = "2024-11-05",
    val availableToolsCount: Int
)
