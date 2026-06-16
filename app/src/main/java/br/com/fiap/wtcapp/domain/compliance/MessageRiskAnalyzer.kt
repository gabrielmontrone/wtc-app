package br.com.fiap.wtcapp.domain.compliance

import javax.inject.Inject

/** Severity of a flagged message. Ordered so the analyzer can take the maximum. */
enum class RiskLevel {
    NONE,
    LOW,
    MEDIUM,
    HIGH,
    ;

    fun isSuspicious(): Boolean = ordinal >= MEDIUM.ordinal
}

/** Result of a client-side scan: a level plus the detected flag codes. */
data class MessageRiskResult(
    val level: RiskLevel,
    val flags: List<String>,
)

/**
 * Lightweight, on-device mirror of the backend DLP engine. It powers the
 * warn-before-send dialog so the operator is alerted *before* sensitive data
 * leaves the device; the backend remains the authoritative source of truth.
 */
class MessageRiskAnalyzer
    @Inject
    constructor() {
        fun analyze(content: String): MessageRiskResult {
            if (content.isBlank()) return MessageRiskResult(RiskLevel.NONE, emptyList())

            val flags = mutableListOf<String>()
            var level = RiskLevel.NONE

            if (containsValidCard(content)) {
                flags += FLAG_CARD
                level = maxOf(level, RiskLevel.HIGH)
            }
            if (CPF.containsMatchIn(content)) {
                flags += FLAG_CPF
                level = maxOf(level, RiskLevel.MEDIUM)
            }
            if (CNPJ.containsMatchIn(content)) {
                flags += FLAG_CNPJ
                level = maxOf(level, RiskLevel.MEDIUM)
            }
            if (containsSuspiciousLink(content)) {
                flags += FLAG_LINK
                level = maxOf(level, RiskLevel.MEDIUM)
            }

            return MessageRiskResult(level, flags)
        }

        private fun containsValidCard(content: String): Boolean =
            CARD_CANDIDATE.findAll(content).any { match ->
                val digits = match.value.replace(Regex("[ -]"), "")
                digits.length in CARD_MIN..CARD_MAX && isLuhnValid(digits)
            }

        private fun isLuhnValid(digits: String): Boolean {
            var sum = 0
            var doubleDigit = false
            for (i in digits.indices.reversed()) {
                var d = digits[i] - '0'
                if (doubleDigit) {
                    d *= 2
                    if (d > NINE) d -= NINE
                }
                sum += d
                doubleDigit = !doubleDigit
            }
            return sum % TEN == 0
        }

        private fun containsSuspiciousLink(content: String): Boolean {
            if (RAW_IP_URL.containsMatchIn(content)) return true
            return URL.findAll(content).any { match ->
                val url = match.value
                url.startsWith("http://", ignoreCase = true) || SHORTENERS.contains(host(url))
            }
        }

        private fun host(url: String): String {
            var h =
                url
                    .replaceFirst(Regex("(?i)^https?://"), "")
                    .replaceFirst(Regex("(?i)^www\\."), "")
            h = h.substringBefore('/').substringBefore(':')
            return h.lowercase()
        }

        private companion object {
            const val FLAG_CPF = "CPF"
            const val FLAG_CNPJ = "CNPJ"
            const val FLAG_CARD = "CARD"
            const val FLAG_LINK = "SUSPICIOUS_LINK"
            const val CARD_MIN = 13
            const val CARD_MAX = 19
            const val NINE = 9
            const val TEN = 10

            val CPF = Regex("(?<!\\d)\\d{3}\\.?\\d{3}\\.?\\d{3}-?\\d{2}(?!\\d)")
            val CNPJ = Regex("(?<!\\d)\\d{2}\\.?\\d{3}\\.?\\d{3}/?\\d{4}-?\\d{2}(?!\\d)")
            val CARD_CANDIDATE = Regex("(?<!\\d)\\d(?:[ -]?\\d){12,18}(?!\\d)")
            val URL = Regex("(?i)\\b(?:https?://|www\\.)\\S+")
            val RAW_IP_URL = Regex("(?i)https?://\\d{1,3}(?:\\.\\d{1,3}){3}")
            val SHORTENERS =
                setOf(
                    "bit.ly", "tinyurl.com", "t.co", "goo.gl", "ow.ly",
                    "is.gd", "buff.ly", "cutt.ly", "rebrand.ly",
                )
        }
    }
