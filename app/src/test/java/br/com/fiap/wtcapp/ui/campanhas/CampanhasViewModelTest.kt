package br.com.fiap.wtcapp.ui.campanhas

import br.com.fiap.wtcapp.FakeCampaignRepository
import br.com.fiap.wtcapp.MainDispatcherRule
import br.com.fiap.wtcapp.domain.model.Campaign
import br.com.fiap.wtcapp.domain.usecase.GetCampaignsUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CampanhasViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `aggregates metrics from loaded campaigns`() =
        runTest {
            val repository =
                FakeCampaignRepository(
                    listResult =
                        Result.success(
                            listOf(
                                Campaign("1", "A", null, "MESSAGE", "SENT", 100, 90, 10),
                                Campaign("2", "B", null, "MESSAGE", "SENT", 100, 70, 5),
                            ),
                        ),
                )
            val viewModel = CampanhasViewModel(GetCampaignsUseCase(repository))

            val state = viewModel.uiState.value
            assertEquals(2, state.totalCampaigns)
            assertEquals(200L, state.totalSends)
            assertEquals(15L, state.totalResponses)
            assertEquals(80, state.successRate)
        }

    @Test
    fun `success rate is zero when nothing was sent`() =
        runTest {
            val repository =
                FakeCampaignRepository(
                    listResult = Result.success(listOf(Campaign("1", "A", null, "MESSAGE", "DRAFT", 0, 0, 0))),
                )
            val viewModel = CampanhasViewModel(GetCampaignsUseCase(repository))

            assertEquals(0, viewModel.uiState.value.successRate)
        }

    @Test
    fun `failure surfaces an error message`() =
        runTest {
            val repository = FakeCampaignRepository(listResult = Result.failure(RuntimeException("erro")))
            val viewModel = CampanhasViewModel(GetCampaignsUseCase(repository))

            assertEquals("erro", viewModel.uiState.value.errorMessage)
        }
}
