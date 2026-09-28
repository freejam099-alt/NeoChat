package com.deepwiki.app.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class CustomFontOption(val displayName: String, val fontFamily: FontFamily) {
    NEO_GROTESK("Neo Grotesk", FontFamily.Default),
    MONOSPACE("Code Mono", FontFamily.Monospace),
    SERIF("Editorial Serif", FontFamily.Serif),
    SANS_SERIF("Clean Sans", FontFamily.SansSerif)
}

data class NeoStyleConfig(
    val cornerRadius: Dp = 10.dp, // 60% box, 40% rounded squircle feel
    val borderWidth: Dp = 2.5.dp,
    val shadowOffset: Dp = 4.dp,
    val borderColor: Color = NeoColors.BorderDark,
    val fontFamily: FontFamily = FontFamily.Default
)

val LocalNeoStyle = staticCompositionLocalOf { NeoStyleConfig() }

@Composable
fun NeoBrutalistTheme(
    fontOption: CustomFontOption = CustomFontOption.NEO_GROTESK,
    accentColor: Color = NeoColors.Yellow,
    content: @Composable () -> Unit
) {
    val styleConfig = NeoStyleConfig(
        cornerRadius = 10.dp,
        borderWidth = 2.5.dp,
        shadowOffset = 4.dp,
        borderColor = NeoColors.BorderDark,
        fontFamily = fontOption.fontFamily
    )

    val colorScheme = lightColorScheme(
        primary = accentColor,
        onPrimary = NeoColors.BorderDark,
        background = NeoColors.Background,
        onBackground = NeoColors.TextPrimary,
        surface = NeoColors.Surface,
        onSurface = NeoColors.TextPrimary,
        outline = NeoColors.BorderDark
    )

    CompositionLocalProvider(
        LocalNeoStyle provides styleConfig
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            content = content
        )
    }
}
