package br.com.fiap.wtcapp.domain.repository

import br.com.fiap.wtcapp.domain.model.Conversation

interface ConversationRepository {
    suspend fun conversations(customerId: String): Result<List<Conversation>>

    suspend fun startConversation(customerId: String): Result<Conversation>

    /** Operator-only: opens (or reuses) a conversation with a client account, found by email. */
    suspend fun startConversationWithClient(email: String): Result<Conversation>
}
