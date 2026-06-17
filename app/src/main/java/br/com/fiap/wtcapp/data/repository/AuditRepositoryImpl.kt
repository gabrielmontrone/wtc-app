package br.com.fiap.wtcapp.data.repository

import br.com.fiap.wtcapp.data.remote.WtcApi
import br.com.fiap.wtcapp.data.remote.dto.toDomain
import br.com.fiap.wtcapp.di.IoDispatcher
import br.com.fiap.wtcapp.domain.model.AuditEvent
import br.com.fiap.wtcapp.domain.model.AuditSummary
import br.com.fiap.wtcapp.domain.repository.AuditRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AuditRepositoryImpl
    @Inject
    constructor(
        private val api: WtcApi,
        @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    ) : AuditRepository {
        override suspend fun events(): Result<List<AuditEvent>> =
            withContext(ioDispatcher) {
                runCatching { api.listAuditEvents().map { it.toDomain() } }
            }

        override suspend fun summary(): Result<AuditSummary> =
            withContext(ioDispatcher) {
                runCatching { api.getAuditSummary().toDomain() }
            }
    }
