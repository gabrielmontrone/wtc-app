package br.com.fiap.wtcapp.domain.repository

import br.com.fiap.wtcapp.domain.model.AuditEvent
import br.com.fiap.wtcapp.domain.model.AuditSummary

interface AuditRepository {
    /** Returns the most recent audit-trail entries (newest first). */
    suspend fun events(): Result<List<AuditEvent>>

    /** Returns the server-side aggregated summary for the dashboard. */
    suspend fun summary(): Result<AuditSummary>
}
