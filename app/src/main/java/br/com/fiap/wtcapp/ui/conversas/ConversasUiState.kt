package br.com.fiap.wtcapp.ui.conversas

import br.com.fiap.wtcapp.domain.model.Conversation

data class ConversasUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val conversations: List<Conversation> = emptyList(),
    val isStarting: Boolean = false,
)
