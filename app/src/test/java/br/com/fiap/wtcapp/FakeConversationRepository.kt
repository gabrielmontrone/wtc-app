package br.com.fiap.wtcapp

import br.com.fiap.wtcapp.domain.model.Conversation
import br.com.fiap.wtcapp.domain.repository.ConversationRepository

class FakeConversationRepository(
    private var result: Result<List<Conversation>> = Result.success(emptyList()),
) : ConversationRepository {
    var lastCustomerId: String? = null
        private set

    override suspend fun conversations(customerId: String): Result<List<Conversation>> {
        lastCustomerId = customerId
        return result
    }
}
