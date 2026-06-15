package br.com.fiap.wtcapp.ui.register

import br.com.fiap.wtcapp.FakeAuthRepository
import br.com.fiap.wtcapp.MainDispatcherRule
import br.com.fiap.wtcapp.domain.usecase.RegisterUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RegisterViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private fun viewModelWith(repository: FakeAuthRepository) = RegisterViewModel(RegisterUseCase(repository))

    @Test
    fun `mismatched passwords show an error and never call the repository`() =
        runTest {
            val repository = FakeAuthRepository()
            val viewModel = viewModelWith(repository)
            viewModel.onEmailChange("user@wtc.com")
            viewModel.onPasswordChange("secret123")
            viewModel.onConfirmPasswordChange("different")

            viewModel.register()

            assertEquals("As senhas não coincidem", viewModel.uiState.value.errorMessage)
            assertEquals(0, repository.registerCallCount)
        }

    @Test
    fun `successful registration marks the state as registered with the chosen role`() =
        runTest {
            val repository = FakeAuthRepository()
            val viewModel = viewModelWith(repository)
            viewModel.onEmailChange("user@wtc.com")
            viewModel.onPasswordChange("secret123")
            viewModel.onConfirmPasswordChange("secret123")
            viewModel.onProfileChange(false)

            viewModel.register()

            val state = viewModel.uiState.value
            assertTrue(state.isRegistered)
            assertFalse(state.isLoading)
            assertEquals("CLIENTE", repository.lastRegisterRole)
        }

    @Test
    fun `failed registration surfaces the error and stays unregistered`() =
        runTest {
            val repository = FakeAuthRepository()
            repository.setRegisterResult(Result.failure(IllegalStateException("E-mail já cadastrado")))
            val viewModel = viewModelWith(repository)
            viewModel.onEmailChange("user@wtc.com")
            viewModel.onPasswordChange("secret123")
            viewModel.onConfirmPasswordChange("secret123")

            viewModel.register()

            val state = viewModel.uiState.value
            assertFalse(state.isRegistered)
            assertNotNull(state.errorMessage)
        }
}
