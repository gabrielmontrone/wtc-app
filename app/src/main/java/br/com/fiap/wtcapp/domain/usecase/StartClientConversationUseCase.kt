package br.com.fiap.wtcapp.domain.usecase

import br.com.fiap.wtcapp.domain.model.Conversation
import br.com.fiap.wtcapp.domain.repository.ConversationRepository
import javax.inject.Inject

/**
 * Operator-only: opens (or reuses) a conversation with a client account identified by email,
 * so the operator can message a real account that the client sees on login.
 */
class StartClientConversationUseCase
    @Inject
    constructor(
        private val repository: ConversationRepository,
    ) {
        suspend operator fun invoke(email: String): Result<Conversation> {
            val trimmed = email.trim()
            if (trimmed.isBlank()) {
                return Result.failure(IllegalArgumentException("Informe o e-mail do cliente"))
            }
            return repository.startConversationWithClient(trimmed)
        }
    }
