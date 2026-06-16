package br.com.fiap.wtcapp.domain.usecase

import br.com.fiap.wtcapp.domain.model.AuditEvent
import br.com.fiap.wtcapp.domain.repository.AuditRepository
import javax.inject.Inject

class GetAuditEventsUseCase
    @Inject
    constructor(
        private val repository: AuditRepository,
    ) {
        suspend operator fun invoke(): Result<List<AuditEvent>> = repository.events()
    }
