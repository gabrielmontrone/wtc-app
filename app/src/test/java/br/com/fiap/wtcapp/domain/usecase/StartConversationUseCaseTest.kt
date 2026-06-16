package br.com.fiap.wtcapp.domain.usecase

import br.com.fiap.wtcapp.FakeConversationRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class StartConversationUseCaseTest {
    @Test
    fun `blank customer id fails without hitting the repository`() =
        runTest {
            val repository = FakeConversationRepository()
            val useCase = StartConversationUseCase(repository)

            val result = useCase("   ")

            assertTrue(result.isFailure)
            assertEquals(0, repository.startCount)
        }

    @Test
    fun `valid customer id delegates to the repository`() =
        runTest {
            val repository = FakeConversationRepository()
            val useCase = StartConversationUseCase(repository)

            val result = useCase("cust1")

            assertTrue(result.isSuccess)
            assertEquals(1, repository.startCount)
            assertEquals("cust1", repository.lastStartedCustomerId)
        }
}
