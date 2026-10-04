package com.localtime.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DayColors = lightColorScheme(
    primary = Color(0xFF3867FF),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDDE5FF),
    onPrimaryContainer = Color(0xFF081944),
    secondary = Color(0xFF6B58C8),
    secondaryContainer = Color(0xFFE9E3FF),
    onSecondaryContainer = Color(0xFF21164F),
    tertiary = Color(0xFFE47A32),
    tertiaryContainer = Color(0xFFFFDBC7),
    onTertiaryContainer = Color(0xFF351300),
    background = Color(0xFFF3F6FC),
    onBackground = Color(0xFF161A24),
    surface = Color(0xFFF7F9FD),
    surfaceVariant = Color(0xFFE7EBF4),
    onSurface = Color(0xFF151821),
    onSurfaceVariant = Color(0xFF5D6474),
    outline = Color(0xFF8991A2),
)

private val NightColors = darkColorScheme(
    primary = Color(0xFFAFC2FF),
    onPrimary = Color(0xFF122047),
    primaryContainer = Color(0xFF263865),
    onPrimaryContainer = Color(0xFFDCE4FF),
    secondary = Color(0xFFD0C2FF),
    secondaryContainer = Color(0xFF3A3264),
    onSecondaryContainer = Color(0xFFEAE2FF),
    tertiary = Color(0xFFFFB787),
    tertiaryContainer = Color(0xFF693D20),
    onTertiaryContainer = Color(0xFFFFDBCB),
    background = Color(0xFF070B12),
    onBackground = Color(0xFFE6EAF4),
    surface = Color(0xFF0D131D),
    surfaceVariant = Color(0xFF1B2330),
    onSurface = Color(0xFFE7EBF4),
    onSurfaceVariant = Color(0xFFB1B8C7),
    outline = Color(0xFF6F7788),
)

enum class ThemeMode { SYSTEM, LIGHT, DARK }

@Composable
fun LocalTimeTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit,
) {
    val dark = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    MaterialTheme(
        colorScheme = if (dark) NightColors else DayColors,
        typography = Typography(),
        content = content,
    )
}
