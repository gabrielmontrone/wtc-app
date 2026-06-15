package br.com.fiap.wtcapp.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColors =
    lightColorScheme(
        primary = Ink,
        onPrimary = Paper,
        secondary = Ink,
        onSecondary = Paper,
        background = LightBackground,
        onBackground = Ink,
        surface = LightSurface,
        onSurface = Ink,
        surfaceVariant = LightSurfaceVariant,
        onSurfaceVariant = LightMuted,
        outline = LightOutline,
        outlineVariant = LightOutline,
        error = LightError,
        onError = Paper,
    )

private val DarkColors =
    darkColorScheme(
        primary = Paper,
        onPrimary = Ink,
        secondary = Paper,
        onSecondary = Ink,
        background = CarbonBackground,
        onBackground = Paper,
        surface = CarbonSurface,
        onSurface = Paper,
        surfaceVariant = CarbonSurfaceVariant,
        onSurfaceVariant = CarbonMuted,
        outline = CarbonOutline,
        outlineVariant = CarbonOutline,
        error = DarkError,
        onError = Ink,
    )

/**
 * Pure themer used by @Preview and by [WtcAppTheme]. Applies the C6 Carbon palette
 * (dynamic color intentionally disabled so the brand identity stays consistent).
 */
@Composable
fun WTCTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}
