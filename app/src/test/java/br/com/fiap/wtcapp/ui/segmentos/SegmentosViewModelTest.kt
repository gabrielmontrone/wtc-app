package br.com.fiap.wtcapp.ui.segmentos

import br.com.fiap.wtcapp.FakeSegmentRepository
import br.com.fiap.wtcapp.MainDispatcherRule
import br.com.fiap.wtcapp.domain.model.Segment
import br.com.fiap.wtcapp.domain.usecase.CreateSegmentUseCase
import br.com.fiap.wtcapp.domain.usecase.GetSegmentsUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SegmentosViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private fun segment(
        id: String,
        name: String,
    ) = Segment(id, name, vip = false, active = true, minScore = null, minLoyalty = null)

    @Test
    fun `loads segments on init`() =
        runTest {
            val repository = FakeSegmentRepository(Result.success(listOf(segment("1", "VIP"))))

            val viewModel = SegmentosViewModel(GetSegmentsUseCase(repository), CreateSegmentUseCase(repository))

            assertFalse(viewModel.uiState.value.isLoading)
            assertEquals(1, viewModel.uiState.value.segments.size)
        }

    @Test
    fun `search filters by name`() =
        runTest {
            val repository =
                FakeSegmentRepository(
                    Result.success(listOf(segment("1", "VIP"), segment("2", "Recentes"))),
                )
            val viewModel = SegmentosViewModel(GetSegmentsUseCase(repository), CreateSegmentUseCase(repository))

            viewModel.onSearchChange("rec")

            assertEquals(listOf("2"), viewModel.uiState.value.visibleSegments.map { it.id })
        }

    @Test
    fun `failure surfaces an error message`() =
        runTest {
            val repository = FakeSegmentRepository(Result.failure(RuntimeException("falhou")))

            val viewModel = SegmentosViewModel(GetSegmentsUseCase(repository), CreateSegmentUseCase(repository))

            assertEquals("falhou", viewModel.uiState.value.errorMessage)
        }

    @Test
    fun `saving a valid segment creates it and reloads the list`() =
        runTest {
            val repository = FakeSegmentRepository(Result.success(emptyList()))
            val viewModel = SegmentosViewModel(GetSegmentsUseCase(repository), CreateSegmentUseCase(repository))
            val loadsAfterInit = repository.callCount

            viewModel.onAddSegmentClick()
            viewModel.onFormNameChange("Clientes VIP")
            viewModel.onFormVipChange(true)
            viewModel.onFormMinScoreChange("80")
            viewModel.saveSegment()

            assertEquals(1, repository.createCount)
            assertEquals("Clientes VIP", repository.lastCreated?.name)
            assertEquals(80, repository.lastCreated?.minScore)
            // dialog closed and the list reloaded
            assertEquals(null, viewModel.uiState.value.addForm)
            assertEquals(loadsAfterInit + 1, repository.callCount)
        }

    @Test
    fun `blank segment name surfaces an error without calling the repository`() =
        runTest {
            val repository = FakeSegmentRepository(Result.success(emptyList()))
            val viewModel = SegmentosViewModel(GetSegmentsUseCase(repository), CreateSegmentUseCase(repository))

            viewModel.onAddSegmentClick()
            viewModel.onFormNameChange("   ")
            viewModel.saveSegment()

            assertEquals(0, repository.createCount)
            assertEquals("Nome do segmento é obrigatório", viewModel.uiState.value.errorMessage)
            assertFalse(viewModel.uiState.value.addForm!!.isSaving)
        }
}
