package br.com.fiap.wtcapp.domain.compliance

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MessageRiskAnalyzerTest {
    private val analyzer = MessageRiskAnalyzer()

    @Test
    fun `clean message has no risk`() {
        val result = analyzer.analyze("Olá, podemos confirmar a reunião?")
        assertEquals(RiskLevel.NONE, result.level)
        assertTrue(result.flags.isEmpty())
    }

    @Test
    fun `blank message is clean`() {
        assertEquals(RiskLevel.NONE, analyzer.analyze("   ").level)
    }

    @Test
    fun `detects cpf as medium`() {
        val result = analyzer.analyze("CPF 529.982.247-25")
        assertEquals(RiskLevel.MEDIUM, result.level)
        assertTrue(result.flags.contains("CPF"))
        assertTrue(result.level.isSuspicious())
    }

    @Test
    fun `detects valid card as high`() {
        val result = analyzer.analyze("cartão 4111 1111 1111 1111")
        assertEquals(RiskLevel.HIGH, result.level)
        assertTrue(result.flags.contains("CARD"))
    }

    @Test
    fun `ignores digits that fail luhn`() {
        val result = analyzer.analyze("pedido 1234 5678 9012 3456")
        assertFalse(result.flags.contains("CARD"))
        assertEquals(RiskLevel.NONE, result.level)
    }

    @Test
    fun `flags http link and shortener`() {
        assertTrue(analyzer.analyze("veja http://promo.example.com").flags.contains("SUSPICIOUS_LINK"))
        assertTrue(analyzer.analyze("https://bit.ly/abc").flags.contains("SUSPICIOUS_LINK"))
    }

    @Test
    fun `allows plain https link`() {
        val result = analyzer.analyze("site https://www.wtc.com.br")
        assertFalse(result.flags.contains("SUSPICIOUS_LINK"))
        assertEquals(RiskLevel.NONE, result.level)
    }
}
