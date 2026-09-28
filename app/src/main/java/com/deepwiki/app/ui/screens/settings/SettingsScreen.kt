package com.deepwiki.app.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.deepwiki.app.R
import com.deepwiki.app.model.AiProvider
import com.deepwiki.app.model.AppSettings
import com.deepwiki.app.model.ThinkingEffort
import com.deepwiki.app.theme.CustomFontOption
import com.deepwiki.app.theme.LocalNeoStyle
import com.deepwiki.app.theme.NeoButton
import com.deepwiki.app.theme.NeoColors

@Composable
fun SettingsScreen(
    settings: AppSettings,
    onUpdateSettings: (AppSettings) -> Unit,
    onFetchModels: (AiProvider, (List<String>) -> Unit) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    var selectedProviderForConfig by remember { mutableStateOf<AiProvider?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp)
    ) {
        item {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    tint = NeoColors.BorderDark,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "PENGATURAN SISTEM",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = NeoColors.BorderDark,
                    fontFamily = LocalNeoStyle.current.fontFamily
                )
            }
            Text(
                text = "Konfigurasi DeepWiki AI, MCP, Rendering Engine, dan Neo-Brutalist Theme.",
                fontSize = 12.sp,
                color = NeoColors.TextSecondary
            )
        }

        // SECTION 1: AI REASONING & THINKING EFFORT
        item {
            SettingsCategoryHeader("ALUR KERJA AI & REASONING EFFORT")
            SettingsCard {
                // Thinking Effort Selector
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_thinking_effort),
                                contentDescription = null,
                                tint = Color.Unspecified,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Kekuatan Penalaran (Thinking Effort)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = NeoColors.BorderDark
                            )
                        }
                        Text(
                            text = settings.thinkingEffort.label,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            color = NeoColors.BorderDark
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        ThinkingEffort.values().forEach { effort ->
                            val isSelected = settings.thinkingEffort == effort
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onUpdateSettings(settings.copy(thinkingEffort = effort)) }
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = NeoColors.BorderDark,
                                        shape = RoundedCornerShape(6.dp)
                                    )
                                    .background(
                                        if (isSelected) NeoColors.Yellow else NeoColors.Surface,
                                        RoundedCornerShape(6.dp)
                                    )
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = effort.label,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = NeoColors.BorderDark
                                )
                            }
                        }
                    }
                }

                SettingsDivider()

                // Menampilkan Alur Kerja AI (CoT)
                SettingsSwitchRow(
                    title = "Menampilkan Alur Kerja AI (CoT)",
                    subtitle = "Tampilkan tahapan Chain-of-Thought AI sebelum memberikan jawaban",
                    checked = settings.showWorkflowCoT,
                    onCheckedChange = { onUpdateSettings(settings.copy(showWorkflowCoT = it)) }
                )

                SettingsDivider()

                // Show Thinking Content
                SettingsSwitchRow(
                    title = "Show Thinking Content",
                    subtitle = "Ekstrak dan tampilkan reasoning tokens (<think> tags)",
                    checked = settings.showThinkingContent,
                    onCheckedChange = { onUpdateSettings(settings.copy(showThinkingContent = it)) }
                )

                SettingsDivider()

                // Auto Collapse Thinking
                SettingsSwitchRow(
                    title = "Auto Collapse Thinking",
                    subtitle = "Tutup blok pemikiran otomatis setelah streaming selesai",
                    checked = settings.autoCollapseThinking,
                    onCheckedChange = { onUpdateSettings(settings.copy(autoCollapseThinking = it)) }
                )

                SettingsDivider()

                // Streaming Text Mirip Gemini
                SettingsSwitchRow(
                    title = "Streaming Text Mirip Gemini",
                    subtitle = "Transmisi token bertahap halus dengan kursor berkedip",
                    checked = settings.geminiStyleStreaming,
                    onCheckedChange = { onUpdateSettings(settings.copy(geminiStyleStreaming = it)) }
                )
            }
        }

        // SECTION 2: TOOLS & MCP
        item {
            SettingsCategoryHeader("INTEGRASI TOOLS & PROTOKOL MCP")
            SettingsCard {
                // Web Search
                SettingsSwitchRow(
                    title = "Fitur Web Search",
                    subtitle = "Izinkan AI mencari data terkini di web secara otonom",
                    checked = settings.webSearchEnabled,
                    onCheckedChange = { onUpdateSettings(settings.copy(webSearchEnabled = it)) }
                )

                SettingsDivider()

                // MCP (Model Context Protocol)
                SettingsSwitchRow(
                    title = "Model Context Protocol (MCP)",
                    subtitle = "Aktifkan integrasi tool context eksternal (mcp_check, mcp_use, mcp_tools_use)",
                    checked = settings.mcpEnabled,
                    onCheckedChange = { onUpdateSettings(settings.copy(mcpEnabled = it)) }
                )
            }
        }

        // SECTION 3: RENDERING ENGINE (Markdown & LaTeX)
        item {
            SettingsCategoryHeader("MESIN DOKUMEN & RENDERING")
            SettingsCard {
                // Markdown Renderer
                SettingsSwitchRow(
                    title = "Markdown Renderer",
                    subtitle = "Format teks dengan header, tabel brutalist, daftar, dan blok kode",
                    checked = settings.markdownRendererEnabled,
                    onCheckedChange = { onUpdateSettings(settings.copy(markdownRendererEnabled = it)) }
                )

                SettingsDivider()

                // LaTeX Rendering
                SettingsSwitchRow(
                    title = "LaTeX Math Rendering",
                    subtitle = "Render ekspresi matematika inline ($...$) dan block ($$...$$)",
                    checked = settings.latexRenderingEnabled,
                    onCheckedChange = { onUpdateSettings(settings.copy(latexRenderingEnabled = it)) }
                )
            }
        }

        // SECTION 4: CUSTOMISASI UI & TYPOGRAPHY
        item {
            SettingsCategoryHeader("KUSTOMISASI UI NEO-BRUTALISM")
            SettingsCard {
                // Custom Font Selector
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Gaya Font (Custom Font)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = NeoColors.BorderDark
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        CustomFontOption.values().forEach { font ->
                            val isSelected = settings.customFont == font
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onUpdateSettings(settings.copy(customFont = font)) }
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = NeoColors.BorderDark,
                                        shape = RoundedCornerShape(6.dp)
                                    )
                                    .background(
                                        if (isSelected) NeoColors.Yellow else NeoColors.Surface,
                                        RoundedCornerShape(6.dp)
                                    )
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = font.displayName,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Normal,
                                    fontFamily = font.fontFamily,
                                    color = NeoColors.BorderDark
                                )
                            }
                        }
                    }
                }

                SettingsDivider()

                // Corner Radius Ratio: 60% Square, 40% Round
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Rasio Sudut (60% Kotak : 40% Bulat)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = NeoColors.BorderDark
                        )
                        Text(
                            text = "10 dp",
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            color = NeoColors.BorderDark
                        )
                    }
                    Text(
                        text = "Mempertahankan estetika squircle tajam Neo-Brutalist",
                        fontSize = 11.sp,
                        color = NeoColors.TextSecondary
                    )
                }

                SettingsDivider()

                // Bubble Rule indicator (Free of emojis)
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Format Tampilan Percakapan",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = NeoColors.BorderDark
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "[PENGGUNA] Memakai Bubble Chat Neo-Brutalist di kanan.\n[AI ASSISTANT] Full-width Technical Wiki Document (Bukan bubble).",
                        fontSize = 11.5.sp,
                        color = NeoColors.TextPrimary
                    )
                }
            }
        }

        // SECTION 5: PROVIDER CREDENTIALS & ENDPOINTS
        item {
            SettingsCategoryHeader("KREDENSIAL PROVIDER AI (10 PROVIDER)")
            SettingsCard {
                AiProvider.values().forEachIndexed { index, provider ->
                    val providerConfig = settings.providerConfigs[provider.id]
                    val currentModel = providerConfig?.selectedModel?.ifBlank { provider.defaultModel } ?: provider.defaultModel

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedProviderForConfig = provider }
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .border(1.5.dp, NeoColors.BorderDark, RoundedCornerShape(4.dp))
                                    .background(provider.getAccentColor(), RoundedCornerShape(4.dp))
                                    .padding(4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(id = provider.getIconRes()),
                                    contentDescription = null,
                                    tint = NeoColors.BorderDark,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = provider.displayName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = NeoColors.BorderDark
                                )
                                Text(
                                    text = currentModel,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = NeoColors.TextSecondary
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = NeoColors.BorderDark,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    if (index < AiProvider.values().size - 1) {
                        SettingsDivider()
                    }
                }
            }
        }
    }

    // Modal Provider Config Dialog with Model Fetcher
    selectedProviderForConfig?.let { provider ->
        val existingConfig = settings.providerConfigs[provider.id]
        ProviderConfigDialog(
            provider = provider,
            currentApiKey = existingConfig?.apiKey ?: "",
            currentBaseUrl = existingConfig?.baseUrl ?: provider.defaultBaseUrl,
            currentModel = existingConfig?.selectedModel ?: provider.defaultModel,
            cachedModels = existingConfig?.availableModels ?: emptyList(),
            onDismiss = { selectedProviderForConfig = null },
            onFetchModels = { callback ->
                onFetchModels(provider, callback)
            },
            onSave = { apiKey, baseUrl, model, modelsList ->
                val newMap = settings.providerConfigs.toMutableMap()
                newMap[provider.id] = com.deepwiki.app.model.ProviderConfig(
                    providerId = provider.id,
                    apiKey = apiKey,
                    baseUrl = baseUrl,
                    selectedModel = model,
                    availableModels = modelsList
                )
                onUpdateSettings(settings.copy(providerConfigs = newMap))
                selectedProviderForConfig = null
            }
        )
    }
}

@Composable
fun SettingsCategoryHeader(title: String) {
    Text(
        text = title,
        fontWeight = FontWeight.Black,
        fontSize = 12.sp,
        color = NeoColors.BorderDark,
        modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
    )
}

@Composable
fun SettingsCard(content: @Composable ColumnScope.() -> Unit) {
    val shape = RoundedCornerShape(LocalNeoStyle.current.cornerRadius)
    Box(modifier = Modifier.fillMaxWidth()) {
        // Shadow
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = 3.dp, y = 3.dp)
                .background(NeoColors.BorderDark, shape)
        )
        // Card Surface
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(LocalNeoStyle.current.borderWidth, NeoColors.BorderDark, shape)
                .background(NeoColors.Surface, shape)
                .clip(shape),
            content = content
        )
    }
}

@Composable
fun SettingsSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 13.5.sp,
                color = NeoColors.BorderDark
            )
            Text(
                text = subtitle,
                fontSize = 11.5.sp,
                color = NeoColors.TextSecondary
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = NeoColors.BorderDark,
                checkedTrackColor = NeoColors.Yellow,
                uncheckedThumbColor = Color.Gray,
                uncheckedTrackColor = Color(0xFFEEEEEE)
            )
        )
    }
}

@Composable
fun SettingsDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(NeoColors.BorderDark.copy(alpha = 0.15f))
    )
}

@Composable
fun ProviderConfigDialog(
    provider: AiProvider,
    currentApiKey: String,
    currentBaseUrl: String,
    currentModel: String,
    cachedModels: List<String>,
    onDismiss: () -> Unit,
    onFetchModels: ((List<String>) -> Unit) -> Unit,
    onSave: (apiKey: String, baseUrl: String, model: String, availableModels: List<String>) -> Unit
) {
    var apiKey by remember { mutableStateOf(currentApiKey) }
    var baseUrl by remember { mutableStateOf(currentBaseUrl) }
    var model by remember { mutableStateOf(currentModel) }
    var availableModels by remember { mutableStateOf(cachedModels) }
    var isFetching by remember { mutableStateOf(false) }
    val shape = RoundedCornerShape(12.dp)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = provider.getIconRes()),
                    contentDescription = null,
                    tint = NeoColors.BorderDark,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Konfigurasi ${provider.displayName}",
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "API Key:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                OutlinedTextField(
                    value = apiKey,
                    onValueChange = { apiKey = it },
                    placeholder = { Text("Masukkan API Key Anda") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Base URL Endpoint:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                OutlinedTextField(
                    value = baseUrl,
                    onValueChange = { baseUrl = it },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Model ID:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )

                    // FETCH MODEL BUTTON WITH VECTOR ICON
                    Row(
                        modifier = Modifier
                            .border(1.dp, NeoColors.BorderDark, RoundedCornerShape(4.dp))
                            .background(NeoColors.Yellow, RoundedCornerShape(4.dp))
                            .clickable {
                                isFetching = true
                                onFetchModels { fetched ->
                                    isFetching = false
                                    availableModels = fetched
                                    if (fetched.isNotEmpty() && model.isBlank()) {
                                        model = fetched.first()
                                    }
                                }
                            }
                            .padding(horizontal = 6.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_model_fetch),
                            contentDescription = "Fetch Model",
                            tint = NeoColors.BorderDark,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isFetching) "FETCHING..." else "FETCH MODEL",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = NeoColors.BorderDark
                        )
                    }
                }

                OutlinedTextField(
                    value = model,
                    onValueChange = { model = it },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = androidx.compose.ui.text.TextStyle(fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                )

                // List of Fetched Models Chip Selector
                if (availableModels.isNotEmpty()) {
                    Text(
                        text = "Model Tersedia di Server (${availableModels.size}):",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeoColors.TextSecondary
                    )
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(availableModels) { m ->
                            Box(
                                modifier = Modifier
                                    .clickable { model = m }
                                    .border(
                                        width = if (model == m) 1.5.dp else 1.dp,
                                        color = NeoColors.BorderDark,
                                        shape = RoundedCornerShape(4.dp)
                                    )
                                    .background(
                                        if (model == m) NeoColors.Cyan else Color(0xFFEEEEEE),
                                        RoundedCornerShape(4.dp)
                                    )
                                    .padding(horizontal = 6.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = m,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = if (model == m) FontWeight.Bold else FontWeight.Normal,
                                    color = NeoColors.BorderDark
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(apiKey, baseUrl, model, availableModels) },
                colors = ButtonDefaults.buttonColors(containerColor = NeoColors.Yellow)
            ) {
                Text("Simpan", color = NeoColors.BorderDark, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal", color = NeoColors.BorderDark)
            }
        },
        shape = shape,
        containerColor = NeoColors.Surface
    )
}
