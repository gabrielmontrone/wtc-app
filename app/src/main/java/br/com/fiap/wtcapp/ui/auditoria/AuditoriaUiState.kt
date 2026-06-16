package br.com.fiap.wtcapp.ui.auditoria

import br.com.fiap.wtcapp.domain.model.AuditEvent

data class AuditoriaUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val events: List<AuditEvent> = emptyList(),
)
