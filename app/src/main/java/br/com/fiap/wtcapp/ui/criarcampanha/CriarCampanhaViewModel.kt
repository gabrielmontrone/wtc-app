package br.com.fiap.wtcapp.ui.criarcampanha

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.fiap.wtcapp.domain.usecase.CreateCampaignUseCase
import br.com.fiap.wtcapp.domain.usecase.GetSegmentsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CriarCampanhaViewModel
    @Inject
    constructor(
        private val getSegments: GetSegmentsUseCase,
        private val createCampaign: CreateCampaignUseCase,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(CriarCampanhaUiState())
        val uiState: StateFlow<CriarCampanhaUiState> = _uiState.asStateFlow()

        init {
            loadSegments()
        }

        private fun loadSegments() {
            _uiState.update { it.copy(isLoadingSegments = true) }
            viewModelScope.launch {
                getSegments().fold(
                    onSuccess = { segments ->
                        _uiState.update {
                            it.copy(
                                isLoadingSegments = false,
                                segments = segments,
                                selectedSegmentId = it.selectedSegmentId ?: segments.firstOrNull()?.id,
                            )
                        }
                    },
                    onFailure = { error ->
                        _uiState.update {
                            it.copy(isLoadingSegments = false, errorMessage = error.message ?: "Erro ao carregar segmentos")
                        }
                    },
                )
            }
        }

        fun onTitleChange(value: String) = _uiState.update { it.copy(title = value) }

        fun onMessageChange(value: String) = _uiState.update { it.copy(message = value) }

        fun onSegmentSelect(id: String) = _uiState.update { it.copy(selectedSegmentId = id) }

        fun onErrorShown() = _uiState.update { it.copy(errorMessage = null) }

        fun submit() {
            val current = _uiState.value
            if (current.isSubmitting) return
            _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }
            viewModelScope.launch {
                createCampaign(current.title, current.message, current.selectedSegmentId).fold(
                    onSuccess = { _uiState.update { it.copy(isSubmitting = false, isCreated = true) } },
                    onFailure = { error ->
                        _uiState.update {
                            it.copy(isSubmitting = false, errorMessage = error.message ?: "Erro ao criar campanha")
                        }
                    },
                )
            }
        }
    }
