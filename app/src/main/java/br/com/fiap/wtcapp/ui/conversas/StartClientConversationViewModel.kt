package br.com.fiap.wtcapp.ui.conversas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.fiap.wtcapp.domain.usecase.StartClientConversationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StartClientConversationViewModel
    @Inject
    constructor(
        private val startClientConversation: StartClientConversationUseCase,
    ) : ViewModel() {
        data class UiState(
            val email: String = "",
            val isLoading: Boolean = false,
            val errorMessage: String? = null,
            val openedConversationId: String? = null,
        )

        private val _uiState = MutableStateFlow(UiState())
        val uiState: StateFlow<UiState> = _uiState.asStateFlow()

        fun onEmailChange(value: String) = _uiState.update { it.copy(email = value, errorMessage = null) }

        fun start() {
            val email = _uiState.value.email
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            viewModelScope.launch {
                startClientConversation(email).fold(
                    onSuccess = { conversation ->
                        _uiState.update { it.copy(isLoading = false, openedConversationId = conversation.id) }
                    },
                    onFailure = { error ->
                        _uiState.update {
                            it.copy(isLoading = false, errorMessage = error.message ?: "Erro ao iniciar conversa")
                        }
                    },
                )
            }
        }

        fun onErrorShown() = _uiState.update { it.copy(errorMessage = null) }

        /** Resets state after the caller has navigated to the opened conversation. */
        fun onConversationOpened() = _uiState.update { UiState() }
    }
