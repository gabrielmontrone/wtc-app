package br.com.fiap.wtcapp.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.fiap.wtcapp.domain.usecase.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel
    @Inject
    constructor(
        private val registerUseCase: RegisterUseCase,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(RegisterUiState())
        val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

        fun onEmailChange(value: String) = _uiState.update { it.copy(email = value, errorMessage = null) }

        fun onPasswordChange(value: String) = _uiState.update { it.copy(password = value, errorMessage = null) }

        fun onConfirmPasswordChange(value: String) = _uiState.update { it.copy(confirmPassword = value, errorMessage = null) }

        fun onProfileChange(operator: Boolean) = _uiState.update { it.copy(operatorProfile = operator) }

        fun onErrorShown() = _uiState.update { it.copy(errorMessage = null) }

        fun register() {
            val current = _uiState.value
            if (current.isLoading) return
            if (current.password != current.confirmPassword) {
                _uiState.update { it.copy(errorMessage = "As senhas não coincidem") }
                return
            }
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            viewModelScope.launch {
                registerUseCase(current.email, current.password, current.role).fold(
                    onSuccess = { _uiState.update { it.copy(isLoading = false, isRegistered = true) } },
                    onFailure = { error ->
                        _uiState.update {
                            it.copy(isLoading = false, errorMessage = error.message ?: "Erro ao criar conta")
                        }
                    },
                )
            }
        }
    }
