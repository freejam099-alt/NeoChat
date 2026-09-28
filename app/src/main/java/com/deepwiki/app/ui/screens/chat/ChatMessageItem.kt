package com.deepwiki.app.ui.screens.chat

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.deepwiki.app.model.AppSettings
import com.deepwiki.app.model.ChatMessage
import com.deepwiki.app.model.MessageRole
import com.deepwiki.app.theme.LocalNeoStyle
import com.deepwiki.app.theme.NeoColors
import com.deepwiki.app.ui.components.MarkdownLatexRenderer
import com.deepwiki.app.ui.components.ThinkingCoTView
import com.deepwiki.app.ui.components.ToolCallView

@Composable
fun ChatMessageItem(
    message: ChatMessage,
    settings: AppSettings,
    modifier: Modifier = Modifier
) {
    if (message.role == MessageRole.USER) {
        // PENGGUNA: MEMAKAI BUBBLE CHAT (Neo-Brutalist Bubble on Right)
        UserBubbleItem(message = message, modifier = modifier)
    } else {
        // AI ASSISTANT: KAGAK BUBBLE (Full-Width Technical Wiki Document Layout)
        AiDocumentItem(message = message, settings = settings, modifier = modifier)
    }
}

/**
 * User Message: Styled as a crisp Neo-Brutalist Speech Bubble
 */
@Composable
fun UserBubbleItem(
    message: ChatMessage,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(
        topStart = LocalNeoStyle.current.cornerRadius,
        topEnd = 2.dp,
        bottomStart = LocalNeoStyle.current.cornerRadius,
        bottomEnd = LocalNeoStyle.current.cornerRadius
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 48.dp, end = 12.dp, top = 6.dp, bottom = 6.dp),
        horizontalArrangement = Arrangement.End
    ) {
        Box {
            // Hard Shadow
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .offset(x = 3.dp, y = 3.dp)
                    .background(NeoColors.BorderDark, shape)
            )

            // Bubble Surface
            Column(
                modifier = Modifier
                    .border(LocalNeoStyle.current.borderWidth, NeoColors.BorderDark, shape)
                    .background(NeoColors.Yellow, shape)
                    .clip(shape)
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "User",
                        tint = NeoColors.BorderDark,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "YOU",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = NeoColors.BorderDark
                    )
                }

                Text(
                    text = message.content,
                    fontSize = 14.5.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = NeoColors.BorderDark,
                    fontFamily = LocalNeoStyle.current.fontFamily
                )
            }
        }
    }
}

/**
 * AI Response: NOT A BUBBLE ("Sedangkan Ai Kagak")
 * Technical Wiki-Article format spanning full width with brutalist structure.
 */
@Composable
fun AiDocumentItem(
    message: ChatMessage,
    settings: AppSettings,
    modifier: Modifier = Modifier
) {
    // Blinking cursor transition for streaming text
    val infiniteTransition = rememberInfiniteTransition(label = "cursor")
    val cursorAlpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(450),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cursorAlpha"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        // 1. Technical AI Header (Not inside bubble)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Provider Vector Icon
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .border(1.5.dp, NeoColors.BorderDark, RoundedCornerShape(4.dp))
                    .background(message.provider.getAccentColor(), RoundedCornerShape(4.dp))
                    .padding(3.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = message.provider.getIconRes()),
                    contentDescription = message.provider.displayName,
                    tint = NeoColors.BorderDark,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "DEEPWIKI // ${message.provider.displayName.uppercase()}",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Black,
                color = NeoColors.BorderDark,
                fontFamily = LocalNeoStyle.current.fontFamily
            )

            Spacer(modifier = Modifier.weight(1f))

            if (message.isStreaming) {
                Box(
                    modifier = Modifier
                        .background(NeoColors.Cyan, RoundedCornerShape(4.dp))
                        .border(1.dp, NeoColors.BorderDark, RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "STREAMING",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = NeoColors.BorderDark
                    )
                }
            }
        }

        // 2. Chain of Thought (CoT) View if present and enabled in Settings
        if (settings.showWorkflowCoT && message.thinkingContent.isNotBlank()) {
            ThinkingCoTView(
                thinkingText = message.thinkingContent,
                isStreaming = message.isStreaming,
                autoCollapsed = settings.autoCollapseThinking
            )
            Spacer(modifier = Modifier.height(6.dp))
        }

        // 3. Tool Calls (web_search, mcp_tools_use, etc.)
        if (message.toolCalls.isNotEmpty()) {
            message.toolCalls.forEach { toolCall ->
                ToolCallView(toolCall = toolCall)
                Spacer(modifier = Modifier.height(4.dp))
            }
        }

        // 4. Main Body: Full Width Clean Markdown & LaTeX Document (No bubble container!)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                if (settings.markdownRendererEnabled) {
                    MarkdownLatexRenderer(
                        markdownText = message.content,
                        enableLatex = settings.latexRenderingEnabled
                    )
                } else {
                    Text(
                        text = message.content,
                        fontSize = 14.sp,
                        color = NeoColors.TextPrimary,
                        fontFamily = LocalNeoStyle.current.fontFamily
                    )
                }

                // Streaming cursor
                if (message.isStreaming && settings.geminiStyleStreaming) {
                    Box(
                        modifier = Modifier
                            .padding(top = 2.dp)
                            .size(width = 8.dp, height = 16.dp)
                            .background(NeoColors.BorderDark.copy(alpha = cursorAlpha))
                    )
                }
            }
        }

        // Subtle Brutalist bottom divider
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
                .height(1.5.dp)
                .background(NeoColors.BorderDark.copy(alpha = 0.15f))
        )
    }
}
