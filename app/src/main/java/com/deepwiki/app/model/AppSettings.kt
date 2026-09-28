package com.deepwiki.app.model

import com.deepwiki.app.theme.CustomFontOption
import kotlinx.serialization.Serializable

@Serializable
data class ProviderConfig(
    val providerId: String,
    val apiKey: String = "",
    val baseUrl: String = "",
    val selectedModel: String = "",
    val isCustomEndpoint: Boolean = false,
    val availableModels: List<String> = emptyList()
)

data class AppSettings(
    // AI Providers credentials & model config
    val providerConfigs: Map<String, ProviderConfig> = emptyMap(),
    val activeProvider: AiProvider = AiProvider.OPENROUTER,

    // Reasoning & Thinking Effort
    val thinkingEffort: ThinkingEffort = ThinkingEffort.MEDIUM,

    // Settings features requested
    val mcpEnabled: Boolean = true,
    val webSearchEnabled: Boolean = true,
    val webSearchEngine: String = "DuckDuckGo", // DuckDuckGo, Brave, SearXNG
    val showWorkflowCoT: Boolean = true, // Menampilkan Alur Kerja AI (CoT)
    val showThinkingContent: Boolean = true, // Show Thinking Content
    val autoCollapseThinking: Boolean = true, // Auto Collapse Thinking
    val geminiStyleStreaming: Boolean = true, // Streaming Text Mirip Gemini
    val markdownRendererEnabled: Boolean = true, // Markdown renderer
    val latexRenderingEnabled: Boolean = true, // LateX Rendering
    val customFont: CustomFontOption = CustomFontOption.NEO_GROTESK, // Custom Font

    // UI Customization
    val cornerSquircleRatio: Float = 0.6f, // Sudut Corner 60% kotak 40% sudut bulat
    val borderWidthDp: Float = 2.5f,
    val accentColorHex: String = "#FFE600", // Yellow default Neo-Brutalist
    val userBubbleEnabled: Boolean = true, // Pengguna Memakai Bubble Chat
    val aiFullWidthNoBubble: Boolean = true // Sedangkan AI Kagak (Full-width clean technical wiki layout)
)
