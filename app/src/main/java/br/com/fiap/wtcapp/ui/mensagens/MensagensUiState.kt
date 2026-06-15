package br.com.fiap.wtcapp.ui.mensagens

import br.com.fiap.wtcapp.domain.model.ChatMessage

data class MensagensUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val messages: List<ChatMessage> = emptyList(),
    val reply: String = "",
    val isSending: Boolean = false,
) {
    val canSend: Boolean
        get() = !isSending && reply.isNotBlank()
}
