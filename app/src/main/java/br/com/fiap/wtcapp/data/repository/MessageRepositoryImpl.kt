package br.com.fiap.wtcapp.data.repository

import br.com.fiap.wtcapp.data.remote.WtcApi
import br.com.fiap.wtcapp.data.remote.dto.ChatMessageRequestDto
import br.com.fiap.wtcapp.data.remote.dto.toDomain
import br.com.fiap.wtcapp.di.IoDispatcher
import br.com.fiap.wtcapp.domain.model.ChatMessage
import br.com.fiap.wtcapp.domain.repository.MessageRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MessageRepositoryImpl
    @Inject
    constructor(
        private val api: WtcApi,
        @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    ) : MessageRepository {
        override suspend fun messages(conversationId: String): Result<List<ChatMessage>> =
            withContext(ioDispatcher) {
                runCatching { api.listMessages(conversationId).map { it.toDomain() } }
            }

        override suspend fun sendReply(
            conversationId: String,
            content: String,
        ): Result<ChatMessage> =
            withContext(ioDispatcher) {
                runCatching { api.sendReply(conversationId, ChatMessageRequestDto(content)).toDomain() }
            }
    }
