package com.deepwiki.app.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.deepwiki.app.R
import com.deepwiki.app.theme.NeoBrutalistTheme
import com.deepwiki.app.theme.NeoColors
import com.deepwiki.app.ui.components.NavigationTab
import com.deepwiki.app.ui.components.NeoFloatingNavbar
import com.deepwiki.app.ui.screens.chat.ChatScreen
import com.deepwiki.app.ui.screens.settings.SettingsScreen
import com.deepwiki.app.ui.screens.tools.ToolsScreen
import com.deepwiki.app.viewmodel.ChatViewModel

class MainActivity : ComponentActivity() {

    private val chatViewModel: ChatViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val uiState by chatViewModel.uiState.collectAsState()
            var currentTab by remember { mutableStateOf(NavigationTab.CHAT) }

            NeoBrutalistTheme(
                fontOption = uiState.settings.customFont
            ) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        DeepWikiTopBar(currentTab = currentTab)
                    },
                    containerColor = NeoColors.Background
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        // Main Tab Views
                        when (currentTab) {
                            NavigationTab.CHAT -> {
                                ChatScreen(
                                    uiState = uiState,
                                    onSelectProvider = { chatViewModel.selectProvider(it) },
                                    onSendMessage = { chatViewModel.sendMessage(it) },
                                    onInputChanged = { chatViewModel.setInputText(it) },
                                    onStopStreaming = { chatViewModel.stopStreaming() },
                                    onToggleWebSearch = { chatViewModel.toggleWebSearch() },
                                    onThinkingEffortChanged = { chatViewModel.setThinkingEffort(it) },
                                    onFetchModels = { chatViewModel.fetchModelsForProvider(it) }
                                )
                            }
                            NavigationTab.TOOLS -> {
                                ToolsScreen(
                                    toolExecutor = chatViewModel.toolExecutor
                                )
                            }
                            NavigationTab.SETTINGS -> {
                                SettingsScreen(
                                    settings = uiState.settings,
                                    onUpdateSettings = { chatViewModel.updateSettings(it) },
                                    onFetchModels = { provider, callback ->
                                        chatViewModel.fetchModelsForProvider(provider, callback)
                                    }
                                )
                            }
                        }

                        // Floating Neo-Brutalist Navbar positioned at the bottom
                        NeoFloatingNavbar(
                            currentTab = currentTab,
                            onTabSelected = { currentTab = it },
                            modifier = Modifier.align(Alignment.BottomCenter)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DeepWikiTopBar(currentTab: NavigationTab) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(NeoColors.Yellow)
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // DeepWiki Brand Icon (Vector XML only, 22dp inside 32dp container)
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .border(2.dp, NeoColors.BorderDark, RoundedCornerShape(6.dp))
                        .background(NeoColors.Surface, RoundedCornerShape(6.dp))
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_deepwiki_logo),
                        contentDescription = "DeepWiki Logo",
                        tint = NeoColors.BorderDark,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = "DEEPWIKI",
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        letterSpacing = 1.sp,
                        color = NeoColors.BorderDark
                    )
                    Text(
                        text = "NEO-BRUTALIST AI KNOWLEDGE ENGINE",
                        fontWeight = FontWeight.Bold,
                        fontSize = 8.5.sp,
                        color = NeoColors.BorderDark.copy(alpha = 0.8f)
                    )
                }
            }

            // Status Badge without emojis
            Box(
                modifier = Modifier
                    .border(1.5.dp, NeoColors.BorderDark, RoundedCornerShape(4.dp))
                    .background(NeoColors.Cyan, RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = "v1.1 [ONLINE]",
                    fontWeight = FontWeight.Black,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    color = NeoColors.BorderDark
                )
            }
        }

        // Hard bottom border
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.5.dp)
                .background(NeoColors.BorderDark)
        )
    }
}
