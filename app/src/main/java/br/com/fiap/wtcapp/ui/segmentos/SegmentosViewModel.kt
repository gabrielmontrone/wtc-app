package br.com.fiap.wtcapp.ui.segmentos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.fiap.wtcapp.domain.usecase.CreateSegmentUseCase
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
        private val createSegment: CreateSegmentUseCase,
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

        fun onAddSegmentClick() = _uiState.update { it.copy(addForm = AddSegmentForm()) }

        fun onAddSegmentDismiss() = _uiState.update { it.copy(addForm = null) }

        fun onFormNameChange(value: String) = updateForm { it.copy(name = value) }

        fun onFormVipChange(value: Boolean) = updateForm { it.copy(vip = value) }

        fun onFormActiveChange(value: Boolean) = updateForm { it.copy(active = value) }

        fun onFormMinScoreChange(value: String) = updateForm { it.copy(minScore = value.filter(Char::isDigit)) }

        fun onFormMinLoyaltyChange(value: String) = updateForm { it.copy(minLoyalty = value.filter(Char::isDigit)) }

        fun saveSegment() {
            val form = _uiState.value.addForm ?: return
            if (form.isSaving) return
            updateForm { it.copy(isSaving = true) }
            viewModelScope.launch {
                createSegment(
                    name = form.name,
                    vip = form.vip,
                    active = form.active,
                    minScore = form.minScore.toIntOrNull(),
                    minLoyalty = form.minLoyalty.toIntOrNull(),
                ).fold(
                    onSuccess = {
                        _uiState.update { it.copy(addForm = null) }
                        load()
                    },
                    onFailure = { error ->
                        _uiState.update {
                            it.copy(
                                addForm = it.addForm?.copy(isSaving = false),
                                errorMessage = error.message ?: "Erro ao criar segmento",
                            )
                        }
                    },
                )
            }
        }

        private fun updateForm(transform: (AddSegmentForm) -> AddSegmentForm) =
            _uiState.update { state -> state.addForm?.let { state.copy(addForm = transform(it)) } ?: state }
    }
