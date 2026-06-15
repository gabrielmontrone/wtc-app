package br.com.fiap.wtcapp.ui.segmentos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.fiap.wtcapp.domain.usecase.GetSegmentsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SegmentosViewModel
    @Inject
    constructor(
        private val getSegments: GetSegmentsUseCase,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(SegmentosUiState())
        val uiState: StateFlow<SegmentosUiState> = _uiState.asStateFlow()

        init {
            load()
        }

        fun load() {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            viewModelScope.launch {
                getSegments().fold(
                    onSuccess = { segments ->
                        _uiState.update { it.copy(isLoading = false, segments = segments) }
                    },
                    onFailure = { error ->
                        _uiState.update {
                            it.copy(isLoading = false, errorMessage = error.message ?: "Erro ao carregar segmentos")
                        }
                    },
                )
            }
        }

        fun onSearchChange(value: String) = _uiState.update { it.copy(search = value) }

        fun onErrorShown() = _uiState.update { it.copy(errorMessage = null) }
    }
