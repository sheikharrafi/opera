package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = OperaRedBright,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF4D0005),
    onPrimaryContainer = Color(0xFFFFDADA),
    secondary = Color(0xFFE2B7BA),
    onSecondary = Color(0xFF422327),
    background = OperaDarkBg,
    onBackground = Color(0xFFF1F5F9),
    surface = OperaDarkSurface,
    onSurface = Color(0xFFF1F5F9),
    surfaceVariant = OperaDarkSurfaceVariant,
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = OperaDarkBorder
)

private val LightColorScheme = lightColorScheme(
    primary = OperaRed,
    onPrimary = Color.White,
    primaryContainer = OperaRedContainer,
    onPrimaryContainer = OperaRedOnContainer,
    secondary = Color(0xFF75565A),
    onSecondary = Color.White,
    background = OperaLightBg,
    onBackground = OperaTextPrimary,
    surface = OperaLightSurface,
    onSurface = OperaTextPrimary,
    surfaceVariant = OperaLightSurfaceVariant,
    onSurfaceVariant = OperaTextSecondary,
    outline = OperaBorder
)

val PrivateDarkColorScheme = darkColorScheme(
    primary = OperaPrivatePurple,
    onPrimary = Color.White,
    background = OperaPrivateDarkBg,
    surface = OperaPrivateSurface,
    onBackground = Color(0xFFF5F3FF),
    onSurface = Color(0xFFF5F3FF),
    surfaceVariant = Color(0x662E2849),
    onSurfaceVariant = Color(0xFFDDD6FE),
    outline = Color(0x33A78BFA)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    isPrivateMode: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        isPrivateMode -> PrivateDarkColorScheme
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
