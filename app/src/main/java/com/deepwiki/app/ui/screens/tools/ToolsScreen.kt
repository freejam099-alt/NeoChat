package com.deepwiki.app.ui.screens.tools

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.deepwiki.app.engine.ToolExecutor
import com.deepwiki.app.model.BuiltinAiTools
import com.deepwiki.app.model.ToolCallInfo
import com.deepwiki.app.model.ToolDefinition
import com.deepwiki.app.theme.LocalNeoStyle
import com.deepwiki.app.theme.NeoButton
import com.deepwiki.app.theme.NeoColors
import kotlinx.coroutines.launch

@Composable
fun ToolsScreen(
    toolExecutor: ToolExecutor,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    var selectedTool by remember { mutableStateOf<ToolDefinition?>(null) }
    var testInputJson by remember { mutableStateOf("") }
    var testOutput by remember { mutableStateOf<ToolCallInfo?>(null) }
    var isRunning by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .border(1.5.dp, NeoColors.BorderDark, RoundedCornerShape(6.dp))
                        .background(NeoColors.Yellow, RoundedCornerShape(6.dp))
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_tool_call),
                        contentDescription = null,
                        tint = NeoColors.BorderDark,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "DEEPWIKI AI TOOLS & MCP",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = NeoColors.BorderDark,
                    fontFamily = LocalNeoStyle.current.fontFamily
                )
            }
            Text(
                text = "Katalog 6 Tool AI bawaan dan server Model Context Protocol yang siap dipanggil secara otonom.",
                fontSize = 12.sp,
                color = NeoColors.TextSecondary
            )
        }

        items(BuiltinAiTools.ALL_TOOLS) { tool ->
            ToolItemCard(
                tool = tool,
                isSelected = selectedTool == tool,
                onClick = {
                    selectedTool = tool
                    testInputJson = when (tool.name) {
                        "web_search" -> "{\"query\":\"Perkembangan DeepSeek R1 2025\"}"
                        "memory_tools" -> "{\"action\":\"list\"}"
                        "mcp_check" -> "{\"server_id\":\"filesystem-mcp\"}"
                        "mcp_list" -> "{}"
                        "mcp_use" -> "{\"server_id\":\"filesystem-mcp\",\"enable\":true}"
                        "mcp_tools_use" -> "{\"server_id\":\"filesystem-mcp\",\"tool_name\":\"list_directory\",\"arguments\":{}}"
                        else -> "{}"
                    }
                    testOutput = null
                }
            )
        }

        // Test Console Section
        selectedTool?.let { tool ->
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "CONSOLE UJI TOOL: ${tool.name}",
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp,
                    color = NeoColors.BorderDark
                )

                val shape = RoundedCornerShape(LocalNeoStyle.current.cornerRadius)
                Box(modifier = Modifier.fillMaxWidth().padding(top = 6.dp)) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .offset(x = 3.dp, y = 3.dp)
                            .background(NeoColors.BorderDark, shape)
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(LocalNeoStyle.current.borderWidth, NeoColors.BorderDark, shape)
                            .background(NeoColors.Surface, shape)
                            .clip(shape)
                            .padding(14.dp)
                    ) {
                        Text(
                            text = "Parameter JSON:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        OutlinedTextField(
                            value = testInputJson,
                            onValueChange = { testInputJson = it },
                            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                            textStyle = androidx.compose.ui.text.TextStyle(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp
                            )
                        )

                        NeoButton(
                            text = if (isRunning) "Mengeksekusi..." else "Jalankan Tool",
                            onClick = {
                                isRunning = true
                                coroutineScope.launch {
                                    testOutput = toolExecutor.execute(tool.name, testInputJson)
                                    isRunning = false
                                }
                            },
                            enabled = !isRunning,
                            backgroundColor = NeoColors.Cyan
                        )

                        testOutput?.let { res ->
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Hasil Eksekusi (${res.executionTimeMs} ms):",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp)
                                    .background(Color(0xFFF3F4F6), RoundedCornerShape(6.dp))
                                    .border(1.dp, NeoColors.BorderDark, RoundedCornerShape(6.dp))
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = res.result ?: "(Kosong)",
                                    fontSize = 11.5.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = NeoColors.TextPrimary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ToolItemCard(
    tool: ToolDefinition,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(LocalNeoStyle.current.cornerRadius)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        // Hard Shadow
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
                .border(
                    width = if (isSelected) 2.5.dp else 1.5.dp,
                    color = NeoColors.BorderDark,
                    shape = shape
                )
                .background(
                    if (isSelected) NeoColors.Yellow.copy(alpha = 0.3f) else NeoColors.Surface,
                    shape
                )
                .clip(shape)
                .padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val iconRes = if (tool.name == "web_search") R.drawable.ic_web_search else R.drawable.ic_tool_call

                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .border(1.dp, NeoColors.BorderDark, RoundedCornerShape(4.dp))
                            .background(NeoColors.Yellow, RoundedCornerShape(4.dp))
                            .padding(3.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = iconRes),
                            contentDescription = null,
                            tint = if (tool.name == "web_search") Color.Unspecified else NeoColors.BorderDark,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = tool.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = NeoColors.BorderDark
                    )
                }

                Text(
                    text = "[UJI COBA]",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = NeoColors.BorderDark
                )
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = tool.description,
                fontSize = 12.sp,
                color = NeoColors.TextSecondary,
                lineHeight = 17.sp
            )

            if (tool.parameters.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    tool.parameters.forEach { param ->
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFE2E8F0), RoundedCornerShape(4.dp))
                                .padding(horizontal = 5.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "${param.name}: ${param.type}",
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                color = NeoColors.TextPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}
