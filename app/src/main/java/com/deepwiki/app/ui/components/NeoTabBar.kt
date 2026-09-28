package com.deepwiki.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.deepwiki.app.model.AiProvider
import com.deepwiki.app.theme.LocalNeoStyle
import com.deepwiki.app.theme.NeoColors

/**
 * Neo-Brutalist Horizontal Tab Bar for Switching AI Providers
 */
@Composable
fun NeoTabBar(
    selectedProvider: AiProvider,
    onProviderSelected: (AiProvider) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AiProvider.values().forEach { provider ->
            val isSelected = provider == selectedProvider
            val shape = RoundedCornerShape(LocalNeoStyle.current.cornerRadius)

            Box(
                modifier = Modifier
                    .clickable { onProviderSelected(provider) }
            ) {
                // Hard Shadow on selected
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .offset(x = 2.dp, y = 2.dp)
                            .background(NeoColors.BorderDark, shape)
                    )
                }

                Row(
                    modifier = Modifier
                        .border(
                            width = if (isSelected) 2.5.dp else 1.5.dp,
                            color = NeoColors.BorderDark,
                            shape = shape
                        )
                        .background(
                            color = if (isSelected) provider.getAccentColor() else NeoColors.Surface,
                            shape = shape
                        )
                        .clip(shape)
                        .padding(horizontal = 12.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = provider.getIconRes()),
                        contentDescription = provider.displayName,
                        tint = NeoColors.BorderDark,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = provider.displayName,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                        color = NeoColors.BorderDark,
                        fontFamily = LocalNeoStyle.current.fontFamily
                    )
                }
            }
        }
    }
}
