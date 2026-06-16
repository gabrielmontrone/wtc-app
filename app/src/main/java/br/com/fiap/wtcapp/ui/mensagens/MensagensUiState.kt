package br.com.fiap.wtcapp.ui.mensagens

import br.com.fiap.wtcapp.domain.model.Campaign
import br.com.fiap.wtcapp.domain.model.ChatMessage

data class MensagensUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val messages: List<ChatMessage> = emptyList(),
    val reply: String = "",
    val isSending: Boolean = false,
    val campaigns: List<Campaign> = emptyList(),
    val isUploading: Boolean = false,
    val pendingImageUrl: String? = null,
) {
    val canSend: Boolean
        get() = !isSending && !isUploading && (reply.isNotBlank() || pendingImageUrl != null)

    /**
     * Campaign templates that match the current "/" command. Empty unless the reply
     * starts with "/". The text after "/" filters campaigns by name (case-insensitive).
     */
    val campaignSuggestions: List<Campaign>
        get() =
            if (reply.startsWith("/")) {
                val query = reply.drop(1).trim()
                campaigns.filter { campaign ->
                    campaign.content.isNotBlank() &&
                        (query.isBlank() || campaign.name.contains(query, ignoreCase = true))
                }
            } else {
                emptyList()
            }
}
