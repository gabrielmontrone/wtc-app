package br.com.fiap.wtcapp.data.remote.dto

import br.com.fiap.wtcapp.domain.model.AuditSummary
import kotlinx.serialization.Serializable

@Serializable
data class LabelCountDto(
    val label: String = "",
    val count: Int = 0,
)

@Serializable
data class AuditSummaryDto(
    val totalEvents: Int = 0,
    val suspiciousCount: Int = 0,
    val failedLoginCount: Int = 0,
    val sentMessageCount: Int = 0,
    val suspiciousRatePercent: Int = 0,
    val countsByAction: List<LabelCountDto> = emptyList(),
    val riskDistribution: List<LabelCountDto> = emptyList(),
    val topUsers: List<LabelCountDto> = emptyList(),
)

fun AuditSummaryDto.toDomain(): AuditSummary =
    AuditSummary(
        totalEvents = totalEvents,
        suspiciousCount = suspiciousCount,
        failedLoginCount = failedLoginCount,
        sentMessageCount = sentMessageCount,
        suspiciousRatePercent = suspiciousRatePercent,
        countsByAction = countsByAction.map { it.label to it.count },
        riskDistribution = riskDistribution.map { it.label to it.count },
        topUsers = topUsers.map { it.label to it.count },
    )
