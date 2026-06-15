package br.com.fiap.wtcapp

import br.com.fiap.wtcapp.domain.model.ChatMessage
import br.com.fiap.wtcapp.domain.repository.MessageRepository

class FakeMessageRepository(
    private var listResult: Result<List<ChatMessage>> = Result.success(emptyList()),
    private var sendResult: Result<ChatMessage> =
        Result.success(ChatMessage("new", null, "enviado", "SENT", "OPERATOR")),
) : MessageRepository {
    var lastConversationId: String? = null
        private set
    var lastSentContent: String? = null
        private set

    override suspend fun messages(conversationId: String): Result<List<ChatMessage>> {
        lastConversationId = conversationId
        return listResult
    }

    override suspend fun sendReply(
        conversationId: String,
        content: String,
    ): Result<ChatMessage> {
        lastSentContent = content
        return sendResult
    }
}
