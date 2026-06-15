package br.com.fiap.wtcapp.data.local

import android.content.Context
import androidx.core.content.edit
import br.com.fiap.wtcapp.domain.model.ThemeMode
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Persists the user's [ThemeMode] choice and exposes it as a [StateFlow] so the whole
 * app re-themes instantly when it changes. Plain SharedPreferences — it is not sensitive.
 */
@Singleton
class ThemePreferences
    @Inject
    constructor(
        @ApplicationContext context: Context,
    ) {
        private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        private val _themeMode = MutableStateFlow(read())
        val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

        fun setThemeMode(mode: ThemeMode) {
            prefs.edit { putString(KEY_MODE, mode.name) }
            _themeMode.value = mode
        }

        private fun read(): ThemeMode =
            prefs.getString(KEY_MODE, null)
                ?.let { stored -> runCatching { ThemeMode.valueOf(stored) }.getOrNull() }
                ?: ThemeMode.SYSTEM

        private companion object {
            const val PREFS_NAME = "wtc_theme"
            const val KEY_MODE = "theme_mode"
        }
    }
