package br.com.fiap.wtcapp.domain.model

/** A single message within a conversation. */
data class ChatMessage(
    val id: String,
    val subject: String?,
    val content: String,
    val status: String?,
    val senderRole: String?,
)
