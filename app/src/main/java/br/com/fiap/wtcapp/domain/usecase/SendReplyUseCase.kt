package br.com.fiap.wtcapp.domain.usecase

import br.com.fiap.wtcapp.domain.model.ChatMessage
import br.com.fiap.wtcapp.domain.repository.MessageRepository
import javax.inject.Inject

class SendReplyUseCase
    @Inject
    constructor(
        private val repository: MessageRepository,
    ) {
        suspend operator fun invoke(
            conversationId: String,
            content: String,
            imageUrl: String? = null,
        ): Result<ChatMessage> {
            val trimmed = content.trim()
            if (trimmed.isBlank() && imageUrl.isNullOrBlank()) {
                return Result.failure(IllegalArgumentException("Digite uma mensagem ou anexe uma foto"))
            }
            return repository.sendReply(conversationId, trimmed, imageUrl?.takeIf { it.isNotBlank() })
        }
    }
