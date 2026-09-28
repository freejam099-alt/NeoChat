package com.deepwiki.app.ui.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.deepwiki.app.R
import com.deepwiki.app.model.AiProvider
import com.deepwiki.app.model.ThinkingEffort
import com.deepwiki.app.theme.LocalNeoStyle
import com.deepwiki.app.theme.NeoColors
import com.deepwiki.app.ui.components.NeoTabBar
import com.deepwiki.app.viewmodel.ChatUiState

@Composable
fun ChatScreen(
    uiState: ChatUiState,
    onSelectProvider: (AiProvider) -> Unit,
    onSendMessage: (String) -> Unit,
    onInputChanged: (String) -> Unit,
    onStopStreaming: () -> Unit,
    onToggleWebSearch: () -> Unit,
    onThinkingEffortChanged: (ThinkingEffort) -> Unit,
    onFetchModels: (AiProvider) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    // Auto-scroll to bottom on new message / streaming chunk
    LaunchedEffect(uiState.messages.size, uiState.messages.lastOrNull()?.content?.length) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }

    val activeModel = uiState.settings.providerConfigs[uiState.activeProvider.id]?.selectedModel
        ?.ifBlank { uiState.activeProvider.defaultModel }
        ?: uiState.activeProvider.defaultModel

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NeoColors.Background)
    ) {
        // Top Provider Tab Bar
        NeoTabBar(
            selectedProvider = uiState.activeProvider,
            onProviderSelected = onSelectProvider,
            modifier = Modifier.background(NeoColors.Surface)
        )

        // Subheader: Active Model info + Model Fetcher Action (Vector Icons only)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(width = 1.dp, color = NeoColors.BorderDark.copy(alpha = 0.2f))
                .background(Color(0xFFF6F3EC))
                .padding(horizontal = 14.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "MODEL:",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    color = NeoColors.BorderDark
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = activeModel,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = NeoColors.BorderDark
                )
            }

            // Quick Fetch Models Vector Button
            Row(
                modifier = Modifier
                    .border(1.dp, NeoColors.BorderDark, RoundedCornerShape(4.dp))
                    .background(NeoColors.Surface, RoundedCornerShape(4.dp))
                    .clickable { onFetchModels(uiState.activeProvider) }
                    .padding(horizontal = 6.dp, vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_model_fetch),
                    contentDescription = "Fetch Model",
                    tint = NeoColors.BorderDark,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (uiState.isFetchingModels) "FETCHING..." else "FETCH MODEL",
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = NeoColors.BorderDark
                )
            }
        }

        // Hard Border Divider
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(LocalNeoStyle.current.borderWidth)
                .background(NeoColors.BorderDark)
        )

        // Chat Messages List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(top = 8.dp, bottom = 12.dp)
        ) {
            items(
                items = uiState.messages,
                key = { it.id }
            ) { message ->
                ChatMessageItem(
                    message = message,
                    settings = uiState.settings
                )
            }
        }

        // Chat Input Bar with Thinking Effort and Vector icons
        ChatInputBar(
            inputText = uiState.inputText,
            onInputChanged = onInputChanged,
            onSendMessage = onSendMessage,
            onStopStreaming = onStopStreaming,
            isStreaming = uiState.isStreaming,
            webSearchActive = uiState.webSearchActive,
            onToggleWebSearch = onToggleWebSearch,
            thinkingEffort = uiState.settings.thinkingEffort,
            onThinkingEffortChanged = onThinkingEffortChanged,
            activeProvider = uiState.activeProvider
        )

        // Bottom space so Floating Navbar doesn't overlap
        Spacer(modifier = Modifier.height(72.dp))
    }
}
