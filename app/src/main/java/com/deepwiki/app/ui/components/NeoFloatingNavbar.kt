package com.deepwiki.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.deepwiki.app.theme.LocalNeoStyle
import com.deepwiki.app.theme.NeoColors

enum class NavigationTab(val label: String, val icon: ImageVector) {
    CHAT("Chat", Icons.Default.Chat),
    TOOLS("Tools & MCP", Icons.Default.Hub),
    SETTINGS("Pengaturan", Icons.Default.Settings)
}

/**
 * Floating Neo-Brutalist Bottom Navigation Bar
 */
@Composable
fun NeoFloatingNavbar(
    currentTab: NavigationTab,
    onTabSelected: (NavigationTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(LocalNeoStyle.current.cornerRadius + 4.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        // Hard Shadow
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .offset(x = 4.dp, y = 4.dp)
                .background(NeoColors.BorderDark, shape)
        )

        // Navbar Container
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .border(LocalNeoStyle.current.borderWidth, NeoColors.BorderDark, shape)
                .background(NeoColors.Surface, shape)
                .clip(shape)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavigationTab.values().forEach { tab ->
                val isSelected = tab == currentTab
                val itemShape = RoundedCornerShape(8.dp)

                Row(
                    modifier = Modifier
                        .clip(itemShape)
                        .clickable { onTabSelected(tab) }
                        .then(
                            if (isSelected) {
                                Modifier
                                    .border(2.dp, NeoColors.BorderDark, itemShape)
                                    .background(NeoColors.Yellow, itemShape)
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            } else {
                                Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
                            }
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.label,
                        tint = NeoColors.BorderDark,
                        modifier = Modifier.size(20.dp)
                    )
                    if (isSelected) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = tab.label,
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp,
                            color = NeoColors.BorderDark,
                            fontFamily = LocalNeoStyle.current.fontFamily
                        )
                    }
                }
            }
        }
    }
}
