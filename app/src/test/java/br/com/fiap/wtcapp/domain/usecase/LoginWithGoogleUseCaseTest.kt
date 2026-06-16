package br.com.fiap.wtcapp.domain.usecase

import br.com.fiap.wtcapp.FakeAuthRepository
import br.com.fiap.wtcapp.domain.model.Session
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LoginWithGoogleUseCaseTest {
    @Test
    fun `delegates a valid token to the repository`() =
        runTest {
            val repository = FakeAuthRepository(Result.success(Session("jwt", "CLIENTE")))
            val useCase = LoginWithGoogleUseCase(repository)

            val result = useCase("id-token")

            assertTrue(result.isSuccess)
            assertEquals("id-token", repository.lastGoogleIdToken)
        }

    @Test
    fun `rejects a blank token without hitting the repository`() =
        runTest {
            val repository = FakeAuthRepository()
            val useCase = LoginWithGoogleUseCase(repository)

            val result = useCase("")

            assertTrue(result.isFailure)
            assertEquals(0, repository.googleCallCount)
        }
}
