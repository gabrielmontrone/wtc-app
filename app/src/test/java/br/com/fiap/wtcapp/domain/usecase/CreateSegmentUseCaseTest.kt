package br.com.fiap.wtcapp.domain.usecase

import br.com.fiap.wtcapp.FakeSegmentRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CreateSegmentUseCaseTest {
    @Test
    fun `blank name fails without hitting the repository`() =
        runTest {
            val repository = FakeSegmentRepository()
            val useCase = CreateSegmentUseCase(repository)

            val result = useCase("   ", vip = false, active = true, minScore = null, minLoyalty = null)

            assertTrue(result.isFailure)
            assertEquals(0, repository.createCount)
        }

    @Test
    fun `valid input trims and delegates to the repository`() =
        runTest {
            val repository = FakeSegmentRepository()
            val useCase = CreateSegmentUseCase(repository)

            val result = useCase("  Clientes VIP  ", vip = true, active = true, minScore = 80, minLoyalty = 2)

            assertTrue(result.isSuccess)
            assertEquals(1, repository.createCount)
            assertEquals("Clientes VIP", repository.lastCreated?.name)
            assertEquals(80, repository.lastCreated?.minScore)
            assertEquals(2, repository.lastCreated?.minLoyalty)
        }
}
