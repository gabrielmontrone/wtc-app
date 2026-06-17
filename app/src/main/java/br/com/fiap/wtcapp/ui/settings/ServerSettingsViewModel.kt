package br.com.fiap.wtcapp.ui.settings

import androidx.lifecycle.ViewModel
import br.com.fiap.wtcapp.data.local.ServerConfigStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/** Exposes the runtime backend URL and lets the user change it from the server settings dialog. */
@HiltViewModel
class ServerSettingsViewModel
    @Inject
    constructor(
        private val storage: ServerConfigStorage,
    ) : ViewModel() {
        private val _baseUrl = MutableStateFlow(storage.baseUrl())
        val baseUrl: StateFlow<String> = _baseUrl.asStateFlow()

        val defaultBaseUrl: String get() = storage.defaultBaseUrl()

        fun save(url: String) {
            storage.saveBaseUrl(url)
            _baseUrl.value = storage.baseUrl()
        }
    }
