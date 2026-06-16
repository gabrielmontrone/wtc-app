package br.com.fiap.wtcapp.domain.model

/** A single message within a conversation. */
data class ChatMessage(
    val id: String,
    val subject: String?,
    val content: String,
    val status: String?,
    val senderRole: String?,
    /** Public URL of an attached photo, when the message carries one. */
    val imageUrl: String? = null,
)
