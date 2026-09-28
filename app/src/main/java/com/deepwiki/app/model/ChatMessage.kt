package com.deepwiki.app.model

import java.util.UUID

enum class MessageRole {
    USER,
    ASSISTANT,
    SYSTEM,
    TOOL
}

enum class ToolStatus {
    RUNNING,
    COMPLETED,
    ERROR
}

data class ToolCallInfo(
    val id: String = UUID.randomUUID().toString(),
    val toolName: String,
    val arguments: String,
    val result: String? = null,
    val status: ToolStatus = ToolStatus.RUNNING,
    val executionTimeMs: Long = 0
)

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val role: MessageRole,
    val content: String,
    val thinkingContent: String = "",
    val isThinkingCollapsed: Boolean = true,
    val toolCalls: List<ToolCallInfo> = emptyList(),
    val isStreaming: Boolean = false,
    val provider: AiProvider = AiProvider.OPENROUTER,
    val timestamp: Long = System.currentTimeMillis()
)
