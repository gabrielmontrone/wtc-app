package br.com.fiap.wtcapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.fiap.wtcapp.domain.model.ThemeMode

/**
 * App-level theme wrapper: resolves the persisted [ThemeMode] into light/dark and applies
 * [WTCTheme]. Used by every Activity (which must be `@AndroidEntryPoint`). Previews call
 * [WTCTheme] directly since they have no ViewModel.
 *
 * The app targets an edge-to-edge SDK, so content would otherwise draw under the status and
 * navigation bars. [systemBarsPadding] insets every screen away from those bars in one place;
 * it also consumes the insets, so inner [androidx.compose.material3.Scaffold]s don't double-pad.
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
    WTCTheme(darkTheme = darkTheme) {
        Box(modifier = Modifier.fillMaxSize().systemBarsPadding()) {
            content()
        }
    }
}
