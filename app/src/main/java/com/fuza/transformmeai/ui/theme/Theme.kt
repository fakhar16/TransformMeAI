package com.fuza.transformmeai.ui.theme

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

private val DarkColors =
    darkColorScheme(
        primary = PurpleSecondary,
        onPrimary = DeepBackground,
        secondary = AccentMint,
        onSecondary = DeepBackground,
        tertiary = PurplePrimary,
        background = DeepBackground,
        onBackground = ColorPalette.onDark,
        surface = SurfaceTint,
        onSurface = ColorPalette.onDark,
        surfaceVariant = ColorPalette.surfaceVariantDark,
        onSurfaceVariant = ColorPalette.onSurfaceVariantDark,
    )

private val LightColors =
    lightColorScheme(
        primary = PurplePrimary,
        onPrimary = ColorPalette.onLight,
        secondary = PurpleSecondary,
        onSecondary = ColorPalette.onLight,
        tertiary = AccentMint,
        onTertiary = DeepBackground,
        background = ColorPalette.lightBackground,
        onBackground = DeepBackground,
        surface = ColorPalette.lightSurface,
        onSurface = DeepBackground,
        surfaceVariant = ColorPalette.surfaceVariantLight,
        onSurfaceVariant = ColorPalette.onSurfaceVariantLight,
    )

private object ColorPalette {
    val onDark = Color(0xFFE8E6FF)
    val onLight = Color(0xFFFFFFFF)
    val lightBackground = Color(0xFFF6F4FF)
    val lightSurface = Color(0xFFFFFFFF)
    val surfaceVariantDark = Color(0xFF2A2C40)
    val onSurfaceVariantDark = Color(0xFFC6C4DD)
    val surfaceVariantLight = Color(0xFFE6E0F5)
    val onSurfaceVariantLight = Color(0xFF4A4458)
}

@Composable
fun TransformMeAiTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val colorScheme =
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                if (useDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            }
            useDarkTheme -> DarkColors
            else -> LightColors
        }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = TransformTypography,
        content = content,
    )
}