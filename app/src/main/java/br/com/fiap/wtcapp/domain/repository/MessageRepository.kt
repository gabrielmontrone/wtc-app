package br.com.fiap.wtcapp.domain.repository

import br.com.fiap.wtcapp.domain.model.ChatMessage

interface MessageRepository {
    suspend fun messages(conversationId: String): Result<List<ChatMessage>>

    suspend fun sendReply(
        conversationId: String,
        content: String,
    ): Result<ChatMessage>
}
