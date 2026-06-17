package br.com.fiap.wtcapp.data.remote.dto

import br.com.fiap.wtcapp.domain.model.Conversation
import kotlinx.serialization.Serializable

@Serializable
data class ConversationResponseDto(
    val id: String,
    val customerId: String = "",
    val operatorId: String? = null,
    val status: String = "",
)

@Serializable
data class ConversationRequestDto(
    val customerId: String,
)

@Serializable
data class StartClientConversationRequestDto(
    val email: String,
)

fun ConversationResponseDto.toDomain(): Conversation =
    Conversation(
        id = id,
        customerId = customerId,
        operatorId = operatorId,
        status = status,
    )
