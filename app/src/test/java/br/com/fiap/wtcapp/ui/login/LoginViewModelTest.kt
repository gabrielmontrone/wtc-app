package br.com.fiap.wtcapp.ui.login

import br.com.fiap.wtcapp.FakeAuthRepository
import br.com.fiap.wtcapp.MainDispatcherRule
import br.com.fiap.wtcapp.domain.model.Session
import br.com.fiap.wtcapp.domain.usecase.LoginUseCase
import br.com.fiap.wtcapp.domain.usecase.LoginWithGoogleUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private fun viewModelWith(repository: FakeAuthRepository) = LoginViewModel(LoginUseCase(repository), LoginWithGoogleUseCase(repository))

    @Test
    fun `successful login marks state as logged in`() =
        runTest {
            val viewModel = viewModelWith(FakeAuthRepository(Result.success(Session("jwt", "OPERATOR"))))
            viewModel.onEmailChange("user@wtc.com")
            viewModel.onPasswordChange("secret")

            viewModel.login()

            val state = viewModel.uiState.value
            assertTrue(state.isLoggedIn)
            assertFalse(state.isLoading)
            assertNull(state.errorMessage)
        }

    @Test
    fun `failed login exposes the error and stays logged out`() =
        runTest {
            val viewModel = viewModelWith(FakeAuthRepository(Result.failure(RuntimeException("Credenciais inválidas"))))
            viewModel.onEmailChange("user@wtc.com")
            viewModel.onPasswordChange("wrong")

            viewModel.login()

            val state = viewModel.uiState.value
            assertFalse(state.isLoggedIn)
            assertEquals("Credenciais inválidas", state.errorMessage)
        }

    @Test
    fun `blank input is rejected before reaching the repository`() =
        runTest {
            val repository = FakeAuthRepository()
            val viewModel = viewModelWith(repository)

            viewModel.login()

            assertEquals(0, repository.loginCallCount)
            assertNotNull(viewModel.uiState.value.errorMessage)
        }

    @Test
    fun `google sign-in success logs in using the id token`() =
        runTest {
            val repository = FakeAuthRepository(Result.success(Session("jwt", "CLIENTE")))
            val viewModel = viewModelWith(repository)

            viewModel.onGoogleSignIn("google-id-token")

            assertTrue(viewModel.uiState.value.isLoggedIn)
            assertEquals(1, repository.googleCallCount)
            assertEquals("google-id-token", repository.lastGoogleIdToken)
        }

    @Test
    fun `google sign-in error surfaces the message`() =
        runTest {
            val viewModel = viewModelWith(FakeAuthRepository())

            viewModel.onGoogleSignInError("Login cancelado")

            assertFalse(viewModel.uiState.value.isLoggedIn)
            assertEquals("Login cancelado", viewModel.uiState.value.errorMessage)
        }

    @Test
    fun `editing a field clears the previous error`() =
        runTest {
            val viewModel = viewModelWith(FakeAuthRepository(Result.failure(RuntimeException("boom"))))
            viewModel.onEmailChange("user@wtc.com")
            viewModel.onPasswordChange("secret")
            viewModel.login()
            assertNotNull(viewModel.uiState.value.errorMessage)

            viewModel.onEmailChange("new@wtc.com")

            assertNull(viewModel.uiState.value.errorMessage)
        }
}
