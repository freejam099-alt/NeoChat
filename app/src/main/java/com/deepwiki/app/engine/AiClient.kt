package com.deepwiki.app.engine

import com.deepwiki.app.model.AiProvider
import com.deepwiki.app.model.ChatMessage
import com.deepwiki.app.model.MessageRole
import com.deepwiki.app.model.ThinkingEffort
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.concurrent.TimeUnit

sealed class StreamChunk {
    data class Content(val text: String) : StreamChunk()
    data class Thinking(val text: String) : StreamChunk()
    data class ToolCallDetected(val toolName: String, val rawJson: String) : StreamChunk()
    data class Error(val message: String) : StreamChunk()
    object Done : StreamChunk()
}

class AiClient(
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()
) {

    /**
     * Fetch Model List directly from provider server
     * Supports OpenAI compatible /v1/models and Ollama /api/tags
     */
    suspend fun fetchModels(
        provider: AiProvider,
        apiKey: String,
        baseUrl: String = provider.defaultBaseUrl
    ): List<String> = withContext(Dispatchers.IO) {
        val cleanBase = baseUrl.trimEnd('/')
        val endpoint = when {
            provider == AiProvider.OLLAMA && cleanBase.contains(":11434") -> "$cleanBase/api/tags"
            cleanBase.endsWith("/v1") -> "$cleanBase/models"
            cleanBase.endsWith("/chat/completions") -> cleanBase.removeSuffix("/chat/completions") + "/models"
            else -> "$cleanBase/models"
        }

        try {
            val reqBuilder = Request.Builder().url(endpoint).get()
            if (apiKey.isNotBlank()) {
                reqBuilder.addHeader("Authorization", "Bearer $apiKey")
            }
            if (provider == AiProvider.OPENROUTER) {
                reqBuilder.addHeader("HTTP-Referer", "https://deepwiki.org")
            }

            client.newCall(reqBuilder.build()).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext getFallbackModels(provider)
                }

                val body = response.body?.string() ?: return@withContext getFallbackModels(provider)
                val json = Json.parseToJsonElement(body).jsonObject

                // Standard /v1/models response { "data": [ { "id": "..." } ] }
                val dataArray = json["data"]?.jsonArray
                if (dataArray != null && dataArray.isNotEmpty()) {
                    return@withContext dataArray.mapNotNull {
                        it.jsonObject["id"]?.jsonPrimitive?.contentOrNull
                    }.sorted()
                }

                // Ollama /api/tags response { "models": [ { "name": "..." } ] }
                val modelsArray = json["models"]?.jsonArray
                if (modelsArray != null && modelsArray.isNotEmpty()) {
                    return@withContext modelsArray.mapNotNull {
                        it.jsonObject["name"]?.jsonPrimitive?.contentOrNull
                    }.sorted()
                }

                getFallbackModels(provider)
            }
        } catch (e: Exception) {
            getFallbackModels(provider)
        }
    }

    private fun getFallbackModels(provider: AiProvider): List<String> {
        return when (provider) {
            AiProvider.OLLAMA -> listOf("llama3.3:70b", "deepseek-r1:8b", "qwen2.5:32b", "mistral:latest", "phi4:latest")
            AiProvider.QWEN -> listOf("qwen-max-latest", "qwen-plus-latest", "qwen-turbo-latest", "qwq-32b-preview")
            AiProvider.OPENROUTER -> listOf("deepseek/deepseek-r1", "anthropic/claude-3.7-sonnet", "openai/gpt-4o", "google/gemini-2.0-flash-001", "meta-llama/llama-3.3-70b-instruct")
            AiProvider.GROQ -> listOf("deepseek-r1-distill-llama-70b", "llama-3.3-70b-versatile", "mixtral-8x7b-32768", "gemma2-9b-it")
            AiProvider.GROK -> listOf("grok-2-latest", "grok-2-vision-latest", "grok-beta")
            AiProvider.NVIDIA -> listOf("deepseek-ai/deepseek-r1", "meta/llama-3.3-70b-instruct", "mistralai/mistral-large-2-instruct")
            AiProvider.GEMINI -> listOf("gemini-2.0-flash", "gemini-2.0-flash-thinking-exp-01-21", "gemini-1.5-pro", "gemini-1.5-flash")
            AiProvider.OPENAI -> listOf("gpt-4o", "gpt-4o-mini", "o3-mini", "o1", "gpt-4-turbo")
            AiProvider.DEEPSEEK -> listOf("deepseek-reasoner", "deepseek-chat")
            AiProvider.ANTHROPIC -> listOf("claude-3-7-sonnet-20250219", "claude-3-5-sonnet-20241022", "claude-3-5-haiku-20241022")
        }
    }

    fun streamChatCompletion(
        provider: AiProvider,
        apiKey: String,
        baseUrl: String = provider.defaultBaseUrl,
        model: String = provider.defaultModel,
        messages: List<ChatMessage>,
        systemPrompt: String,
        thinkingEffort: ThinkingEffort = ThinkingEffort.MEDIUM
    ): Flow<StreamChunk> = flow {
        // If API key is empty and not Ollama local/cloud without auth, provide realistic autonomous DeepWiki simulated response
        if (apiKey.isBlank() && provider.requiresApiKey) {
            emitSimulatedResponse(provider, messages.lastOrNull()?.content ?: "", thinkingEffort)
            emit(StreamChunk.Done)
            return@flow
        }

        try {
            val endpoint = if (baseUrl.endsWith("/chat/completions")) baseUrl else "$baseUrl/chat/completions"
            val jsonMessages = buildJsonArray {
                add(buildJsonObject {
                    put("role", "system")
                    put("content", systemPrompt)
                })
                messages.takeLast(10).forEach { msg ->
                    add(buildJsonObject {
                        put("role", if (msg.role == MessageRole.USER) "user" else "assistant")
                        put("content", msg.content)
                    })
                }
            }

            val requestBodyJson = buildJsonObject {
                put("model", model)
                put("messages", jsonMessages)
                put("stream", true)
                put("temperature", 0.7)

                // Add reasoning effort parameter for reasoning models (e.g., o-series, Claude thinking, DeepSeek)
                if (thinkingEffort != ThinkingEffort.OFF && provider.supportsThinking) {
                    put("reasoning_effort", thinkingEffort.code)
                }
            }.toString()

            val requestBuilder = Request.Builder()
                .url(endpoint)
                .post(requestBodyJson.toRequestBody("application/json".toMediaType()))
                .addHeader("Content-Type", "application/json")

            if (apiKey.isNotBlank()) {
                requestBuilder.addHeader("Authorization", "Bearer $apiKey")
            }

            // Provider specific headers
            if (provider == AiProvider.OPENROUTER) {
                requestBuilder.addHeader("HTTP-Referer", "https://deepwiki.org")
                requestBuilder.addHeader("X-Title", "DeepWiki Android")
            } else if (provider == AiProvider.ANTHROPIC) {
                requestBuilder.addHeader("anthropic-version", "2023-06-01")
            }

            val request = requestBuilder.build()
            val response = client.newCall(request).execute()

            if (!response.isSuccessful) {
                val errorBody = response.body?.string() ?: ""
                emit(StreamChunk.Error("HTTP ${response.code}: $errorBody"))
                emit(StreamChunk.Done)
                return@flow
            }

            val streamReader = BufferedReader(InputStreamReader(response.body!!.byteStream()))
            var line: String?

            var inThinkTag = false

            while (streamReader.readLine().also { line = it } != null) {
                val currentLine = line ?: continue
                if (currentLine.isBlank() || currentLine.startsWith(":")) continue

                if (currentLine.startsWith("data: ")) {
                    val data = currentLine.removePrefix("data: ").trim()
                    if (data == "[DONE]") {
                        break
                    }

                    try {
                        val parsed = Json.parseToJsonElement(data).jsonObject
                        val choices = parsed["choices"]?.jsonArray
                        val delta = choices?.firstOrNull()?.jsonObject?.get("delta")?.jsonObject

                        // Extract reasoning_content (DeepSeek R1 / Groq / Qwen / Nvidia NIM format)
                        val reasoningDelta = delta?.get("reasoning_content")?.jsonPrimitive?.contentOrNull
                        if (!reasoningDelta.isNullOrEmpty()) {
                            emit(StreamChunk.Thinking(reasoningDelta))
                        }

                        val contentDelta = delta?.get("content")?.jsonPrimitive?.contentOrNull
                        if (!contentDelta.isNullOrEmpty()) {
                            // Check for <think> and </think> tags
                            if (contentDelta.contains("<think>")) {
                                inThinkTag = true
                                val afterThink = contentDelta.substringAfter("<think>")
                                if (afterThink.contains("</think>")) {
                                    val thinkPart = afterThink.substringBefore("</think>")
                                    val rest = afterThink.substringAfter("</think>")
                                    inThinkTag = false
                                    if (thinkPart.isNotEmpty()) emit(StreamChunk.Thinking(thinkPart))
                                    if (rest.isNotEmpty()) emit(StreamChunk.Content(rest))
                                } else {
                                    if (afterThink.isNotEmpty()) emit(StreamChunk.Thinking(afterThink))
                                }
                            } else if (inThinkTag) {
                                if (contentDelta.contains("</think>")) {
                                    inThinkTag = false
                                    val thinkPart = contentDelta.substringBefore("</think>")
                                    val rest = contentDelta.substringAfter("</think>")
                                    if (thinkPart.isNotEmpty()) emit(StreamChunk.Thinking(thinkPart))
                                    if (rest.isNotEmpty()) emit(StreamChunk.Content(rest))
                                } else {
                                    emit(StreamChunk.Thinking(contentDelta))
                                }
                            } else {
                                emit(StreamChunk.Content(contentDelta))
                            }
                        }
                    } catch (e: Exception) {
                        // ignore malformed chunk
                    }
                }
            }
            emit(StreamChunk.Done)
        } catch (e: Exception) {
            emit(StreamChunk.Error("Koneksi gagal: ${e.message}"))
            emit(StreamChunk.Done)
        }
    }.flowOn(Dispatchers.IO)

    /**
     * Clean simulated response without emoji characters.
     */
    private suspend fun kotlinx.coroutines.flow.FlowCollector<StreamChunk>.emitSimulatedResponse(
        provider: AiProvider,
        userPrompt: String,
        thinkingEffort: ThinkingEffort
    ) {
        val lower = userPrompt.lowercase()
        val willSearch = lower.contains("cari") || lower.contains("search") || lower.contains("siapa") || lower.contains("terbaru")
        val willMcp = lower.contains("mcp")

        // 1. Thinking phase (Chain of Thought)
        val thinkingSteps = if (thinkingEffort == ThinkingEffort.OFF) {
            emptyList()
        } else {
            listOf(
                "[INIT] Menginisialisasi modul reasoning DeepWiki Core (Effort: ${thinkingEffort.label})...",
                "[ANALYSIS] Menganalisis parameter query pengguna: \"$userPrompt\".",
                "[TOOLS] Evaluasi pemanggilan alat: search=${willSearch}, mcp=${willMcp}.",
                "[SYNTHESIS] Merumuskan jawaban dalam standar Neo-Brutalist Wiki: headers, matriks perbandingan, dan formulasi LaTeX.",
                "[VERIFY] Format terverifikasi. Memulai transmisi token..."
            )
        }

        for (step in thinkingSteps) {
            emit(StreamChunk.Thinking("$step\n"))
            delay(100)
        }

        // 2. Tool Invocation if triggered
        if (willSearch) {
            emit(StreamChunk.ToolCallDetected(
                toolName = "web_search",
                rawJson = "{\"query\":\"$userPrompt\",\"max_results\":3}"
            ))
            delay(300)
        } else if (willMcp) {
            emit(StreamChunk.ToolCallDetected(
                toolName = "mcp_check",
                rawJson = "{\"server_id\":\"filesystem-mcp\"}"
            ))
            delay(300)
        }

        // 3. Gemini-like Streaming Response text without emojis
        val responseText = buildString {
            appendLine("# DeepWiki Intelligence Hub")
            appendLine("Ditenagai oleh arsitektur **${provider.displayName}** dalam antarmuka *Neo-Brutalism* berakurasi tinggi.")
            appendLine()
            appendLine("## Analisis & Ringkasan")
            appendLine("DeepWiki memadukan mesin komputasi AI terkini dengan sistem pemanggilan tools mandiri (*Autonomous Function Calling*) dan protokol konteks.")
            appendLine()
            appendLine("### Matriks Konfigurasi & Arsitektur")
            appendLine("| Parameter | Konfigurasi | Status |")
            appendLine("| :--- | :--- | :--- |")
            appendLine("| Provider Aktif | ${provider.displayName} | READY |")
            appendLine("| Alur Kerja (CoT) | Chain-of-Thought | AKTIF |")
            appendLine("| Thinking Effort | ${thinkingEffort.label} (${thinkingEffort.maxBudgetTokens} tokens) | DISETEL |")
            appendLine("| Desain Sudut | 60% Kotak, 40% Bulat | PRESISI |")
            appendLine("| Format Chat | User: Bubble, AI: Full-Width Wiki | SESUAI |")
            appendLine("| Format Matematika | LaTeX Engine $\\mathbf{\\Sigma}$ | SIAP |")
            appendLine()
            appendLine("### Formulasi Matematika (LaTeX)")
            appendLine("Model inferensi probabilistik dalam pemilihan token dan bobot atensi dirumuskan sebagai:")
            appendLine("$$\\mathrm{Attention}(Q, K, V) = \\mathrm{softmax}\\left(\\frac{QK^T}{\\sqrt{d_k}}\\right)V$$")
            appendLine("Dengan estimasi fungsi loss terhadap entropi silang:")
            appendLine("$$\\mathcal{L}_{\\mathrm{DeepWiki}} = -\\sum_{i=1}^{N} y_i \\log(\\hat{y}_i) + \\lambda \\|W\\|^2_2$$")
            appendLine()
            appendLine("---")
            appendLine("> Catatan Sistem: Masukkan API Key Anda di menu Pengaturan untuk terhubung langsung ke server resmi ${provider.displayName}.")
        }

        // Stream word by word like Gemini
        val words = responseText.split(" ")
        for (word in words) {
            emit(StreamChunk.Content("$word "))
            delay(25)
        }
    }
}
