package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
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
    background = Color(0xFF0B1120),
    surface = Color(0xFF111827),
    card = Color(0xFF1E293B),
    cardElevated = Color(0xFF243248),
    border = Color(0xFF334155),
    textPrimary = Color(0xFFF8FAFC),
    textMuted = Color(0xFF94A3B8),
    textSecondary = Color(0xFFCBD5E1),
    primary = Color(0xFF34D399),
    primaryLight = Color(0xFFA7F3D0),
    isDark = true
)

val LightAppColors = AppColors(
    background = Color(0xFFF8FAFC),
    surface = Color(0xFFFFFFFF),
    card = Color(0xFFFFFFFF),
    cardElevated = Color(0xFFF1F5F9),
    border = Color(0xFFE2E8F0),
    textPrimary = Color(0xFF0F172A),
    textMuted = Color(0xFF64748B),
    textSecondary = Color(0xFF334155),
    primary = Color(0xFF059669),
    primaryLight = Color(0xFFECFDF5),
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

private val DarkColorScheme =
  darkColorScheme(
    primary = EmeraldPrimary,
    onPrimary = Color(0xFF064E3B),
    primaryContainer = Color(0xFF065F46),
    onPrimaryContainer = EmeraldLight,
    secondary = AccentSky,
    onSecondary = Color(0xFF082F49),
    secondaryContainer = Color(0xFF075985),
    onSecondaryContainer = Color(0xFFBAE6FD),
    tertiary = AccentAmber,
    background = Color(0xFF0B1120),
    onBackground = Color(0xFFF8FAFC),
    surface = Color(0xFF111827),
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = Color(0xFF1E293B),
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = Color(0xFF334155),
    outlineVariant = Color(0xFF1E293B),
    error = AccentCoral,
    onError = Color.White
  )

private val LightColorScheme =
  lightColorScheme(
    primary = Color(0xFF059669),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFECFDF5),
    onPrimaryContainer = Color(0xFF064E3B),
    secondary = Color(0xFF0284C7),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE0F2FE),
    onSecondaryContainer = Color(0xFF0369A1),
    tertiary = Color(0xFFD97706),
    background = Color(0xFFF8FAFC),
    onBackground = Color(0xFF0F172A),
    surface = Color.White,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFF64748B),
    outline = Color(0xFFE2E8F0),
    outlineVariant = Color(0xFFF1F5F9),
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
