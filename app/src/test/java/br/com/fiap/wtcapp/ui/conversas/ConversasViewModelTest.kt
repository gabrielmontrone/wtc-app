package br.com.fiap.wtcapp.ui.conversas

import androidx.lifecycle.SavedStateHandle
import br.com.fiap.wtcapp.ConversasActivity
import br.com.fiap.wtcapp.FakeConversationRepository
import br.com.fiap.wtcapp.MainDispatcherRule
import br.com.fiap.wtcapp.domain.model.Conversation
import br.com.fiap.wtcapp.domain.usecase.GetConversationsUseCase
import br.com.fiap.wtcapp.domain.usecase.StartConversationUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ConversasViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private fun handle(customerId: String?) =
        SavedStateHandle(buildMap { customerId?.let { put(ConversasActivity.EXTRA_CUSTOMER_ID, it) } })

    @Test
    fun `loads conversations for the customer from the saved state`() =
        runTest {
            val repository =
                FakeConversationRepository(
                    Result.success(listOf(Conversation("c1", "cust1", "op", "OPEN"))),
                )

            val viewModel = ConversasViewModel(GetConversationsUseCase(repository), StartConversationUseCase(repository), handle("cust1"))

            assertEquals(1, viewModel.uiState.value.conversations.size)
            assertEquals("cust1", repository.lastCustomerId)
        }

    @Test
    fun `missing customer id surfaces an error`() =
        runTest {
            val repository = FakeConversationRepository()

            val viewModel = ConversasViewModel(GetConversationsUseCase(repository), StartConversationUseCase(repository), handle(null))

            assertNotNull(viewModel.uiState.value.errorMessage)
        }

    @Test
    fun `starting a conversation creates it and reloads the list`() =
        runTest {
            val repository = FakeConversationRepository(Result.success(emptyList()))
            val viewModel = ConversasViewModel(GetConversationsUseCase(repository), StartConversationUseCase(repository), handle("cust1"))

            viewModel.startNewConversation()

            assertEquals(1, repository.startCount)
            assertEquals("cust1", repository.lastStartedCustomerId)
            // the list is reloaded after a successful start (initial load + reload)
            assertEquals("cust1", repository.lastCustomerId)
            assertFalse(viewModel.uiState.value.isStarting)
        }
}
