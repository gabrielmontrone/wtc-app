package br.com.fiap.wtcapp

import br.com.fiap.wtcapp.domain.model.AuditEvent
import br.com.fiap.wtcapp.domain.model.AuditSummary
import br.com.fiap.wtcapp.domain.repository.AuditRepository

class FakeAuditRepository(
    private var result: Result<List<AuditEvent>> = Result.success(emptyList()),
    private var summaryResult: Result<AuditSummary> = Result.success(AuditSummary()),
) : AuditRepository {
    var callCount = 0
        private set

    override suspend fun events(): Result<List<AuditEvent>> {
        callCount++
        return result
    }

    override suspend fun summary(): Result<AuditSummary> = summaryResult
}
