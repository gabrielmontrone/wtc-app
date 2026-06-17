package br.com.fiap.wtcapp.domain.model

/**
 * Aggregated view of the audit trail, derived from the events already fetched.
 *
 * Everything here is computed client-side from the latest events returned by the API, so it
 * reflects that window (see [AuditSummary.from]). The counts power the Auditoria dashboard.
 */
data class AuditSummary(
    val totalEvents: Int = 0,
    val suspiciousCount: Int = 0,
    val failedLoginCount: Int = 0,
    val sentMessageCount: Int = 0,
    /** Share of sent messages that were flagged as suspicious, 0–100; 0 when no messages. */
    val suspiciousRatePercent: Int = 0,
    /** Action -> count, ordered from most to least frequent. */
    val countsByAction: List<Pair<String, Int>> = emptyList(),
    /** Top actors by event count (email -> count), at most five. */
    val topUsers: List<Pair<String, Int>> = emptyList(),
    /** Suspicious messages by risk level (e.g. HIGH/MEDIUM/LOW -> count). Server-provided only. */
    val riskDistribution: List<Pair<String, Int>> = emptyList(),
) {
    companion object {
        private const val ACTION_SUSPICIOUS = "SUSPICIOUS_MESSAGE"
        private const val ACTION_LOGIN_FAILED = "LOGIN_FAILED"
        private const val ACTION_SEND_MESSAGE = "SEND_MESSAGE"
        private const val TOP_USERS_LIMIT = 5

        fun from(events: List<AuditEvent>): AuditSummary {
            if (events.isEmpty()) return AuditSummary()

            val byAction = events.groupingBy { it.action }.eachCount()
            val suspicious = byAction[ACTION_SUSPICIOUS] ?: 0
            val sent = byAction[ACTION_SEND_MESSAGE] ?: 0

            val topUsers =
                events
                    .mapNotNull { it.userEmail?.takeIf { email -> email.isNotBlank() } }
                    .groupingBy { it }
                    .eachCount()
                    .entries
                    .sortedByDescending { it.value }
                    .take(TOP_USERS_LIMIT)
                    .map { it.key to it.value }

            return AuditSummary(
                totalEvents = events.size,
                suspiciousCount = suspicious,
                failedLoginCount = byAction[ACTION_LOGIN_FAILED] ?: 0,
                sentMessageCount = sent,
                suspiciousRatePercent = if (sent > 0) suspicious * 100 / sent else 0,
                countsByAction = byAction.entries.sortedByDescending { it.value }.map { it.key to it.value },
                topUsers = topUsers,
            )
        }
    }
}
