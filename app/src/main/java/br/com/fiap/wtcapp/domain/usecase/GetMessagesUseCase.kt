package br.com.fiap.wtcapp.domain.usecase

import br.com.fiap.wtcapp.domain.model.ChatMessage
import br.com.fiap.wtcapp.domain.repository.MessageRepository
import javax.inject.Inject

class GetMessagesUseCase
    @Inject
    constructor(
        private val repository: MessageRepository,
    ) {
        suspend operator fun invoke(conversationId: String): Result<List<ChatMessage>> {
            if (conversationId.isBlank()) {
                return Result.failure(IllegalArgumentException("Conversa não informada"))
            }
            return repository.messages(conversationId)
        }
    }
