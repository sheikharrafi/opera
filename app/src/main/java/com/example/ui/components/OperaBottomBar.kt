package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DataSavingMode
import com.example.ui.theme.OperaGreen
import com.example.ui.theme.OperaPrivatePurple
import com.example.ui.theme.OperaRed
import com.example.ui.theme.OperaRedBright
import com.example.ui.theme.OperaRedDark

@Composable
fun OperaBottomBar(
    canGoBack: Boolean,
    canGoForward: Boolean,
    tabCount: Int,
    isPrivateMode: Boolean,
    dataSavingMode: DataSavingMode,
    onBackClick: () -> Unit,
    onForwardClick: () -> Unit,
    onOperaMenuClick: () -> Unit,
    onTabsClick: () -> Unit,
    onHomeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val glassBackground = if (isPrivateMode) Color(0xD9181528) else Color(0xE6FFFFFF)
    val glassBorderBrush = Brush.verticalGradient(
        colors = if (isPrivateMode) {
            listOf(Color(0x40A78BFA), Color(0x10FFFFFF))
        } else {
            listOf(Color(0x99FFFFFF), Color(0x33CBD5E1))
        }
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(12.dp)
            .background(glassBackground)
            .border(androidx.compose.foundation.BorderStroke(1.dp, glassBorderBrush))
            .windowInsetsPadding(WindowInsets.navigationBars)
            .height(58.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back Button
            IconButton(
                onClick = onBackClick,
                enabled = canGoBack,
                modifier = Modifier
                    .size(48.dp)
                    .testTag("nav_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = if (canGoBack) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                )
            }

            // Forward Button
            IconButton(
                onClick = onForwardClick,
                enabled = canGoForward,
                modifier = Modifier
                    .size(48.dp)
                    .testTag("nav_forward_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Forward",
                    tint = if (canGoForward) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                )
            }

            // Iconic Opera Center Glowing "O" Button
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(50.dp)
                    .clickable { onOperaMenuClick() }
                    .testTag("opera_menu_button")
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .shadow(6.dp, CircleShape)
                        .clip(CircleShape)
                        .background(
                            brush = if (isPrivateMode) {
                                Brush.radialGradient(
                                    colors = listOf(OperaPrivatePurple, Color(0xFF4A148C))
                                )
                            } else {
                                Brush.radialGradient(
                                    colors = listOf(OperaRedBright, OperaRed, OperaRedDark)
                                )
                            }
                        )
                        .border(1.5.dp, Color.White.copy(alpha = 0.8f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "O",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                // Data Savings Active Dot Badge
                if (dataSavingMode != DataSavingMode.OFF) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = (-2).dp, y = 2.dp)
                            .size(11.dp)
                            .clip(CircleShape)
                            .background(if (dataSavingMode == DataSavingMode.EXTREME) OperaGreen else Color(0xFFFFB300))
                            .border(1.5.dp, Color.White, CircleShape)
                    )
                }
            }

            // Glass Tabs Counter Button
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .clickable { onTabsClick() }
                    .testTag("tabs_switcher_button"),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .background(
                            if (isPrivateMode) Color(0x338B5CF6) else Color(0x33CBD5E1),
                            RoundedCornerShape(6.dp)
                        )
                        .border(
                            width = 1.5.dp,
                            color = if (isPrivateMode) OperaPrivatePurple else MaterialTheme.colorScheme.onSurface,
                            shape = RoundedCornerShape(6.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$tabCount",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isPrivateMode) OperaPrivatePurple else MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Home Button
            IconButton(
                onClick = onHomeClick,
                modifier = Modifier
                    .size(48.dp)
                    .testTag("nav_home_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home / Speed Dial",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
