package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Frosted Glass Ambient Mesh Background
 */
@Composable
fun FrostedGlassBackground(
    isPrivateMode: Boolean = false,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val isDark = isPrivateMode || MaterialTheme.colorScheme.background.value.toLong() < 0xFF888888L

    Box(
        modifier = modifier
            .fillMaxSize()
            .drawBehind {
                val width = size.width
                val height = size.height

                if (isPrivateMode) {
                    // Deep violet frosted mesh
                    drawRect(Color(0xFF0F0C1B))
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0x557C3AED), Color(0x007C3AED)),
                            center = Offset(width * 0.2f, height * 0.15f),
                            radius = width * 0.7f
                        )
                    )
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0x44EC4899), Color(0x00EC4899)),
                            center = Offset(width * 0.85f, height * 0.45f),
                            radius = width * 0.6f
                        )
                    )
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0x334F46E5), Color(0x004F46E5)),
                            center = Offset(width * 0.3f, height * 0.85f),
                            radius = width * 0.8f
                        )
                    )
                } else if (isDark) {
                    // Dark obsidian frosted mesh
                    drawRect(Color(0xFF0F172A))
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0x44E11D48), Color(0x00E11D48)),
                            center = Offset(width * 0.15f, height * 0.1f),
                            radius = width * 0.65f
                        )
                    )
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0x333B82F6), Color(0x003B82F6)),
                            center = Offset(width * 0.85f, height * 0.5f),
                            radius = width * 0.7f
                        )
                    )
                } else {
                    // Light luminous frosted glass mesh
                    drawRect(Color(0xFFF1F5F9))
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0x28FA1E32), Color(0x00FA1E32)),
                            center = Offset(width * 0.1f, height * 0.08f),
                            radius = width * 0.65f
                        )
                    )
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0x2238BDF8), Color(0x0038BDF8)),
                            center = Offset(width * 0.9f, height * 0.35f),
                            radius = width * 0.75f
                        )
                    )
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0x18818CF8), Color(0x00818CF8)),
                            center = Offset(width * 0.2f, height * 0.8f),
                            radius = width * 0.7f
                        )
                    )
                }
            }
    ) {
        content()
    }
}

/**
 * Frosted Glass Card container with specular reflection border & subtle elevation
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(16.dp),
    isPrivateMode: Boolean = false,
    elevation: Dp = 4.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val isDark = isPrivateMode || MaterialTheme.colorScheme.background.value.toLong() < 0xFF888888L

    val glassBackground = when {
        isPrivateMode -> Color(0xB31E1B2E)
        isDark -> Color(0xB31E293B)
        else -> Color(0xD9FFFFFF)
    }

    val glassBorderBrush = Brush.linearGradient(
        colors = when {
            isPrivateMode -> listOf(
                Color(0x66A78BFA),
                Color(0x22FFFFFF),
                Color(0x117C3AED)
            )
            isDark -> listOf(
                Color(0x4DFFFFFF),
                Color(0x1AFFFFFF),
                Color(0x2238BDF8)
            )
            else -> listOf(
                Color(0xEEFFFFFF),
                Color(0x66FFFFFF),
                Color(0x40CBD5E1)
            )
        },
        start = Offset(0f, 0f),
        end = Offset(400f, 400f)
    )

    Box(
        modifier = modifier
            .shadow(
                elevation = elevation,
                shape = shape,
                ambientColor = if (isDark) Color.Black.copy(alpha = 0.5f) else Color(0x1A0F172A),
                spotColor = if (isDark) Color.Black.copy(alpha = 0.6f) else Color(0x260F172A)
            )
            .clip(shape)
            .background(glassBackground)
            .border(1.dp, glassBorderBrush, shape)
            .then(
                if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
            )
    ) {
        content()
    }
}
