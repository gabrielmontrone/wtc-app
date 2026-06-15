package br.com.fiap.wtcapp.ui.contatos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.fiap.wtcapp.domain.usecase.GetCustomersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContatosViewModel
    @Inject
    constructor(
        private val getCustomers: GetCustomersUseCase,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(ContatosUiState())
        val uiState: StateFlow<ContatosUiState> = _uiState.asStateFlow()

        init {
            load()
        }

        fun load() {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            viewModelScope.launch {
                getCustomers().fold(
                    onSuccess = { customers ->
                        _uiState.update { it.copy(isLoading = false, customers = customers) }
                    },
                    onFailure = { error ->
                        _uiState.update {
                            it.copy(isLoading = false, errorMessage = error.message ?: "Erro ao carregar contatos")
                        }
                    },
                )
            }
        }

        fun onSearchChange(value: String) = _uiState.update { it.copy(search = value) }

        fun onFilterChange(filter: ContatoFiltro) = _uiState.update { it.copy(filter = filter) }

        fun onErrorShown() = _uiState.update { it.copy(errorMessage = null) }
    }
