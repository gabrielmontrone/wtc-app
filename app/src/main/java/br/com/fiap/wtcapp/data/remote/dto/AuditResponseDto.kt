package br.com.fiap.wtcapp.data.remote.dto

import br.com.fiap.wtcapp.domain.model.AuditEvent
import kotlinx.serialization.Serializable

@Serializable
data class AuditResponseDto(
    val id: String,
    val action: String = "",
    val userEmail: String? = null,
    val details: String? = null,
    val timestamp: String = "",
)

fun AuditResponseDto.toDomain(): AuditEvent =
    AuditEvent(
        id = id,
        action = action,
        userEmail = userEmail,
        details = details,
        timestamp = timestamp,
    )
