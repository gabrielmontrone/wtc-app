package br.com.fiap.wtcapp.domain.usecase

import br.com.fiap.wtcapp.FakeAuthRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RegisterUseCaseTest {
    @Test
    fun `blank email fails without hitting the repository`() =
        runTest {
            val repository = FakeAuthRepository()
            val useCase = RegisterUseCase(repository)

            val result = useCase("   ", "secret123", "OPERADOR")

            assertTrue(result.isFailure)
            assertEquals(0, repository.registerCallCount)
        }

    @Test
    fun `short password is rejected before reaching the repository`() =
        runTest {
            val repository = FakeAuthRepository()
            val useCase = RegisterUseCase(repository)

            val result = useCase("user@wtc.com", "123", "CLIENTE")

            assertTrue(result.isFailure)
            assertEquals(0, repository.registerCallCount)
        }

    @Test
    fun `valid input trims email and delegates with the role`() =
        runTest {
            val repository = FakeAuthRepository()
            val useCase = RegisterUseCase(repository)

            val result = useCase("  user@wtc.com  ", "secret123", "CLIENTE")

            assertTrue(result.isSuccess)
            assertEquals("user@wtc.com", repository.lastRegisterEmail)
            assertEquals("CLIENTE", repository.lastRegisterRole)
            assertEquals(1, repository.registerCallCount)
        }
}
