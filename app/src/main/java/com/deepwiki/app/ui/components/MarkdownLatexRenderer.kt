package com.deepwiki.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.deepwiki.app.theme.LocalNeoStyle
import com.deepwiki.app.theme.NeoColors

/**
 * Rich Markdown & LaTeX Math Renderer styled for Neo-Brutalism.
 */
@Composable
fun MarkdownLatexRenderer(
    markdownText: String,
    modifier: Modifier = Modifier,
    enableLatex: Boolean = true
) {
    val lines = markdownText.lines()
    val scrollState = rememberScrollState()

    Column(modifier = modifier.fillMaxWidth()) {
        var inCodeBlock = false
        var codeLanguage = ""
        val codeBuffer = StringBuilder()

        var inTable = false
        val tableLines = mutableListOf<String>()

        var inLatexBlock = false
        val latexBuffer = StringBuilder()

        var i = 0
        while (i < lines.size) {
            val line = lines[i]

            // Display LaTeX block: $$ ... $$
            if (enableLatex && line.trim().startsWith("$$") && line.trim().endsWith("$$") && line.trim().length > 4) {
                val formula = line.trim().removePrefix("$$").removeSuffix("$$").trim()
                LatexDisplayBlock(formula)
                i++
                continue
            } else if (enableLatex && line.trim() == "$$") {
                if (inLatexBlock) {
                    LatexDisplayBlock(latexBuffer.toString().trim())
                    latexBuffer.clear()
                    inLatexBlock = false
                } else {
                    inLatexBlock = true
                }
                i++
                continue
            } else if (inLatexBlock) {
                latexBuffer.appendLine(line)
                i++
                continue
            }

            // Code blocks: ```kotlin ... ```
            if (line.trim().startsWith("```")) {
                if (inCodeBlock) {
                    NeoCodeBlock(codeLanguage, codeBuffer.toString().trimEnd())
                    codeBuffer.clear()
                    inCodeBlock = false
                } else {
                    inCodeBlock = true
                    codeLanguage = line.trim().removePrefix("```").trim()
                }
                i++
                continue
            }

            if (inCodeBlock) {
                codeBuffer.appendLine(line)
                i++
                continue
            }

            // Tables: | Header | ... |
            if (line.trim().startsWith("|") && line.trim().endsWith("|")) {
                tableLines.add(line.trim())
                inTable = true
                i++
                continue
            } else if (inTable) {
                NeoTableBlock(tableLines)
                tableLines.clear()
                inTable = false
            }

            // Horizontal Divider
            if (line.trim() == "---" || line.trim() == "***") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .height(2.5.dp)
                        .background(NeoColors.BorderDark)
                )
                i++
                continue
            }

            // Headers
            when {
                line.startsWith("# ") -> {
                    Text(
                        text = parseInlineMarkdown(line.removePrefix("# ")),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = NeoColors.TextPrimary,
                        modifier = Modifier.padding(top = 10.dp, bottom = 4.dp),
                        fontFamily = LocalNeoStyle.current.fontFamily
                    )
                }
                line.startsWith("## ") -> {
                    Text(
                        text = parseInlineMarkdown(line.removePrefix("## ")),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = NeoColors.TextPrimary,
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp),
                        fontFamily = LocalNeoStyle.current.fontFamily
                    )
                }
                line.startsWith("### ") -> {
                    Text(
                        text = parseInlineMarkdown(line.removePrefix("### ")),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeoColors.TextPrimary,
                        modifier = Modifier.padding(top = 6.dp, bottom = 2.dp),
                        fontFamily = LocalNeoStyle.current.fontFamily
                    )
                }
                line.startsWith("> ") -> {
                    // Blockquote
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .background(NeoColors.Yellow.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                            .border(width = 2.dp, color = NeoColors.BorderDark, shape = RoundedCornerShape(4.dp))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = parseInlineMarkdown(line.removePrefix("> ")),
                            fontStyle = FontStyle.Italic,
                            fontSize = 13.sp,
                            color = NeoColors.TextPrimary,
                            fontFamily = LocalNeoStyle.current.fontFamily
                        )
                    }
                }
                line.startsWith("- ") || line.startsWith("* ") -> {
                    // Bullet list
                    Row(modifier = Modifier.padding(vertical = 2.dp, horizontal = 4.dp)) {
                        Text("▪ ", fontWeight = FontWeight.Black, color = NeoColors.BorderDark)
                        Text(
                            text = parseInlineMarkdown(line.substring(2)),
                            fontSize = 14.sp,
                            color = NeoColors.TextPrimary,
                            fontFamily = LocalNeoStyle.current.fontFamily
                        )
                    }
                }
                line.isNotBlank() -> {
                    Text(
                        text = parseInlineMarkdown(line),
                        fontSize = 14.sp,
                        lineHeight = 21.sp,
                        color = NeoColors.TextPrimary,
                        modifier = Modifier.padding(vertical = 2.dp),
                        fontFamily = LocalNeoStyle.current.fontFamily
                    )
                }
                else -> {
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
            i++
        }

        // Flush hanging blocks if any
        if (inCodeBlock && codeBuffer.isNotEmpty()) {
            NeoCodeBlock(codeLanguage, codeBuffer.toString().trimEnd())
        }
        if (inTable && tableLines.isNotEmpty()) {
            NeoTableBlock(tableLines)
        }
        if (inLatexBlock && latexBuffer.isNotEmpty()) {
            LatexDisplayBlock(latexBuffer.toString().trim())
        }
    }
}

/**
 * Neo-Brutalist Code Block with copy action
 */
@Composable
fun NeoCodeBlock(language: String, code: String) {
    val clipboardManager = LocalClipboardManager.current
    val shape = RoundedCornerShape(8.dp)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .border(2.dp, NeoColors.BorderDark, shape)
            .background(NeoColors.SurfaceDark, shape)
            .clip(shape)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(NeoColors.BorderDark)
                .padding(horizontal = 12.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = (language.ifBlank { "code" }).uppercase(),
                color = NeoColors.Yellow,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
            IconButton(
                onClick = { clipboardManager.setText(AnnotatedString(code)) },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Salin Kode",
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
        // Code content
        Text(
            text = code,
            color = Color(0xFFF1F1F1),
            fontFamily = FontFamily.Monospace,
            fontSize = 12.5.sp,
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(12.dp)
        )
    }
}

/**
 * Neo-Brutalist Table Block
 */
@Composable
fun NeoTableBlock(lines: List<String>) {
    val scrollState = rememberScrollState()
    val shape = RoundedCornerShape(6.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .border(2.dp, NeoColors.BorderDark, shape)
            .background(NeoColors.Surface, shape)
            .clip(shape)
            .horizontalScroll(scrollState)
    ) {
        Column(modifier = Modifier.padding(4.dp)) {
            lines.forEachIndexed { index, rawRow ->
                // Skip markdown delimiter line | :--- | :--- |
                if (rawRow.contains("---")) return@forEachIndexed

                val cells = rawRow.split("|")
                    .map { it.trim() }
                    .filter { it.isNotEmpty() }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(if (index == 0) NeoColors.Yellow else Color.Transparent)
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    cells.forEach { cell ->
                        Text(
                            text = parseInlineMarkdown(cell),
                            fontSize = 12.5.sp,
                            fontWeight = if (index == 0) FontWeight.Bold else FontWeight.Normal,
                            color = NeoColors.TextPrimary,
                            modifier = Modifier
                                .widthIn(min = 90.dp)
                                .padding(end = 12.dp)
                        )
                    }
                }
                if (index < lines.size - 1) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(NeoColors.BorderDark.copy(alpha = 0.3f))
                    )
                }
            }
        }
    }
}

/**
 * Neo-Brutalist Display LaTeX Math Block
 */
@Composable
fun LatexDisplayBlock(formula: String) {
    val shape = RoundedCornerShape(8.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .border(2.dp, NeoColors.BorderDark, shape)
            .background(Color(0xFFF3F4F6), shape)
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "LATEX FORMULA",
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = NeoColors.TextSecondary,
            fontFamily = FontFamily.Monospace
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = formula,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.Serif,
            color = NeoColors.BorderDark,
            modifier = Modifier.horizontalScroll(rememberScrollState())
        )
    }
}

/**
 * Parses bold **text**, italic *text*, inline code `code`, and inline LaTeX $math$
 */
fun parseInlineMarkdown(text: String): AnnotatedString {
    return buildAnnotatedString {
        var idx = 0
        while (idx < text.length) {
            when {
                // Inline LaTeX: $...$
                text.startsWith("$", idx) && !text.startsWith("$$", idx) -> {
                    val end = text.indexOf('$', idx + 1)
                    if (end != -1) {
                        pushStyle(SpanStyle(fontFamily = FontFamily.Serif, fontStyle = FontStyle.Italic, fontWeight = FontWeight.Bold))
                        append(text.substring(idx + 1, end))
                        pop()
                        idx = end + 1
                    } else {
                        append(text[idx])
                        idx++
                    }
                }
                // Bold: **text**
                text.startsWith("**", idx) -> {
                    val end = text.indexOf("**", idx + 2)
                    if (end != -1) {
                        pushStyle(SpanStyle(fontWeight = FontWeight.Black))
                        append(text.substring(idx + 2, end))
                        pop()
                        idx = end + 2
                    } else {
                        append(text[idx])
                        idx++
                    }
                }
                // Inline Code: `code`
                text.startsWith("`", idx) -> {
                    val end = text.indexOf('`', idx + 1)
                    if (end != -1) {
                        pushStyle(SpanStyle(
                            fontFamily = FontFamily.Monospace,
                            background = Color(0xFFE2E8F0),
                            fontWeight = FontWeight.SemiBold
                        ))
                        append(" ${text.substring(idx + 1, end)} ")
                        pop()
                        idx = end + 1
                    } else {
                        append(text[idx])
                        idx++
                    }
                }
                // Italic: *text*
                text.startsWith("*", idx) -> {
                    val end = text.indexOf('*', idx + 1)
                    if (end != -1) {
                        pushStyle(SpanStyle(fontStyle = FontStyle.Italic))
                        append(text.substring(idx + 1, end))
                        pop()
                        idx = end + 1
                    } else {
                        append(text[idx])
                        idx++
                    }
                }
                else -> {
                    append(text[idx])
                    idx++
                }
            }
        }
    }
}
