package br.com.fiap.wtcapp.ui.auditoria

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.fiap.wtcapp.domain.model.AuditSummary
import br.com.fiap.wtcapp.domain.usecase.GetAuditEventsUseCase
import br.com.fiap.wtcapp.domain.usecase.GetAuditSummaryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuditoriaViewModel
    @Inject
    constructor(
        private val getAuditEvents: GetAuditEventsUseCase,
        private val getAuditSummary: GetAuditSummaryUseCase,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(AuditoriaUiState())
        val uiState: StateFlow<AuditoriaUiState> = _uiState.asStateFlow()

        init {
            load()
        }

        fun load() {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            viewModelScope.launch {
                getAuditEvents().fold(
                    onSuccess = { events ->
                        // Show the list with a client-side summary immediately, then enrich it with
                        // the server-side aggregation (accurate totals + risk distribution).
                        _uiState.update {
                            it.copy(isLoading = false, events = events, summary = AuditSummary.from(events))
                        }
                        getAuditSummary().onSuccess { serverSummary ->
                            _uiState.update { it.copy(summary = serverSummary) }
                        }
                    },
                    onFailure = { error ->
                        _uiState.update {
                            it.copy(isLoading = false, errorMessage = error.message ?: "Erro ao carregar auditoria")
                        }
                    },
                )
            }
        }

        fun onErrorShown() = _uiState.update { it.copy(errorMessage = null) }
    }
