package br.com.fiap.wtcapp.ui.home

import br.com.fiap.wtcapp.FakeAuthRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class HomeViewModelTest {
    @Test
    fun `operator role unlocks the full console`() {
        val viewModel = HomeViewModel(FakeAuthRepository(roleValue = "OPERADOR", userIdValue = "op1"))

        assertTrue(viewModel.isOperator)
    }

    @Test
    fun `client role is not operator and exposes own conversation id`() {
        val viewModel = HomeViewModel(FakeAuthRepository(roleValue = "CLIENTE", userIdValue = "client1"))

        assertFalse(viewModel.isOperator)
        assertEquals("client1", viewModel.conversationId)
    }

    @Test
    fun `missing role is treated as non-operator`() {
        val viewModel = HomeViewModel(FakeAuthRepository(roleValue = null, userIdValue = null))

        assertFalse(viewModel.isOperator)
    }
}
