package com.deepwiki.app.ui.screens.chat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.deepwiki.app.R
import com.deepwiki.app.model.AiProvider
import com.deepwiki.app.model.ThinkingEffort
import com.deepwiki.app.theme.LocalNeoStyle
import com.deepwiki.app.theme.NeoColors
import com.deepwiki.app.theme.NeoIconButton

@Composable
fun ChatInputBar(
    inputText: String,
    onInputChanged: (String) -> Unit,
    onSendMessage: (String) -> Unit,
    onStopStreaming: () -> Unit,
    isStreaming: Boolean,
    webSearchActive: Boolean,
    onToggleWebSearch: () -> Unit,
    thinkingEffort: ThinkingEffort,
    onThinkingEffortChanged: (ThinkingEffort) -> Unit,
    activeProvider: AiProvider,
    modifier: Modifier = Modifier
) {
    var showThinkingEffortPopup by remember { mutableStateOf(false) }
    val shape = RoundedCornerShape(LocalNeoStyle.current.cornerRadius)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        // Thinking Effort Selector Popup (Neo-Brutalist Card)
        AnimatedVisibility(
            visible = showThinkingEffortPopup,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp)
            ) {
                // Shadow
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .offset(x = 3.dp, y = 3.dp)
                        .background(NeoColors.BorderDark, RoundedCornerShape(8.dp))
                )
                // Content
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(LocalNeoStyle.current.borderWidth, NeoColors.BorderDark, RoundedCornerShape(8.dp))
                        .background(NeoColors.Surface, RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_thinking_effort),
                                contentDescription = "Thinking Effort",
                                tint = Color.Unspecified,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "THINKING EFFORT (${activeProvider.displayName.uppercase()})",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = NeoColors.BorderDark
                            )
                        }

                        Text(
                            text = "[TUTUP]",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeoColors.BorderDark,
                            modifier = Modifier.clickable { showThinkingEffortPopup = false }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        ThinkingEffort.values().forEach { effort ->
                            val isSelected = effort == thinkingEffort
                            val itemShape = RoundedCornerShape(6.dp)

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        onThinkingEffortChanged(effort)
                                        showThinkingEffortPopup = false
                                    }
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = NeoColors.BorderDark,
                                        shape = itemShape
                                    )
                                    .background(
                                        if (isSelected) NeoColors.Yellow else Color(0xFFF7F7F7),
                                        itemShape
                                    )
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = effort.label,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black,
                                        color = NeoColors.BorderDark
                                    )
                                    Text(
                                        text = if (effort == ThinkingEffort.OFF) "0" else "${effort.maxBudgetTokens / 1024}k tok",
                                        fontSize = 9.sp,
                                        color = NeoColors.TextSecondary,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Main Input Bar with Hard Shadow
        Box(modifier = Modifier.fillMaxWidth()) {
            // Hard Shadow Layer
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .offset(x = 3.dp, y = 3.dp)
                    .background(NeoColors.BorderDark, shape)
            )

            // Inner Input Container
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(LocalNeoStyle.current.borderWidth, NeoColors.BorderDark, shape)
                    .background(NeoColors.Surface, shape)
                    .clip(shape)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                // Top Part: Text Field
                BasicTextField(
                    value = inputText,
                    onValueChange = onInputChanged,
                    textStyle = TextStyle(
                        color = NeoColors.TextPrimary,
                        fontSize = 14.sp,
                        fontFamily = LocalNeoStyle.current.fontFamily
                    ),
                    cursorBrush = SolidColor(NeoColors.BorderDark),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 40.dp, max = 120.dp),
                    decorationBox = { innerTextField ->
                        if (inputText.isEmpty()) {
                            Text(
                                text = "Tanyakan apapun pada DeepWiki...",
                                color = NeoColors.TextSecondary,
                                fontSize = 13.5.sp,
                                fontFamily = LocalNeoStyle.current.fontFamily
                            )
                        }
                        innerTextField()
                    }
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Bottom Action Row INSIDE Input Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // LEFT CORNER (SUDUT BAWAH KIRI DI DALAM INPUT CHAT):
                    // Thinking Effort Icon + Quick Vector Actions
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // 1. Thinking Effort Button (Vector Icon Sudut Bawah Kiri)
                        Box(
                            modifier = Modifier
                                .border(1.5.dp, NeoColors.BorderDark, RoundedCornerShape(6.dp))
                                .background(
                                    if (thinkingEffort != ThinkingEffort.OFF) NeoColors.Yellow.copy(alpha = 0.35f) else Color(0xFFF1F1F1),
                                    RoundedCornerShape(6.dp)
                                )
                                .clickable { showThinkingEffortPopup = !showThinkingEffortPopup }
                                .padding(horizontal = 7.dp, vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_thinking_effort),
                                    contentDescription = "Thinking Effort",
                                    tint = Color.Unspecified,
                                    modifier = Modifier.size(19.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = thinkingEffort.label,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace,
                                    color = NeoColors.BorderDark
                                )
                            }
                        }

                        // 2. Web Search Fetch Icon (Vector ic_web_search)
                        Box(
                            modifier = Modifier
                                .border(1.5.dp, NeoColors.BorderDark, RoundedCornerShape(6.dp))
                                .background(
                                    if (webSearchActive) NeoColors.Cyan else Color(0xFFF1F1F1),
                                    RoundedCornerShape(6.dp)
                                )
                                .clickable(onClick = onToggleWebSearch)
                                .padding(horizontal = 7.dp, vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_web_search),
                                    contentDescription = "Web Search",
                                    tint = Color.Unspecified,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "WEB",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NeoColors.BorderDark
                                )
                            }
                        }

                        // 3. Tool Call Context Vector (Vector ic_tool_call)
                        Box(
                            modifier = Modifier
                                .border(1.5.dp, NeoColors.BorderDark, RoundedCornerShape(6.dp))
                                .background(Color(0xFFF1F1F1), RoundedCornerShape(6.dp))
                                .clickable {
                                    onInputChanged(if (inputText.isBlank()) "@mcp " else "$inputText @mcp ")
                                }
                                .padding(horizontal = 7.dp, vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_tool_call),
                                    contentDescription = "Tool Call",
                                    tint = NeoColors.BorderDark,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "TOOLS",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NeoColors.BorderDark
                                )
                            }
                        }
                    }

                    // RIGHT CORNER: Send or Stop Action Button
                    if (isStreaming) {
                        NeoIconButton(
                            icon = Icons.Default.Stop,
                            onClick = onStopStreaming,
                            backgroundColor = NeoColors.Coral,
                            size = 34.dp
                        )
                    } else {
                        NeoIconButton(
                            icon = Icons.Default.Send,
                            onClick = {
                                if (inputText.isNotBlank()) {
                                    onSendMessage(inputText)
                                }
                            },
                            backgroundColor = if (inputText.isNotBlank()) NeoColors.Yellow else Color(0xFFE0E0E0),
                            size = 34.dp
                        )
                    }
                }
            }
        }
    }
}
