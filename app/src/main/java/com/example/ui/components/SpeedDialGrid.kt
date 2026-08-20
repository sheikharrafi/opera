package com.example.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.OfflinePin
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Bookmark
import com.example.data.model.DataSavingMode
import com.example.data.model.DataSavingsStats
import com.example.ui.theme.OperaGreen
import com.example.ui.theme.OperaPrivatePurple
import com.example.ui.theme.OperaRed
import com.example.ui.theme.OperaRedBright
import com.example.ui.theme.OperaRedDark
import com.example.util.UrlUtils

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SpeedDialGrid(
    bookmarks: List<Bookmark>,
    dataSavingMode: DataSavingMode,
    dataSavingsStats: DataSavingsStats,
    isPrivateMode: Boolean,
    onSelectUrl: (String) -> Unit,
    onAddSpeedDial: (String, String) -> Unit,
    onDeleteSpeedDial: (Bookmark) -> Unit = {},
    onOpenBookmarks: () -> Unit = {},
    onOpenHistory: () -> Unit = {},
    onOpenDownloads: () -> Unit = {},
    onOpenSavedPages: () -> Unit = {},
    onOpenDataSavingsDashboard: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedItemToDelete by remember { mutableStateOf<Bookmark?>(null) }
    var newTitle by remember { mutableStateOf("") }
    var newUrl by remember { mutableStateOf("") }

    val speedDialItems = bookmarks.filter { it.isSpeedDial }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Authentic Opera Mini Header Branding
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Iconic Opera Red "O" Logo Badge
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .shadow(4.dp, CircleShape)
                        .clip(CircleShape)
                        .background(
                            brush = if (isPrivateMode) {
                                Brush.radialGradient(listOf(OperaPrivatePurple, Color(0xFF4A148C)))
                            } else {
                                Brush.radialGradient(listOf(OperaRedBright, OperaRed, OperaRedDark))
                            }
                        )
                        .border(1.5.dp, Color.White.copy(alpha = 0.8f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "O",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                Column {
                    Text(
                        text = if (isPrivateMode) "Opera Mini Private" else "Opera Mini",
                        fontWeight = FontWeight.Black,
                        fontSize = 19.sp,
                        color = if (isPrivateMode) OperaPrivatePurple else OperaRed
                    )
                    Text(
                        text = "Fast • Lightweight • Data Saver",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Quick Data Savings Capsule
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = if (isPrivateMode) Color(0x338B5CF6) else OperaGreen.copy(alpha = 0.15f),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isPrivateMode) OperaPrivatePurple.copy(alpha = 0.4f) else OperaGreen.copy(alpha = 0.4f)
                ),
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onOpenDataSavingsDashboard() }
                    .testTag("speed_dial_savings_pill")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = "Savings",
                        tint = if (isPrivateMode) OperaPrivatePurple else OperaGreen,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = if (dataSavingMode != DataSavingMode.OFF) "${dataSavingsStats.savingPercentage}% SAVED" else "SAVER OFF",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isPrivateMode) OperaPrivatePurple else OperaGreen
                    )
                }
            }
        }

        // Quick Navigation Utilities Bar (Bookmarks, History, Downloads, Offline Pages)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            QuickNavButton(
                icon = Icons.Default.Bookmark,
                label = "Bookmarks",
                isPrivateMode = isPrivateMode,
                onClick = onOpenBookmarks,
                modifier = Modifier.weight(1f)
            )
            QuickNavButton(
                icon = Icons.Default.History,
                label = "History",
                isPrivateMode = isPrivateMode,
                onClick = onOpenHistory,
                modifier = Modifier.weight(1f)
            )
            QuickNavButton(
                icon = Icons.Default.Download,
                label = "Downloads",
                isPrivateMode = isPrivateMode,
                onClick = onOpenDownloads,
                modifier = Modifier.weight(1f)
            )
            QuickNavButton(
                icon = Icons.Default.OfflinePin,
                label = "Saved",
                isPrivateMode = isPrivateMode,
                onClick = onOpenSavedPages,
                modifier = Modifier.weight(1f)
            )
        }

        // Speed Dial Section Title
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Speed Dial",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "${speedDialItems.size} sites (long-press to edit)",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // 4-Column Grid with Speed Dial Tiles
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height((((speedDialItems.size + 1) / 4 + 1) * 96).dp)
        ) {
            items(speedDialItems, key = { it.id }) { item ->
                SpeedDialTile(
                    title = item.title,
                    url = item.url,
                    isPrivateMode = isPrivateMode,
                    onClick = { onSelectUrl(item.url) },
                    onLongClick = { selectedItemToDelete = item }
                )
            }

            // Add Speed Dial Button
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { showAddDialog = true }
                        .testTag("add_speed_dial_button")
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .shadow(2.dp, CircleShape)
                            .clip(CircleShape)
                            .background(if (isPrivateMode) Color(0x33A78BFA) else Color(0x33CBD5E1))
                            .border(
                                1.dp,
                                if (isPrivateMode) Color(0x44A78BFA) else Color(0x66FFFFFF),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add shortcut",
                            tint = if (isPrivateMode) OperaPrivatePurple else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Text(
                        text = "Add",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1
                    )
                }
            }
        }
    }

    // Add Speed Dial Dialog
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add to Speed Dial", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = newTitle,
                        onValueChange = { newTitle = it },
                        label = { Text("Title") },
                        placeholder = { Text("e.g. ESPN") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newUrl,
                        onValueChange = { newUrl = it },
                        label = { Text("Address (URL)") },
                        placeholder = { Text("e.g. espn.com") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newUrl.isNotBlank()) {
                            val cleanUrl = if (!newUrl.startsWith("http://") && !newUrl.startsWith("https://")) {
                                "https://$newUrl"
                            } else newUrl
                            val title = if (newTitle.isBlank()) cleanUrl else newTitle
                            onAddSpeedDial(title, cleanUrl)
                            newTitle = ""
                            newUrl = ""
                            showAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = if (isPrivateMode) OperaPrivatePurple else OperaRed)
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Delete confirmation dialog for Speed Dial
    selectedItemToDelete?.let { item ->
        AlertDialog(
            onDismissRequest = { selectedItemToDelete = null },
            title = { Text("Remove Speed Dial") },
            text = { Text("Do you want to remove \"${item.title}\" from Speed Dial?") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteSpeedDial(item)
                        selectedItemToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = OperaRed)
                ) {
                    Text("Remove")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedItemToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun QuickNavButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isPrivateMode: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = if (isPrivateMode) Color(0x332A2440) else Color(0x55F1F5F9),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isPrivateMode) Color(0x33A78BFA) else Color(0x66FFFFFF)
        )
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isPrivateMode) OperaPrivatePurple else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SpeedDialTile(
    title: String,
    url: String,
    isPrivateMode: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val domain = UrlUtils.getDomain(url)
    val (iconColor, brandLetter) = getBrandStyle(domain)

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .testTag("speed_dial_item_${title.lowercase()}"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .shadow(3.dp, CircleShape)
                .clip(CircleShape)
                .background(
                    if (isPrivateMode) Color(0x661E1B2E) else Color(0xD9FFFFFF)
                )
                .border(
                    1.dp,
                    if (isPrivateMode) Color(0x40A78BFA) else Color(0x80FFFFFF),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(iconColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = brandLetter,
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp
                )
            }
        }

        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}

private fun getBrandStyle(domain: String): Pair<Color, String> = when {
    domain.contains("google") -> Pair(Color(0xFF4285F4), "G")
    domain.contains("facebook") -> Pair(Color(0xFF1877F2), "f")
    domain.contains("youtube") -> Pair(Color(0xFFFF0000), "▶")
    domain.contains("wikipedia") -> Pair(Color(0xFF222222), "W")
    domain.contains("amazon") -> Pair(Color(0xFFFF9900), "a")
    domain.contains("twitter") || domain.contains("x.com") -> Pair(Color(0xFF000000), "𝕏")
    domain.contains("reddit") -> Pair(Color(0xFFFF4500), "r")
    domain.contains("bbc") -> Pair(Color(0xFFBB1919), "B")
    domain.contains("cricbuzz") -> Pair(Color(0xFF009270), "c")
    domain.contains("yahoo") -> Pair(Color(0xFF6001D2), "Y")
    domain.contains("github") -> Pair(Color(0xFF24292E), "git")
    else -> Pair(OperaRedBright, domain.firstOrNull()?.uppercase() ?: "O")
}
