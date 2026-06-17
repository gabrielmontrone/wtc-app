package br.com.fiap.wtcapp.ui.auditoria

import br.com.fiap.wtcapp.domain.model.AuditEvent
import br.com.fiap.wtcapp.domain.model.AuditSummary

data class AuditoriaUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val events: List<AuditEvent> = emptyList(),
    val summary: AuditSummary = AuditSummary(),
)
