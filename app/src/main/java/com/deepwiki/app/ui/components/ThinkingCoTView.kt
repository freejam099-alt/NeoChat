package com.deepwiki.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import com.deepwiki.app.theme.LocalNeoStyle
import com.deepwiki.app.theme.NeoColors

/**
 * Expandable/Collapsible Neo-Brutalist Chain-of-Thought (CoT) Thinking block.
 * Free of emojis; uses crisp SVG vector icons and technical formatting.
 */
@Composable
fun ThinkingCoTView(
    thinkingText: String,
    isStreaming: Boolean,
    autoCollapsed: Boolean = true,
    modifier: Modifier = Modifier
) {
    if (thinkingText.isBlank()) return

    var isExpanded by remember { mutableStateOf(!autoCollapsed || isStreaming) }

    LaunchedEffect(isStreaming) {
        if (!isStreaming && autoCollapsed) {
            isExpanded = false
        }
    }

    val shape = RoundedCornerShape(LocalNeoStyle.current.cornerRadius)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .border(2.dp, NeoColors.BorderDark, shape)
            .background(Color(0xFFFFF9E6), shape)
            .clip(shape)
    ) {
        // CoT Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded }
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_thinking_effort),
                    contentDescription = "Chain of Thought",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(17.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "ALUR KERJA AI (CoT)",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = LocalNeoStyle.current.fontFamily,
                    color = NeoColors.BorderDark
                )
                if (isStreaming) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .background(NeoColors.Coral, RoundedCornerShape(3.dp))
                            .padding(horizontal = 5.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = "PROSES",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (isExpanded) "[TUTUP]" else "[DETAIL]",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = NeoColors.BorderDark
                )
                Spacer(modifier = Modifier.width(2.dp))
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = NeoColors.BorderDark,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // Expanded Thinking Content
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFEFDF8))
                    .padding(12.dp)
            ) {
                Text(
                    text = thinkingText,
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFF333333)
                )
            }
        }
    }
}
