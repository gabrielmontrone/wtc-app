package br.com.fiap.wtcapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.fiap.wtcapp.domain.model.ThemeMode

/**
 * App-level theme wrapper: resolves the persisted [ThemeMode] into light/dark and applies
 * [WTCTheme]. Used by every Activity (which must be `@AndroidEntryPoint`). Previews call
 * [WTCTheme] directly since they have no ViewModel.
 */
@Composable
fun WtcAppTheme(content: @Composable () -> Unit) {
    val viewModel: ThemeViewModel = hiltViewModel()
    val mode by viewModel.themeMode.collectAsStateWithLifecycle()
    val darkTheme =
        when (mode) {
            ThemeMode.SYSTEM -> isSystemInDarkTheme()
            ThemeMode.LIGHT -> false
            ThemeMode.DARK -> true
        }
    WTCTheme(darkTheme = darkTheme, content = content)
}
