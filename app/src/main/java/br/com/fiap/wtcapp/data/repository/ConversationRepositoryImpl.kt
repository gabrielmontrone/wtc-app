package br.com.fiap.wtcapp.data.repository

import br.com.fiap.wtcapp.data.remote.WtcApi
import br.com.fiap.wtcapp.data.remote.dto.ConversationRequestDto
import br.com.fiap.wtcapp.data.remote.dto.StartClientConversationRequestDto
import br.com.fiap.wtcapp.data.remote.dto.toDomain
import br.com.fiap.wtcapp.di.IoDispatcher
import br.com.fiap.wtcapp.domain.model.Conversation
import br.com.fiap.wtcapp.domain.repository.ConversationRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ConversationRepositoryImpl
    @Inject
    constructor(
        private val api: WtcApi,
        @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    ) : ConversationRepository {
        override suspend fun conversations(customerId: String): Result<List<Conversation>> =
            withContext(ioDispatcher) {
                runCatching { api.listConversations(customerId).map { it.toDomain() } }
            }

        override suspend fun startConversation(customerId: String): Result<Conversation> =
            withContext(ioDispatcher) {
                runCatching { api.createConversation(ConversationRequestDto(customerId)).toDomain() }
            }

        override suspend fun startConversationWithClient(email: String): Result<Conversation> =
            withContext(ioDispatcher) {
                runCatching { api.startConversationWithClient(StartClientConversationRequestDto(email)).toDomain() }
            }
    }
