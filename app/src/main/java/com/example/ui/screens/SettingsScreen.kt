package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.DataSaverOn
import androidx.compose.material.icons.filled.Http
import androidx.compose.material.icons.filled.Javascript
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DataSavingMode
import com.example.ui.components.FrostedGlassBackground
import com.example.ui.components.GlassCard
import com.example.ui.theme.OperaGreen
import com.example.ui.theme.OperaRed
import com.example.util.UrlUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    selectedSearchEngine: UrlUtils.SearchEngine,
    dataSavingMode: DataSavingMode,
    isAdBlockerActive: Boolean,
    isHttpsOnlyMode: Boolean,
    isJavaScriptEnabled: Boolean,
    isBlockPopups: Boolean,
    onSearchEngineChange: (UrlUtils.SearchEngine) -> Unit,
    onToggleAdBlocker: () -> Unit,
    onToggleHttpsOnly: () -> Unit,
    onToggleJavaScript: () -> Unit,
    onToggleBlockPopups: () -> Unit,
    onClearAllBrowsingData: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showSearchEngineDialog by remember { mutableStateOf(false) }
    var showClearConfirm by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("settings_back")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        containerColor = Color.Transparent,
        modifier = modifier
    ) { paddingValues ->
        FrostedGlassBackground {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // General Section
                Text(
                    text = "General",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = OperaRed
                )

                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Default Search Engine
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showSearchEngineDialog = true }
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Default Search Engine", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                Text(selectedSearchEngine.displayName, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Text("Change", color = OperaRed, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }

                        Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                        // Block Ads & Trackers
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Block Ads & Trackers", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                Text("Speed up loading by blocking intrusive ad scripts and telemetry", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(
                                checked = isAdBlockerActive,
                                onCheckedChange = { onToggleAdBlocker() },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = OperaRed
                                )
                            )
                        }
                    }
                }

                // Security & Privacy Section
                Text(
                    text = "Security & Privacy",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = OperaRed
                )

                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // HTTPS Only Mode
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("HTTPS-Only Mode", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                Text("Automatically upgrade connections to secure HTTPS", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(
                                checked = isHttpsOnlyMode,
                                onCheckedChange = { onToggleHttpsOnly() },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = OperaRed
                                )
                            )
                        }

                        Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                        // JavaScript Toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Enable JavaScript", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                Text("Required for dynamic websites. Disable for ultra-fast text reading.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(
                                checked = isJavaScriptEnabled,
                                onCheckedChange = { onToggleJavaScript() },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = OperaRed
                                )
                            )
                        }

                        Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                        // Block Pop-ups
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Block Pop-ups & Redirects", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                Text("Prevent unwanted new windows from opening automatically", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(
                                checked = isBlockPopups,
                                onCheckedChange = { onToggleBlockPopups() },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = OperaRed
                                )
                            )
                        }

                        Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                        // Clear All Browsing Data
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showClearConfirm = true }
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Clear Browsing Data", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                Text("Delete history, cache, cookies & web storage", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Text("Clear", color = OperaRed, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // About Opera Mini
                Text(
                    text = "About",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = OperaRed
                )

                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Shield, contentDescription = null, tint = OperaGreen, modifier = Modifier.size(18.dp))
                            Text("Opera Mini Browser v78.0", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                        Text("High-speed data compression engine with hardware acceleration, secure sandboxed private browsing, integrated ad blocker, and offline MHT page archiver.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }

    if (showSearchEngineDialog) {
        AlertDialog(
            onDismissRequest = { showSearchEngineDialog = false },
            title = { Text("Select Default Search Engine") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    UrlUtils.SearchEngine.values().forEach { engine ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSearchEngineChange(engine)
                                    showSearchEngineDialog = false
                                }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(engine.iconLetter, fontWeight = FontWeight.Bold, color = OperaRed)
                            Text(engine.displayName, fontWeight = if (engine == selectedSearchEngine) FontWeight.Bold else FontWeight.Normal)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSearchEngineDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    if (showClearConfirm) {
        AlertDialog(
            onDismissRequest = { showClearConfirm = false },
            title = { Text("Clear All Browsing Data?") },
            text = { Text("This will permanently clear visited history, web caches, cookies, and local database entries.") },
            confirmButton = {
                Button(
                    onClick = {
                        onClearAllBrowsingData()
                        showClearConfirm = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = OperaRed)
                ) {
                    Text("Clear All")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
