package br.com.fiap.wtcapp.data.remote.dto

import br.com.fiap.wtcapp.domain.model.ChatMessage
import kotlinx.serialization.Serializable

@Serializable
data class MessageResponseDto(
    val id: String,
    val subject: String? = null,
    val content: String = "",
    val status: String? = null,
    val senderRole: String? = null,
)

@Serializable
data class ChatMessageRequestDto(
    val content: String,
)

fun MessageResponseDto.toDomain(): ChatMessage =
    ChatMessage(
        id = id,
        subject = subject,
        content = content,
        status = status,
        senderRole = senderRole,
    )
