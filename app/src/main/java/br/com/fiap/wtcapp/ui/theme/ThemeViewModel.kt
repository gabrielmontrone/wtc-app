package br.com.fiap.wtcapp.ui.theme

import androidx.lifecycle.ViewModel
import br.com.fiap.wtcapp.data.local.ThemePreferences
import br.com.fiap.wtcapp.domain.model.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel
    @Inject
    constructor(
        private val themePreferences: ThemePreferences,
    ) : ViewModel() {
        val themeMode: StateFlow<ThemeMode> = themePreferences.themeMode

        fun setThemeMode(mode: ThemeMode) = themePreferences.setThemeMode(mode)
    }
