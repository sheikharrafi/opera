package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TabItem
import com.example.ui.theme.OperaGreen
import com.example.ui.theme.OperaPrivatePurple
import com.example.ui.theme.OperaRed
import com.example.ui.theme.OperaRedBright
import com.example.util.UrlUtils

@Composable
fun OperaTopBar(
    currentTab: TabItem,
    urlInputText: String,
    isEditingUrl: Boolean,
    isPrivateMode: Boolean,
    isAdBlockerActive: Boolean,
    selectedSearchEngine: UrlUtils.SearchEngine,
    onUrlChanged: (String) -> Unit,
    onUrlSubmit: (String) -> Unit,
    onFocusChange: (Boolean) -> Unit,
    onReload: () -> Unit,
    onStop: () -> Unit,
    onSearchEngineSelect: (UrlUtils.SearchEngine) -> Unit,
    onOpenDataSavingsDashboard: () -> Unit,
    onOpenReaderMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    var showSearchEngineMenu by remember { mutableStateOf(false) }

    val glassBackground = if (isPrivateMode) Color(0xCC181528) else Color(0xDDFFFFFF)
    val glassBorder = if (isPrivateMode) Color(0x33A78BFA) else Color(0x55E2E8F0)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(glassBackground)
            .border(0.5.dp, glassBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Search Engine / Security Icon
            Box {
                Surface(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .clickable { showSearchEngineMenu = true }
                        .testTag("search_engine_selector"),
                    color = if (isPrivateMode) OperaPrivatePurple.copy(alpha = 0.25f) else Color(0x33CBD5E1),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isPrivateMode) Color(0x44A78BFA) else Color(0x66FFFFFF)
                    ),
                    shape = CircleShape
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (currentTab.url.startsWith("https://")) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Secure Connection",
                                tint = if (isPrivateMode) OperaPrivatePurple else OperaGreen,
                                modifier = Modifier.size(16.dp)
                            )
                        } else if (currentTab.url == "opera://speeddial") {
                            Text(
                                text = selectedSearchEngine.iconLetter,
                                fontWeight = FontWeight.Black,
                                color = if (isPrivateMode) OperaPrivatePurple else OperaRed,
                                fontSize = 16.sp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Public,
                                contentDescription = "Webpage",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                DropdownMenu(
                    expanded = showSearchEngineMenu,
                    onDismissRequest = { showSearchEngineMenu = false }
                ) {
                    UrlUtils.SearchEngine.values().forEach { engine ->
                        DropdownMenuItem(
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = if (engine == selectedSearchEngine) OperaRed else Color.LightGray.copy(alpha = 0.3f),
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = engine.iconLetter,
                                                color = if (engine == selectedSearchEngine) Color.White else Color.Black,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                    Text(
                                        text = engine.displayName,
                                        fontWeight = if (engine == selectedSearchEngine) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            },
                            onClick = {
                                onSearchEngineSelect(engine)
                                showSearchEngineMenu = false
                            }
                        )
                    }
                }
            }

            // Frosted Glass Omnibox / URL Input
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .shadow(1.dp, RoundedCornerShape(22.dp)),
                shape = RoundedCornerShape(22.dp),
                color = if (isPrivateMode) Color(0x4D2A2440) else Color(0x66F1F5F9),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isPrivateMode) Color(0x40A78BFA) else Color(0x80FFFFFF)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BasicTextField(
                        value = urlInputText,
                        onValueChange = onUrlChanged,
                        modifier = Modifier
                            .weight(1f)
                            .onFocusChanged { onFocusChange(it.isFocused) }
                            .testTag("url_input_field"),
                        singleLine = true,
                        textStyle = TextStyle(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 14.sp
                        ),
                        cursorBrush = SolidColor(if (isPrivateMode) OperaPrivatePurple else OperaRed),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Uri,
                            imeAction = ImeAction.Go
                        ),
                        keyboardActions = KeyboardActions(
                            onGo = {
                                focusManager.clearFocus()
                                onUrlSubmit(urlInputText)
                            }
                        ),
                        decorationBox = { innerTextField ->
                            if (urlInputText.isEmpty() && !isEditingUrl) {
                                Text(
                                    text = if (isPrivateMode) "Private search or enter address" else "Search or enter address",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                    fontSize = 14.sp
                                )
                            }
                            innerTextField()
                        }
                    )

                    // Clear button or Refresh/Stop button
                    if (urlInputText.isNotEmpty() && isEditingUrl) {
                        IconButton(
                            onClick = { onUrlChanged("") },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    } else if (currentTab.url != "opera://speeddial") {
                        if (currentTab.isLoading) {
                            IconButton(
                                onClick = onStop,
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Stop",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        } else {
                            IconButton(
                                onClick = onReload,
                                modifier = Modifier.size(28.dp).testTag("reload_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Refresh",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Quick Data Savings Action Button on Top Bar
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(
                        if (isPrivateMode) Color(0x338B5CF6) else OperaGreen.copy(alpha = 0.15f)
                    )
                    .border(
                        1.dp,
                        if (isPrivateMode) Color(0x66A78BFA) else OperaGreen.copy(alpha = 0.5f),
                        CircleShape
                    )
                    .clickable {
                        if (currentTab.url != "opera://speeddial") {
                            onOpenReaderMode()
                        } else {
                            onOpenDataSavingsDashboard()
                        }
                    }
                    .testTag("top_quick_action_button"),
                contentAlignment = Alignment.Center
            ) {
                if (currentTab.url != "opera://speeddial") {
                    Icon(
                        imageVector = Icons.Default.Article,
                        contentDescription = "Reader View",
                        tint = if (isPrivateMode) OperaPrivatePurple else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(18.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = "Data Savings",
                        tint = if (isPrivateMode) OperaPrivatePurple else OperaGreen,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Slim Neon Loading progress bar
        AnimatedVisibility(
            visible = currentTab.isLoading,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            LinearProgressIndicator(
                progress = { currentTab.progress / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.5.dp),
                color = if (isPrivateMode) OperaPrivatePurple else OperaRedBright,
                trackColor = Color.Transparent
            )
        }
    }
}
