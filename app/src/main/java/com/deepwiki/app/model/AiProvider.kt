package com.deepwiki.app.model

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import com.deepwiki.app.R
import com.deepwiki.app.theme.NeoColors
import kotlinx.serialization.Serializable

@Serializable
enum class AiProvider(
    val id: String,
    val displayName: String,
    val defaultBaseUrl: String,
    val defaultModel: String,
    val requiresApiKey: Boolean = true,
    val supportsThinking: Boolean = false
) {
    OLLAMA(
        id = "ollama",
        displayName = "Ollama Cloud",
        defaultBaseUrl = "https://ollama.example.com/v1",
        defaultModel = "llama3.3:70b",
        requiresApiKey = false
    ),
    QWEN(
        id = "qwen",
        displayName = "Qwen Alibaba",
        defaultBaseUrl = "https://dashscope-intl.aliyuncs.com/compatible-mode/v1",
        defaultModel = "qwen-max-latest",
        supportsThinking = true
    ),
    OPENROUTER(
        id = "openrouter",
        displayName = "OpenRouter",
        defaultBaseUrl = "https://openrouter.ai/api/v1",
        defaultModel = "deepseek/deepseek-r1",
        supportsThinking = true
    ),
    GROQ(
        id = "groq",
        displayName = "Groq LPU",
        defaultBaseUrl = "https://api.groq.com/openai/v1",
        defaultModel = "deepseek-r1-distill-llama-70b",
        supportsThinking = true
    ),
    GROK(
        id = "grok",
        displayName = "xAI Grok",
        defaultBaseUrl = "https://api.x.ai/v1",
        defaultModel = "grok-2-latest",
        supportsThinking = false
    ),
    NVIDIA(
        id = "nvidia",
        displayName = "Nvidia NIM",
        defaultBaseUrl = "https://integrate.api.nvidia.com/v1",
        defaultModel = "deepseek-ai/deepseek-r1",
        supportsThinking = true
    ),
    GEMINI(
        id = "gemini",
        displayName = "Google Gemini",
        defaultBaseUrl = "https://generativelanguage.googleapis.com/v1beta/openai",
        defaultModel = "gemini-2.0-flash",
        supportsThinking = true
    ),
    OPENAI(
        id = "openai",
        displayName = "OpenAI",
        defaultBaseUrl = "https://api.openai.com/v1",
        defaultModel = "gpt-4o",
        supportsThinking = true
    ),
    DEEPSEEK(
        id = "deepseek",
        displayName = "DeepSeek",
        defaultBaseUrl = "https://api.deepseek.com/v1",
        defaultModel = "deepseek-reasoner",
        supportsThinking = true
    ),
    ANTHROPIC(
        id = "anthropic",
        displayName = "Anthropic Claude",
        defaultBaseUrl = "https://api.anthropic.com/v1",
        defaultModel = "claude-3-7-sonnet-20250219",
        supportsThinking = true
    );

    @DrawableRes
    fun getIconRes(): Int = when (this) {
        OLLAMA -> R.drawable.ic_ollama
        QWEN -> R.drawable.ic_qwen
        OPENROUTER -> R.drawable.ic_openrouter
        GROQ -> R.drawable.ic_groq
        GROK -> R.drawable.ic_grok
        NVIDIA -> R.drawable.ic_nvidia
        GEMINI -> R.drawable.ic_gemini
        OPENAI -> R.drawable.ic_openai
        DEEPSEEK -> R.drawable.ic_deepseek
        ANTHROPIC -> R.drawable.ic_anthropic
    }

    fun getAccentColor(): Color = when (this) {
        OLLAMA -> NeoColors.Ollama
        QWEN -> NeoColors.Qwen
        OPENROUTER -> NeoColors.OpenRouter
        GROQ -> NeoColors.Groq
        GROK -> NeoColors.Grok
        NVIDIA -> NeoColors.Nvidia
        GEMINI -> NeoColors.Gemini
        OPENAI -> NeoColors.OpenAI
        DEEPSEEK -> NeoColors.DeepSeek
        ANTHROPIC -> NeoColors.Anthropic
    }
}
