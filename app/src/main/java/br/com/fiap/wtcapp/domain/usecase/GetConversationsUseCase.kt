package br.com.fiap.wtcapp.domain.usecase

import br.com.fiap.wtcapp.domain.model.Conversation
import br.com.fiap.wtcapp.domain.repository.ConversationRepository
import javax.inject.Inject

class GetConversationsUseCase
    @Inject
    constructor(
        private val repository: ConversationRepository,
    ) {
        suspend operator fun invoke(customerId: String): Result<List<Conversation>> {
            if (customerId.isBlank()) {
                return Result.failure(IllegalArgumentException("Cliente não informado"))
            }
            return repository.conversations(customerId)
        }
    }
