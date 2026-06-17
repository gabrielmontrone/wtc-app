package br.com.fiap.wtcapp.domain.usecase

import br.com.fiap.wtcapp.domain.model.AuditSummary
import br.com.fiap.wtcapp.domain.repository.AuditRepository
import javax.inject.Inject

class GetAuditSummaryUseCase
    @Inject
    constructor(
        private val repository: AuditRepository,
    ) {
        suspend operator fun invoke(): Result<AuditSummary> = repository.summary()
    }
