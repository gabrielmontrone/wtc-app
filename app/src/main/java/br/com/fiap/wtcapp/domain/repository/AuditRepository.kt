package br.com.fiap.wtcapp.domain.repository

import br.com.fiap.wtcapp.domain.model.AuditEvent

interface AuditRepository {
    /** Returns the most recent audit-trail entries (newest first). */
    suspend fun events(): Result<List<AuditEvent>>
}
