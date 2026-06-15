package br.com.fiap.wtcapp.domain.usecase

import br.com.fiap.wtcapp.FakeCampaignRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CreateCampaignUseCaseTest {
    @Test
    fun `blank title fails without hitting the repository`() =
        runTest {
            val repository = FakeCampaignRepository()
            val useCase = CreateCampaignUseCase(repository)

            val result = useCase("   ", "mensagem", "seg-1")

            assertTrue(result.isFailure)
            assertEquals(0, repository.createCallCount)
        }

    @Test
    fun `missing segment fails without hitting the repository`() =
        runTest {
            val repository = FakeCampaignRepository()
            val useCase = CreateCampaignUseCase(repository)

            val result = useCase("Título", "mensagem", null)

            assertTrue(result.isFailure)
            assertEquals(0, repository.createCallCount)
        }

    @Test
    fun `valid input trims and delegates to the repository`() =
        runTest {
            val repository = FakeCampaignRepository()
            val useCase = CreateCampaignUseCase(repository)

            val result = useCase("  Black Friday  ", "  promo  ", "seg-1")

            assertTrue(result.isSuccess)
            assertEquals("Black Friday", repository.lastName)
            assertEquals("seg-1", repository.lastSegmentId)
            assertEquals(1, repository.createCallCount)
        }
}
