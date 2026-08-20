package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TabItem
import com.example.ui.components.FrostedGlassBackground
import com.example.ui.components.GlassCard
import com.example.ui.theme.OperaPrivatePurple
import com.example.ui.theme.OperaRed
import com.example.ui.theme.OperaRedBright
import com.example.util.UrlUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabsOverviewScreen(
    tabs: List<TabItem>,
    currentTabId: String,
    onSelectTab: (String) -> Unit,
    onCloseTab: (String) -> Unit,
    onNewTab: (Boolean) -> Unit,
    onCloseAllTabs: () -> Unit,
    onDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val isPrivateView = selectedTabIndex == 1
    val displayedTabs = tabs.filter { it.isPrivate == isPrivateView }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isPrivateView) "Private Tabs (${displayedTabs.size})" else "Tabs (${displayedTabs.size})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                actions = {
                    if (displayedTabs.isNotEmpty()) {
                        IconButton(onClick = onCloseAllTabs, modifier = Modifier.testTag("close_all_tabs_btn")) {
                            Icon(
                                Icons.Default.DeleteSweep,
                                contentDescription = "Close All Tabs",
                                tint = if (isPrivateView) OperaPrivatePurple else OperaRed
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .background(if (isPrivateView) Color(0xD9181528) else Color(0xE6FFFFFF))
                    .border(1.dp, if (isPrivateView) Color(0x33A78BFA) else Color(0x40CBD5E1))
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // New Tab Glass Button
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { onNewTab(isPrivateView) }
                            .testTag("new_tab_bottom_button"),
                        color = if (isPrivateView) OperaPrivatePurple else OperaRed,
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = if (isPrivateView) "New Private Tab" else "New Tab",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }

                    // Done Button
                    Text(
                        text = "Done",
                        color = if (isPrivateView) OperaPrivatePurple else OperaRed,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        modifier = Modifier
                            .clickable { onDone() }
                            .padding(8.dp)
                            .testTag("tabs_done_button")
                    )
                }
            }
        },
        containerColor = Color.Transparent,
        modifier = modifier
    ) { paddingValues ->
        FrostedGlassBackground(isPrivateMode = isPrivateView) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Glass Mode Switcher Tabs (Standard vs Private)
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(14.dp),
                    isPrivateMode = isPrivateView,
                    elevation = 2.dp
                ) {
                    TabRow(
                        selectedTabIndex = selectedTabIndex,
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                                color = if (isPrivateView) OperaPrivatePurple else OperaRed
                            )
                        },
                        divider = {}
                    ) {
                        Tab(
                            selected = selectedTabIndex == 0,
                            onClick = { selectedTabIndex = 0 },
                            text = {
                                Text(
                                    "Standard (${tabs.count { !it.isPrivate }})",
                                    fontWeight = if (selectedTabIndex == 0) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        )
                        Tab(
                            selected = selectedTabIndex == 1,
                            onClick = { selectedTabIndex = 1 },
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Shield,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp),
                                        tint = if (selectedTabIndex == 1) OperaPrivatePurple else Color.Gray
                                    )
                                    Text(
                                        "Private (${tabs.count { it.isPrivate }})",
                                        fontWeight = if (selectedTabIndex == 1) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        )
                    }
                }

                // Tabs Grid
                if (displayedTabs.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = if (isPrivateView) Icons.Default.Shield else Icons.Default.Public,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(56.dp)
                            )
                            Text(
                                text = if (isPrivateView) "No private tabs open" else "No tabs open",
                                color = Color.Gray,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = if (isPrivateView) "Private browsing leaves no history, cookies or cache on your device." else "Tap '+ New Tab' below to start browsing.",
                                color = Color.Gray.copy(alpha = 0.8f),
                                fontSize = 13.sp,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        items(displayedTabs, key = { it.id }) { tab ->
                            TabCardItem(
                                tab = tab,
                                isSelected = tab.id == currentTabId,
                                isPrivateMode = isPrivateView,
                                onClick = { onSelectTab(tab.id) },
                                onClose = { onCloseTab(tab.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TabCardItem(
    tab: TabItem,
    isSelected: Boolean,
    isPrivateMode: Boolean,
    onClick: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeBorderColor = if (isPrivateMode) OperaPrivatePurple else OperaRed

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(160.dp)
            .shadow(if (isSelected) 6.dp else 2.dp, RoundedCornerShape(14.dp))
            .clip(RoundedCornerShape(14.dp))
            .background(if (isPrivateMode) Color(0xD91E1B2E) else Color(0xF2FFFFFF))
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) activeBorderColor else (if (isPrivateMode) Color(0x33A78BFA) else Color(0x66CBD5E1)),
                shape = RoundedCornerShape(14.dp)
            )
            .clickable { onClick() }
            .testTag("tab_item_${tab.id}")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Tab Header with Title and Close Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (isPrivateMode) Color(0x662E2849) else Color(0x40E2E8F0))
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (tab.isPrivate) {
                        Icon(
                            Icons.Default.Shield,
                            contentDescription = null,
                            tint = OperaPrivatePurple,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                    Text(
                        text = if (tab.url == "opera://speeddial") "Speed Dial" else tab.title.ifBlank { "Untitled" },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                IconButton(
                    onClick = onClose,
                    modifier = Modifier
                        .size(20.dp)
                        .testTag("close_tab_btn_${tab.id}")
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Close tab",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            // Tab Preview Representation (Frosted Card Inside)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isPrivateMode) Color(0x3312101F) else Color(0x22CBD5E1)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = if (tab.url == "opera://speeddial") Icons.Default.Public else Icons.Default.Lock,
                        contentDescription = null,
                        tint = if (isPrivateMode) OperaPrivatePurple.copy(alpha = 0.6f) else OperaRed.copy(alpha = 0.6f),
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        text = UrlUtils.getDomain(tab.url),
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
