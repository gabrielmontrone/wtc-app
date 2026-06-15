package br.com.fiap.wtcapp.domain.usecase

import br.com.fiap.wtcapp.FakeAuthRepository
import br.com.fiap.wtcapp.domain.model.Session
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LoginUseCaseTest {
    @Test
    fun `blank email fails without hitting the repository`() =
        runTest {
            val repository = FakeAuthRepository()
            val useCase = LoginUseCase(repository)

            val result = useCase("   ", "password")

            assertTrue(result.isFailure)
            assertEquals(0, repository.loginCallCount)
        }

    @Test
    fun `blank password fails without hitting the repository`() =
        runTest {
            val repository = FakeAuthRepository()
            val useCase = LoginUseCase(repository)

            val result = useCase("user@wtc.com", "")

            assertTrue(result.isFailure)
            assertEquals(0, repository.loginCallCount)
        }

    @Test
    fun `valid input trims email and delegates to the repository`() =
        runTest {
            val repository = FakeAuthRepository(Result.success(Session("jwt", "OPERATOR")))
            val useCase = LoginUseCase(repository)

            val result = useCase("  user@wtc.com  ", "secret")

            assertTrue(result.isSuccess)
            assertEquals("user@wtc.com", repository.lastEmail)
            assertEquals(1, repository.loginCallCount)
        }
}
