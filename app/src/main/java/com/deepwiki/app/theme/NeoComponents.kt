package com.deepwiki.app.theme

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Neo-Brutalist Container with Hard Drop Shadow and Thick Border.
 * Corner is 60% square, 40% rounded (squircle feeling with 10.dp radius).
 */
@Composable
fun NeoCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = NeoColors.Surface,
    borderColor: Color = NeoColors.BorderDark,
    shadowColor: Color = NeoColors.BorderDark,
    cornerRadius: Dp = LocalNeoStyle.current.cornerRadius,
    borderWidth: Dp = LocalNeoStyle.current.borderWidth,
    shadowOffset: Dp = LocalNeoStyle.current.shadowOffset,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)

    Box(modifier = modifier) {
        // Hard Shadow layer
        if (shadowOffset > 0.dp) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .offset(x = shadowOffset, y = shadowOffset)
                    .background(shadowColor, shape)
            )
        }
        // Surface layer
        Box(
            modifier = Modifier
                .border(borderWidth, borderColor, shape)
                .background(backgroundColor, shape)
                .clip(shape),
            content = content
        )
    }
}

/**
 * Tactile Neo-Brutalist Button with click animation (shadow collapses on press)
 */
@Composable
fun NeoButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = NeoColors.Yellow,
    textColor: Color = NeoColors.BorderDark,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    paddingValues: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val shadowOffset = if (isPressed) 1.dp else 4.dp
    val shape = RoundedCornerShape(LocalNeoStyle.current.cornerRadius)

    Box(
        modifier = modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            )
    ) {
        // Shadow
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = shadowOffset, y = shadowOffset)
                .background(NeoColors.BorderDark, shape)
        )
        // Button surface
        Row(
            modifier = Modifier
                .offset(x = if (isPressed) 2.dp else 0.dp, y = if (isPressed) 2.dp else 0.dp)
                .border(LocalNeoStyle.current.borderWidth, NeoColors.BorderDark, shape)
                .background(if (enabled) backgroundColor else Color(0xFFCCCCCC), shape)
                .clip(shape)
                .padding(paddingValues),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = text,
                    tint = textColor,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                color = textColor,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                fontFamily = LocalNeoStyle.current.fontFamily
            )
        }
    }
}

/**
 * Neo-Brutalist Icon Button
 */
@Composable
fun NeoIconButton(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    backgroundColor: Color = NeoColors.Surface,
    tint: Color = NeoColors.BorderDark,
    size: Dp = 40.dp
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val shape = RoundedCornerShape(LocalNeoStyle.current.cornerRadius)

    Box(
        modifier = modifier
            .size(size)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    ) {
        // Shadow
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = if (isPressed) 1.dp else 3.dp, y = if (isPressed) 1.dp else 3.dp)
                .background(NeoColors.BorderDark, shape)
        )
        // Icon Surface
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = if (isPressed) 2.dp else 0.dp, y = if (isPressed) 2.dp else 0.dp)
                .border(LocalNeoStyle.current.borderWidth, NeoColors.BorderDark, shape)
                .background(backgroundColor, shape)
                .clip(shape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = tint,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/**
 * Neo-Brutalist Badge / Chip
 */
@Composable
fun NeoBadge(
    text: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = NeoColors.Yellow,
    textColor: Color = NeoColors.BorderDark,
    icon: (@Composable () -> Unit)? = null
) {
    val shape = RoundedCornerShape(6.dp)
    Row(
        modifier = modifier
            .border(1.5.dp, NeoColors.BorderDark, shape)
            .background(backgroundColor, shape)
            .padding(horizontal = 8.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            icon()
            Spacer(modifier = Modifier.width(4.dp))
        }
        Text(
            text = text,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = LocalNeoStyle.current.fontFamily
        )
    }
}
