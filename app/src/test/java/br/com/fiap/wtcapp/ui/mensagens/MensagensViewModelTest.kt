package br.com.fiap.wtcapp.ui.mensagens

import androidx.lifecycle.SavedStateHandle
import br.com.fiap.wtcapp.FakeAttachmentRepository
import br.com.fiap.wtcapp.FakeCampaignRepository
import br.com.fiap.wtcapp.FakeMessageRepository
import br.com.fiap.wtcapp.MainDispatcherRule
import br.com.fiap.wtcapp.MensagensActivity
import br.com.fiap.wtcapp.domain.model.Campaign
import br.com.fiap.wtcapp.domain.model.ChatMessage
import br.com.fiap.wtcapp.domain.usecase.GetCampaignsUseCase
import br.com.fiap.wtcapp.domain.usecase.GetMessagesUseCase
import br.com.fiap.wtcapp.domain.usecase.SendReplyUseCase
import br.com.fiap.wtcapp.domain.usecase.UploadPhotoUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MensagensViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private fun handle() = SavedStateHandle(mapOf(MensagensActivity.EXTRA_CONVERSATION_ID to "conv1"))

    private fun viewModel(
        messageRepository: FakeMessageRepository = FakeMessageRepository(),
        campaignRepository: FakeCampaignRepository = FakeCampaignRepository(),
        attachmentRepository: FakeAttachmentRepository = FakeAttachmentRepository(),
    ) = MensagensViewModel(
        GetMessagesUseCase(messageRepository),
        SendReplyUseCase(messageRepository),
        GetCampaignsUseCase(campaignRepository),
        UploadPhotoUseCase(attachmentRepository),
        handle(),
    )

    @Test
    fun `loads the conversation history`() =
        runTest {
            val repository =
                FakeMessageRepository(
                    listResult = Result.success(listOf(ChatMessage("1", "S", "oi", "SENT", "CUSTOMER"))),
                )

            val viewModel = viewModel(messageRepository = repository)

            assertEquals(1, viewModel.uiState.value.messages.size)
            assertEquals("conv1", repository.lastConversationId)
        }

    @Test
    fun `sending a reply appends the message and clears the input`() =
        runTest {
            val repository = FakeMessageRepository()
            val viewModel = viewModel(messageRepository = repository)
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
            val viewModel = viewModel(messageRepository = repository)

            viewModel.send()

            assertEquals(null, repository.lastSentContent)
        }

    @Test
    fun `typing slash suggests campaigns by name`() =
        runTest {
            val campaigns =
                FakeCampaignRepository(
                    listResult =
                        Result.success(
                            listOf(
                                Campaign("c1", "Promoção", null, "MESSAGE", "SENT", 0, 0, 0, "Ganhe 30% off!"),
                                Campaign("c2", "Boas-vindas", null, "MESSAGE", "SENT", 0, 0, 0, "Bem-vindo!"),
                            ),
                        ),
                )
            val viewModel = viewModel(campaignRepository = campaigns)

            viewModel.onReplyChange("/prom")

            val suggestions = viewModel.uiState.value.campaignSuggestions
            assertEquals(1, suggestions.size)
            assertEquals("Promoção", suggestions.first().name)
        }

    @Test
    fun `selecting a campaign inserts its content`() =
        runTest {
            val campaign = Campaign("c1", "Promoção", null, "MESSAGE", "SENT", 0, 0, 0, "Ganhe 30% off!")
            val viewModel = viewModel()

            viewModel.onCampaignSelected(campaign)

            assertEquals("Ganhe 30% off!", viewModel.uiState.value.reply)
            assertTrue(viewModel.uiState.value.campaignSuggestions.isEmpty())
        }

    @Test
    fun `picking a photo uploads it and holds the url until sent`() =
        runTest {
            val attachments = FakeAttachmentRepository(Result.success("https://cdn/foto.jpg"))
            val messages = FakeMessageRepository()
            val viewModel = viewModel(messageRepository = messages, attachmentRepository = attachments)

            viewModel.onPhotoPicked("foto.jpg", "image/jpeg", byteArrayOf(1, 2, 3))

            assertEquals("https://cdn/foto.jpg", viewModel.uiState.value.pendingImageUrl)
            assertEquals(1, attachments.uploadCallCount)

            viewModel.send()

            val state = viewModel.uiState.value
            assertNull(state.pendingImageUrl)
            assertEquals("https://cdn/foto.jpg", messages.lastSentImageUrl)
        }
}
