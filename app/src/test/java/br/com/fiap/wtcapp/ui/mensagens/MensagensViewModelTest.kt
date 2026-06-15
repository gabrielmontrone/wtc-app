package br.com.fiap.wtcapp.ui.mensagens

import androidx.lifecycle.SavedStateHandle
import br.com.fiap.wtcapp.FakeMessageRepository
import br.com.fiap.wtcapp.MainDispatcherRule
import br.com.fiap.wtcapp.MensagensActivity
import br.com.fiap.wtcapp.domain.model.ChatMessage
import br.com.fiap.wtcapp.domain.usecase.GetMessagesUseCase
import br.com.fiap.wtcapp.domain.usecase.SendReplyUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MensagensViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private fun handle() = SavedStateHandle(mapOf(MensagensActivity.EXTRA_CONVERSATION_ID to "conv1"))

    private fun viewModel(repository: FakeMessageRepository) =
        MensagensViewModel(GetMessagesUseCase(repository), SendReplyUseCase(repository), handle())

    @Test
    fun `loads the conversation history`() =
        runTest {
            val repository =
                FakeMessageRepository(
                    listResult = Result.success(listOf(ChatMessage("1", "S", "oi", "SENT", "CUSTOMER"))),
                )

            val viewModel = viewModel(repository)

            assertEquals(1, viewModel.uiState.value.messages.size)
            assertEquals("conv1", repository.lastConversationId)
        }

    @Test
    fun `sending a reply appends the message and clears the input`() =
        runTest {
            val repository = FakeMessageRepository()
            val viewModel = viewModel(repository)
            viewModel.onReplyChange("nova resposta")

            viewModel.send()

            val state = viewModel.uiState.value
            assertEquals("", state.reply)
            assertTrue(state.messages.any { it.id == "new" })
            assertEquals("nova resposta", repository.lastSentContent)
        }

    @Test
    fun `blank reply is not sent`() =
        runTest {
            val repository = FakeMessageRepository()
            val viewModel = viewModel(repository)

            viewModel.send()

            assertEquals(null, repository.lastSentContent)
        }
}
