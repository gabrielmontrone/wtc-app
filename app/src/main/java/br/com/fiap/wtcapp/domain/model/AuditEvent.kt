package br.com.fiap.wtcapp.domain.model

/** A single entry from the compliance audit trail. */
data class AuditEvent(
    val id: String,
    val action: String,
    val userEmail: String?,
    val details: String?,
    val timestamp: String,
)
