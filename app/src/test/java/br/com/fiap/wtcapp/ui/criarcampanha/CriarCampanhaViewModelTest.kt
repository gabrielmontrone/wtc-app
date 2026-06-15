package br.com.fiap.wtcapp.ui.criarcampanha

import br.com.fiap.wtcapp.FakeCampaignRepository
import br.com.fiap.wtcapp.FakeSegmentRepository
import br.com.fiap.wtcapp.MainDispatcherRule
import br.com.fiap.wtcapp.domain.model.Segment
import br.com.fiap.wtcapp.domain.usecase.CreateCampaignUseCase
import br.com.fiap.wtcapp.domain.usecase.GetSegmentsUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CriarCampanhaViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private fun segment(id: String) = Segment(id, "Seg $id", vip = false, active = true, minScore = null, minLoyalty = null)

    private fun viewModel(
        segments: FakeSegmentRepository,
        campaigns: FakeCampaignRepository,
    ) = CriarCampanhaViewModel(GetSegmentsUseCase(segments), CreateCampaignUseCase(campaigns))

    @Test
    fun `preselects the first segment after loading`() =
        runTest {
            val vm =
                viewModel(
                    FakeSegmentRepository(Result.success(listOf(segment("1"), segment("2")))),
                    FakeCampaignRepository(),
                )

            val state = vm.uiState.value
            assertFalse(state.isLoadingSegments)
            assertEquals("1", state.selectedSegmentId)
        }

    @Test
    fun `valid submission marks the campaign as created`() =
        runTest {
            val campaigns = FakeCampaignRepository()
            val vm = viewModel(FakeSegmentRepository(Result.success(listOf(segment("1")))), campaigns)
            vm.onTitleChange("Black Friday")
            vm.onMessageChange("Promo!")

            vm.submit()

            assertTrue(vm.uiState.value.isCreated)
            assertEquals(1, campaigns.createCallCount)
            assertEquals("1", campaigns.lastSegmentId)
        }

    @Test
    fun `submission without a title surfaces an error and stays uncreated`() =
        runTest {
            val campaigns = FakeCampaignRepository()
            val vm = viewModel(FakeSegmentRepository(Result.success(listOf(segment("1")))), campaigns)

            vm.submit()

            assertFalse(vm.uiState.value.isCreated)
            assertNotNull(vm.uiState.value.errorMessage)
            assertEquals(0, campaigns.createCallCount)
        }
}
