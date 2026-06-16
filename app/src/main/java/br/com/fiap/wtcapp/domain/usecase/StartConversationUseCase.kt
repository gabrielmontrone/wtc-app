package br.com.fiap.wtcapp.domain.usecase

import br.com.fiap.wtcapp.domain.model.Conversation
import br.com.fiap.wtcapp.domain.repository.ConversationRepository
import javax.inject.Inject

/**
 * Starts a new conversation for a customer. Validation mirrors [GetConversationsUseCase]
 * so an empty customer id never reaches the network.
 */
class StartConversationUseCase
    @Inject
    constructor(
        private val repository: ConversationRepository,
    ) {
        suspend operator fun invoke(customerId: String): Result<Conversation> {
            if (customerId.isBlank()) {
                return Result.failure(IllegalArgumentException("Cliente não informado"))
            }
            return repository.startConversation(customerId)
        }
    }
