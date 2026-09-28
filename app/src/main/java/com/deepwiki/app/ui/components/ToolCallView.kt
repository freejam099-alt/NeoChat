package com.deepwiki.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.HourglassTop
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
import com.deepwiki.app.model.ToolCallInfo
import com.deepwiki.app.model.ToolStatus
import com.deepwiki.app.theme.LocalNeoStyle
import com.deepwiki.app.theme.NeoColors

/**
 * Neo-Brutalist Tool Execution Banner using exact Vector Asset ic_tool_call.xml and ic_web_search.xml.
 * No emojis used.
 */
@Composable
fun ToolCallView(
    toolCall: ToolCallInfo,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val shape = RoundedCornerShape(8.dp)

    val (statusColor, statusIcon, statusText) = when (toolCall.status) {
        ToolStatus.RUNNING -> Triple(NeoColors.Cyan, Icons.Default.HourglassTop, "MEMPROSES")
        ToolStatus.COMPLETED -> Triple(NeoColors.Mint, Icons.Default.CheckCircle, "SUKSES")
        ToolStatus.ERROR -> Triple(NeoColors.Coral, Icons.Default.Error, "GAGAL")
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .border(2.dp, NeoColors.BorderDark, shape)
            .background(statusColor.copy(alpha = 0.15f), shape)
            .clip(shape)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(horizontal = 10.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Vector Tool Call Icon requested by user
                val toolIconRes = if (toolCall.toolName == "web_search") {
                    R.drawable.ic_web_search
                } else {
                    R.drawable.ic_tool_call
                }

                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .border(1.dp, NeoColors.BorderDark, RoundedCornerShape(4.dp))
                        .background(NeoColors.Yellow, RoundedCornerShape(4.dp))
                        .padding(3.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = toolIconRes),
                        contentDescription = "Tool Call",
                        tint = if (toolCall.toolName == "web_search") Color.Unspecified else NeoColors.BorderDark,
                        modifier = Modifier.size(17.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "TOOL CALL:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = NeoColors.BorderDark
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = toolCall.toolName,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = NeoColors.BorderDark
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "$statusText (${toolCall.executionTimeMs}ms)",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeoColors.BorderDark
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = statusIcon,
                    contentDescription = null,
                    tint = NeoColors.BorderDark,
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        AnimatedVisibility(visible = expanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .border(width = 1.dp, color = NeoColors.BorderDark.copy(alpha = 0.3f))
                    .padding(8.dp)
            ) {
                Text(
                    text = "Parameter Input JSON:",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeoColors.TextSecondary
                )
                Text(
                    text = toolCall.arguments,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = NeoColors.TextPrimary
                )

                if (toolCall.result != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Output Eksekusi:",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeoColors.TextSecondary
                    )
                    Text(
                        text = toolCall.result,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = NeoColors.TextPrimary
                    )
                }
            }
        }
    }
}
