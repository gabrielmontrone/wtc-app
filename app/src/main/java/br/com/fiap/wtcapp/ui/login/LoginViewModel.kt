package br.com.fiap.wtcapp.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.fiap.wtcapp.domain.usecase.LoginUseCase
import br.com.fiap.wtcapp.domain.usecase.LoginWithGoogleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel
    @Inject
    constructor(
        private val loginUseCase: LoginUseCase,
        private val loginWithGoogleUseCase: LoginWithGoogleUseCase,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(LoginUiState())
        val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

        fun onEmailChange(value: String) = _uiState.update { it.copy(email = value, errorMessage = null) }

        fun onPasswordChange(value: String) = _uiState.update { it.copy(password = value, errorMessage = null) }

        fun onProfileChange(operator: Boolean) = _uiState.update { it.copy(operatorProfile = operator) }

        fun onErrorShown() = _uiState.update { it.copy(errorMessage = null) }

        fun login() {
            val current = _uiState.value
            if (current.isLoading) return
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            viewModelScope.launch {
                loginUseCase(current.email, current.password).fold(
                    onSuccess = { _uiState.update { state -> state.copy(isLoading = false, isLoggedIn = true) } },
                    onFailure = { error ->
                        _uiState.update { state ->
                            state.copy(isLoading = false, errorMessage = error.message ?: "Erro ao fazer login")
                        }
                    },
                )
            }
        }

        /** Called with the Google ID token retrieved on-device via Credential Manager. */
        fun onGoogleSignIn(idToken: String) {
            if (_uiState.value.isLoading) return
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            viewModelScope.launch {
                loginWithGoogleUseCase(idToken).fold(
                    onSuccess = { _uiState.update { state -> state.copy(isLoading = false, isLoggedIn = true) } },
                    onFailure = { error ->
                        _uiState.update { state ->
                            state.copy(isLoading = false, errorMessage = error.message ?: "Erro ao entrar com Google")
                        }
                    },
                )
            }
        }

        /** Surfaces a failure raised by the on-device Credential Manager flow (cancel, no account…). */
        fun onGoogleSignInError(message: String) = _uiState.update { it.copy(isLoading = false, errorMessage = message) }
    }
