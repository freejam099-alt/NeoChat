package com.deepwiki.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deepwiki.app.engine.AiClient
import com.deepwiki.app.engine.McpManager
import com.deepwiki.app.engine.StreamChunk
import com.deepwiki.app.engine.ToolExecutor
import com.deepwiki.app.model.*
import com.deepwiki.app.prompt.SystemPromptBuilder
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val activeProvider: AiProvider = AiProvider.OPENROUTER,
    val isStreaming: Boolean = false,
    val inputText: String = "",
    val webSearchActive: Boolean = true,
    val isFetchingModels: Boolean = false,
    val settings: AppSettings = AppSettings()
)

class ChatViewModel(
    val mcpManager: McpManager = McpManager(),
    val toolExecutor: ToolExecutor = ToolExecutor(mcpManager),
    val aiClient: AiClient = AiClient()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private var activeStreamJob: Job? = null

    init {
        // Welcome message strictly free of emojis, presenting clean Neo-Brutalist technical style
        val welcomeMessage = ChatMessage(
            role = MessageRole.ASSISTANT,
            provider = AiProvider.OPENROUTER,
            content = """
# DEEPWIKI INTELLIGENCE SYSTEM
Selamat datang di **DeepWiki** — Chatbot AI berarsitektur *Neo-Brutalism* dengan integrasi **10 AI Provider**, **Alur Kerja (CoT)**, dan **Autonomous Tools & MCP**.

### Fitur Utama Siap Pakai:
1. **6 AI Tools Terintegrasi**: `web_search`, `memory_tools`, `mcp_check`, `mcp_list`, `mcp_use`, dan `mcp_tools_use`.
2. **Desain Neo-Brutalism**: Sudut squircle presisi 60% kotak & 40% bulat, border tebal 2.5dp, dan hard drop shadow.
3. **Format Chat Kontras**: 
   - **Pengguna**: Memakai *Bubble Chat* tebal di sisi kanan.
   - **DeepWiki AI**: Format *Full-width Technical Wiki Page* (tanpa bubble) untuk kenyamanan membaca riset teknis dan kode.
4. **Thinking Effort Control**: Atur kekuatan komputasi penalaran AI langsung di sudut kiri bawah bar input chat (OFF, LOW, MED, HIGH).
5. **Fetch Model Langsung**: Tarik daftar model AI terkini dari server penyedia dengan tombol Fetch Model.
6. **LaTeX Mathematical Engine**: Mendukung formula inline seperti ${'$'}E = mc^2${'$'} serta display block:
$$\oint_{\partial \Omega} \mathbf{E} \cdot d\mathbf{l} = -\frac{\partial}{\partial t}\iint_{\Omega} \mathbf{B} \cdot d\mathbf{S}$$

Pilih provider di **Tab Bar** di atas atau mulai ketik instruksi Anda di bawah.
            """.trimIndent(),
            thinkingContent = "[INIT] DeepWiki Core Engine siap. 10 Provider termuat. Menunggu instruksi pengguna...",
            isThinkingCollapsed = false
        )

        _uiState.update { it.copy(messages = listOf(welcomeMessage)) }
    }

    fun setInputText(text: String) {
        _uiState.update { it.copy(inputText = text) }
    }

    fun selectProvider(provider: AiProvider) {
        _uiState.update { it.copy(activeProvider = provider) }
    }

    fun toggleWebSearch() {
        _uiState.update { it.copy(webSearchActive = !it.webSearchActive) }
    }

    fun setThinkingEffort(effort: ThinkingEffort) {
        _uiState.update { state ->
            state.copy(settings = state.settings.copy(thinkingEffort = effort))
        }
    }

    fun updateSettings(newSettings: AppSettings) {
        _uiState.update { it.copy(settings = newSettings) }
    }

    /**
     * Fetch real model list from server provider
     */
    fun fetchModelsForProvider(provider: AiProvider, onComplete: ((List<String>) -> Unit)? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isFetchingModels = true) }
            val cfg = _uiState.value.settings.providerConfigs[provider.id]
            val apiKey = cfg?.apiKey ?: ""
            val baseUrl = if (!cfg?.baseUrl.isNullOrBlank()) cfg!!.baseUrl else provider.defaultBaseUrl

            val models = aiClient.fetchModels(provider, apiKey, baseUrl)

            _uiState.update { state ->
                val newConfigs = state.settings.providerConfigs.toMutableMap()
                val existing = newConfigs[provider.id] ?: ProviderConfig(providerId = provider.id)
                newConfigs[provider.id] = existing.copy(
                    availableModels = models,
                    selectedModel = if (existing.selectedModel.isNotBlank()) existing.selectedModel else (models.firstOrNull() ?: provider.defaultModel)
                )
                state.copy(
                    isFetchingModels = false,
                    settings = state.settings.copy(providerConfigs = newConfigs)
                )
            }
            onComplete?.invoke(models)
        }
    }

    fun stopStreaming() {
        activeStreamJob?.cancel()
        _uiState.update { state ->
            val updated = state.messages.map { msg ->
                if (msg.isStreaming) msg.copy(isStreaming = false) else msg
            }
            state.copy(messages = updated, isStreaming = false)
        }
    }

    fun sendMessage(userText: String) {
        if (userText.isBlank()) return

        val currentState = _uiState.value
        val userMessage = ChatMessage(
            role = MessageRole.USER,
            content = userText.trim(),
            provider = currentState.activeProvider
        )

        val assistantMessageId = UUID.randomUUID().toString()
        val pendingAssistantMessage = ChatMessage(
            id = assistantMessageId,
            role = MessageRole.ASSISTANT,
            content = "",
            thinkingContent = "",
            isStreaming = true,
            provider = currentState.activeProvider
        )

        _uiState.update {
            it.copy(
                messages = it.messages + userMessage + pendingAssistantMessage,
                inputText = "",
                isStreaming = true
            )
        }

        activeStreamJob = viewModelScope.launch {
            val provider = currentState.activeProvider
            val providerCfg = currentState.settings.providerConfigs[provider.id]
            val apiKey = providerCfg?.apiKey ?: ""
            val baseUrl = if (!providerCfg?.baseUrl.isNullOrBlank()) providerCfg!!.baseUrl else provider.defaultBaseUrl
            val model = if (!providerCfg?.selectedModel.isNullOrBlank()) providerCfg!!.selectedModel else provider.defaultModel

            val systemPrompt = SystemPromptBuilder.buildSystemPrompt(
                settings = currentState.settings,
                activeMcpServers = mcpManager.getActiveServers()
            )

            val currentHistory = _uiState.value.messages.dropLast(1) // exclude pending

            aiClient.streamChatCompletion(
                provider = provider,
                apiKey = apiKey,
                baseUrl = baseUrl,
                model = model,
                messages = currentHistory,
                systemPrompt = systemPrompt,
                thinkingEffort = currentState.settings.thinkingEffort
            ).collect { chunk ->
                when (chunk) {
                    is StreamChunk.Thinking -> {
                        updateAssistantMessage(assistantMessageId) { old ->
                            old.copy(thinkingContent = old.thinkingContent + chunk.text)
                        }
                    }
                    is StreamChunk.Content -> {
                        updateAssistantMessage(assistantMessageId) { old ->
                            old.copy(content = old.content + chunk.text)
                        }
                    }
                    is StreamChunk.ToolCallDetected -> {
                        // Autonomous execution of tool
                        val toolResult = toolExecutor.execute(chunk.toolName, chunk.rawJson)
                        updateAssistantMessage(assistantMessageId) { old ->
                            old.copy(
                                toolCalls = old.toolCalls + toolResult,
                                content = old.content + "\n\n> *[Tool ${chunk.toolName} dieksekusi]*\n\n"
                            )
                        }
                    }
                    is StreamChunk.Error -> {
                        updateAssistantMessage(assistantMessageId) { old ->
                            old.copy(content = old.content + "\n\n**Sistem Error**: ${chunk.message}")
                        }
                    }
                    is StreamChunk.Done -> {
                        updateAssistantMessage(assistantMessageId) { old ->
                            old.copy(isStreaming = false)
                        }
                        _uiState.update { it.copy(isStreaming = false) }
                    }
                }
            }
        }
    }

    private fun updateAssistantMessage(id: String, transform: (ChatMessage) -> ChatMessage) {
        _uiState.update { state ->
            val updated = state.messages.map { msg ->
                if (msg.id == id) transform(msg) else msg
            }
            state.copy(messages = updated)
        }
    }
}
