package br.com.fiap.wtcapp

import br.com.fiap.wtcapp.domain.model.Conversation
import br.com.fiap.wtcapp.domain.repository.ConversationRepository

class FakeConversationRepository(
    private var result: Result<List<Conversation>> = Result.success(emptyList()),
) : ConversationRepository {
    var lastCustomerId: String? = null
        private set
    var startCount = 0
        private set
    var lastStartedCustomerId: String? = null
        private set

    private var startResult: Result<Conversation>? = null

    fun setResult(result: Result<List<Conversation>>) {
        this.result = result
    }

    fun setStartResult(result: Result<Conversation>) {
        this.startResult = result
    }

    override suspend fun conversations(customerId: String): Result<List<Conversation>> {
        lastCustomerId = customerId
        return result
    }

    override suspend fun startConversation(customerId: String): Result<Conversation> {
        startCount++
        lastStartedCustomerId = customerId
        return startResult ?: Result.success(Conversation("new-conv", customerId, "op", "OPEN"))
    }

    var lastClientEmail: String? = null
        private set

    override suspend fun startConversationWithClient(email: String): Result<Conversation> {
        lastClientEmail = email
        return startResult ?: Result.success(Conversation("client-conv", "client-id", "op", "OPEN"))
    }
}
