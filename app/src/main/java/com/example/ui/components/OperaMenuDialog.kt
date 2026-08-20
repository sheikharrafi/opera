package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DesktopMac
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.FindInPage
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.OfflinePin
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DataSavingMode
import com.example.data.model.DataSavingsStats
import com.example.data.model.TabItem
import com.example.ui.theme.OperaGreen
import com.example.ui.theme.OperaPrivatePurple
import com.example.ui.theme.OperaRed
import com.example.util.UrlUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OperaMenuDialog(
    isOpen: Boolean,
    currentTab: TabItem,
    dataSavingMode: DataSavingMode,
    dataSavingsStats: DataSavingsStats,
    isAdBlockerActive: Boolean,
    isPrivateMode: Boolean,
    onDismiss: () -> Unit,
    onOpenBookmarks: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenDownloads: () -> Unit,
    onOpenSavedPages: () -> Unit,
    onOpenDataSavingsDashboard: () -> Unit,
    onToggleDataSavings: () -> Unit,
    onToggleAdBlocker: () -> Unit,
    onToggleNightMode: () -> Unit,
    onToggleDesktopSite: () -> Unit,
    onFindInPage: () -> Unit,
    onAddToSpeedDial: () -> Unit,
    onSavePageOffline: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!isOpen) return

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val glassSheetBg = if (isPrivateMode) Color(0xF0181428) else Color(0xF2FFFFFF)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = glassSheetBg,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 24.dp)
        ) {
            // Frosted Glass Data Savings Banner Card
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("menu_data_savings_banner"),
                isPrivateMode = isPrivateMode,
                onClick = {
                    onDismiss()
                    onOpenDataSavingsDashboard()
                }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = if (dataSavingMode == DataSavingMode.EXTREME) OperaGreen else if (dataSavingMode == DataSavingMode.HIGH) OperaRed else Color.Gray,
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color.White.copy(alpha = 0.8f)),
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = if (dataSavingMode != DataSavingMode.OFF) "${dataSavingsStats.savingPercentage}%" else "OFF",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Column {
                            Text(
                                text = "Data Savings: ${dataSavingMode.name}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${UrlUtils.formatBytes(dataSavingsStats.totalSavedBytes)} saved • ${dataSavingsStats.adsBlockedCount} ads blocked",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Switch(
                        checked = dataSavingMode != DataSavingMode.OFF,
                        onCheckedChange = { onToggleDataSavings() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = if (isPrivateMode) OperaPrivatePurple else OperaRed
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 4-Column Feature Grid with Clean Opera Mini Action Tiles
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Bookmarks
                item {
                    OperaMenuItem(
                        icon = Icons.Default.Bookmark,
                        title = "Bookmarks",
                        isPrivateMode = isPrivateMode,
                        onClick = {
                            onDismiss()
                            onOpenBookmarks()
                        }
                    )
                }

                // History
                item {
                    OperaMenuItem(
                        icon = Icons.Default.History,
                        title = "History",
                        isPrivateMode = isPrivateMode,
                        onClick = {
                            onDismiss()
                            onOpenHistory()
                        }
                    )
                }

                // Downloads
                item {
                    OperaMenuItem(
                        icon = Icons.Default.Download,
                        title = "Downloads",
                        isPrivateMode = isPrivateMode,
                        onClick = {
                            onDismiss()
                            onOpenDownloads()
                        }
                    )
                }

                // Saved Offline Pages
                item {
                    OperaMenuItem(
                        icon = Icons.Default.OfflinePin,
                        title = "Saved Pages",
                        isPrivateMode = isPrivateMode,
                        onClick = {
                            onDismiss()
                            onOpenSavedPages()
                        }
                    )
                }

                // Ad Blocker Toggle
                item {
                    OperaMenuItem(
                        icon = Icons.Default.Security,
                        title = "Ad Blocker",
                        badge = if (isAdBlockerActive) "ON" else "OFF",
                        isActive = isAdBlockerActive,
                        isPrivateMode = isPrivateMode,
                        onClick = onToggleAdBlocker
                    )
                }

                // Night Mode Toggle
                item {
                    OperaMenuItem(
                        icon = if (currentTab.isNightMode) Icons.Default.DarkMode else Icons.Outlined.DarkMode,
                        title = "Night Mode",
                        badge = if (currentTab.isNightMode) "ON" else "OFF",
                        isActive = currentTab.isNightMode,
                        isPrivateMode = isPrivateMode,
                        onClick = onToggleNightMode
                    )
                }

                // Desktop Site Toggle
                item {
                    OperaMenuItem(
                        icon = Icons.Default.DesktopMac,
                        title = "Desktop Site",
                        badge = if (currentTab.isDesktopMode) "ON" else "OFF",
                        isActive = currentTab.isDesktopMode,
                        isPrivateMode = isPrivateMode,
                        onClick = onToggleDesktopSite
                    )
                }

                // Find in Page
                item {
                    OperaMenuItem(
                        icon = Icons.Default.FindInPage,
                        title = "Find in Page",
                        isPrivateMode = isPrivateMode,
                        onClick = {
                            onDismiss()
                            onFindInPage()
                        }
                    )
                }

                // Save Page Offline
                item {
                    OperaMenuItem(
                        icon = Icons.Default.Download,
                        title = "Save Offline",
                        isPrivateMode = isPrivateMode,
                        onClick = {
                            onDismiss()
                            onSavePageOffline()
                        }
                    )
                }

                // Add to Speed Dial
                item {
                    OperaMenuItem(
                        icon = Icons.Default.AddCircleOutline,
                        title = "Add to Dial",
                        isPrivateMode = isPrivateMode,
                        onClick = {
                            onDismiss()
                            onAddToSpeedDial()
                        }
                    )
                }

                // Settings
                item {
                    OperaMenuItem(
                        icon = Icons.Default.Settings,
                        title = "Settings",
                        isPrivateMode = isPrivateMode,
                        onClick = {
                            onDismiss()
                            onOpenSettings()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun OperaMenuItem(
    icon: ImageVector,
    title: String,
    badge: String? = null,
    isActive: Boolean = false,
    isPrivateMode: Boolean = false,
    iconTint: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(vertical = 8.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(
                    if (isActive) {
                        if (isPrivateMode) OperaPrivatePurple.copy(alpha = 0.25f) else OperaRed.copy(alpha = 0.15f)
                    } else {
                        if (isPrivateMode) Color(0x33A78BFA) else Color(0x33CBD5E1)
                    }
                )
                .border(
                    1.dp,
                    if (isActive) (if (isPrivateMode) OperaPrivatePurple else OperaRed) else Color(0x40FFFFFF),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isActive) (if (isPrivateMode) OperaPrivatePurple else OperaRed) else iconTint,
                modifier = Modifier.size(24.dp)
            )

            if (badge != null) {
                Surface(
                    color = if (isActive) (if (isPrivateMode) OperaPrivatePurple else OperaRed) else Color.Gray,
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 2.dp, end = 2.dp)
                ) {
                    Text(
                        text = badge,
                        color = Color.White,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 3.dp, vertical = 1.dp)
                    )
                }
            }
        }

        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            textAlign = TextAlign.Center
        )
    }
}
