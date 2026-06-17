package br.com.fiap.wtcapp.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AuditSummaryTest {
    private fun event(
        action: String,
        email: String? = "user@wtc.com",
    ) = AuditEvent(
        id = action + email,
        action = action,
        userEmail = email,
        details = null,
        timestamp = "t",
    )

    @Test
    fun `empty events produce an empty summary`() {
        val summary = AuditSummary.from(emptyList())

        assertEquals(0, summary.totalEvents)
        assertEquals(0, summary.suspiciousCount)
        assertEquals(0, summary.suspiciousRatePercent)
        assertTrue(summary.countsByAction.isEmpty())
        assertTrue(summary.topUsers.isEmpty())
    }

    @Test
    fun `counts each action and totals events`() {
        val events =
            listOf(
                event("SEND_MESSAGE"),
                event("SEND_MESSAGE"),
                event("SUSPICIOUS_MESSAGE"),
                event("LOGIN_FAILED"),
                event("LOGIN_SUCCESS"),
            )

        val summary = AuditSummary.from(events)

        assertEquals(5, summary.totalEvents)
        assertEquals(1, summary.suspiciousCount)
        assertEquals(1, summary.failedLoginCount)
        assertEquals(2, summary.sentMessageCount)
    }

    @Test
    fun `suspicious rate is the percentage of sent messages flagged`() {
        val events =
            listOf(
                event("SEND_MESSAGE"),
                event("SEND_MESSAGE"),
                event("SEND_MESSAGE"),
                event("SEND_MESSAGE"),
                event("SUSPICIOUS_MESSAGE"),
            )

        // 1 suspicious out of 4 sent messages -> 25%.
        assertEquals(25, AuditSummary.from(events).suspiciousRatePercent)
    }

    @Test
    fun `suspicious rate is zero when there are no messages`() {
        val events = listOf(event("LOGIN_SUCCESS"), event("LOGIN_FAILED"))

        assertEquals(0, AuditSummary.from(events).suspiciousRatePercent)
    }

    @Test
    fun `countsByAction is ordered from most to least frequent`() {
        val events =
            listOf(
                event("LOGIN_SUCCESS"),
                event("SEND_MESSAGE"),
                event("SEND_MESSAGE"),
                event("SEND_MESSAGE"),
                event("LOGIN_FAILED"),
                event("LOGIN_FAILED"),
            )

        val ordered = AuditSummary.from(events).countsByAction

        assertEquals("SEND_MESSAGE" to 3, ordered.first())
        assertEquals("LOGIN_FAILED" to 2, ordered[1])
    }

    @Test
    fun `topUsers ranks actors by event count and caps at five`() {
        val events =
            buildList {
                repeat(3) { add(event("SEND_MESSAGE", "ana@wtc.com")) }
                repeat(1) { add(event("SEND_MESSAGE", "bia@wtc.com")) }
                repeat(6) { i -> add(event("LOGIN_SUCCESS", "user$i@wtc.com")) }
                add(event("SEND_MESSAGE", null))
                add(event("SEND_MESSAGE", ""))
            }

        val top = AuditSummary.from(events).topUsers

        assertEquals(5, top.size)
        assertEquals("ana@wtc.com" to 3, top.first())
        assertTrue(top.none { it.first.isBlank() })
    }
}
