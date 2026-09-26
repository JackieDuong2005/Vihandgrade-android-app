package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class AppColors(
    val background: Color,
    val surface: Color,
    val card: Color,
    val cardElevated: Color,
    val border: Color,
    val textPrimary: Color,
    val textMuted: Color,
    val textSecondary: Color,
    val primary: Color,
    val primaryLight: Color,
    val isDark: Boolean
)

val DarkAppColors = AppColors(
    background = BackgroundDark,
    surface = SurfaceDark,
    card = SurfaceDark,
    cardElevated = CardElevatedDark,
    border = BorderDark,
    textPrimary = TextPrimaryDark,
    textMuted = TextMutedDark,
    textSecondary = TextSecondaryDark,
    primary = EmeraldAccent,
    primaryLight = Color(0xFF065F46),
    isDark = true
)

val LightAppColors = AppColors(
    background = BackgroundCream,
    surface = SurfaceLight,
    card = SurfaceLight,
    cardElevated = CardElevatedLight,
    border = BorderLight,
    textPrimary = TextPrimaryLight,
    textMuted = TextMutedLight,
    textSecondary = TextSecondaryLight,
    primary = EmeraldPrimary,
    primaryLight = EmeraldLight,
    isDark = false
)

val LocalAppColors = staticCompositionLocalOf {
    LightAppColors
}

object AppTheme {
    val colors: AppColors
        @Composable
        get() = LocalAppColors.current
}

private val DarkColorScheme = darkColorScheme(
    primary = EmeraldAccent,
    onPrimary = Color(0xFF064E3B),
    primaryContainer = Color(0xFF065F46),
    onPrimaryContainer = Color(0xFFA7F3D0),
    secondary = AccentSky,
    onSecondary = Color(0xFF082F49),
    secondaryContainer = Color(0xFF075985),
    onSecondaryContainer = Color(0xFFBAE6FD),
    tertiary = AccentAmber,
    background = BackgroundDark,
    onBackground = TextPrimaryDark,
    surface = SurfaceDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = CardElevatedDark,
    onSurfaceVariant = TextMutedDark,
    outline = BorderDark,
    outlineVariant = BorderDark,
    error = Color(0xFFEF4444),
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = EmeraldPrimary,
    onPrimary = Color.White,
    primaryContainer = EmeraldLight,
    onPrimaryContainer = Color(0xFF064E3B),
    secondary = Color(0xFF0284C7),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE0F2FE),
    onSecondaryContainer = Color(0xFF0369A1),
    tertiary = Color(0xFFD97706),
    background = BackgroundCream,
    onBackground = TextPrimaryLight,
    surface = SurfaceLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = CardElevatedLight,
    onSurfaceVariant = TextMutedLight,
    outline = BorderLight,
    outlineVariant = BorderSubtleLight,
    error = Color(0xFFDC2626),
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val appColors = if (darkTheme) DarkAppColors else LightAppColors

    CompositionLocalProvider(LocalAppColors provides appColors) {
        MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
    }
}
