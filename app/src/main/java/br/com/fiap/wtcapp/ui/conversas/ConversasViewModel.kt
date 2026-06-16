package br.com.fiap.wtcapp.ui.conversas

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.fiap.wtcapp.ConversasActivity
import br.com.fiap.wtcapp.domain.usecase.GetConversationsUseCase
import br.com.fiap.wtcapp.domain.usecase.StartConversationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConversasViewModel
    @Inject
    constructor(
        private val getConversations: GetConversationsUseCase,
        private val startConversation: StartConversationUseCase,
        savedStateHandle: SavedStateHandle,
    ) : ViewModel() {
        private val customerId: String = savedStateHandle[ConversasActivity.EXTRA_CUSTOMER_ID] ?: ""

        private val _uiState = MutableStateFlow(ConversasUiState())
        val uiState: StateFlow<ConversasUiState> = _uiState.asStateFlow()

        init {
            load()
        }

        fun load() {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            viewModelScope.launch {
                getConversations(customerId).fold(
                    onSuccess = { conversations ->
                        _uiState.update { it.copy(isLoading = false, conversations = conversations) }
                    },
                    onFailure = { error ->
                        _uiState.update {
                            it.copy(isLoading = false, errorMessage = error.message ?: "Erro ao carregar conversas")
                        }
                    },
                )
            }
        }

        fun startNewConversation() {
            if (_uiState.value.isStarting) return
            _uiState.update { it.copy(isStarting = true, errorMessage = null) }
            viewModelScope.launch {
                startConversation(customerId).fold(
                    onSuccess = {
                        _uiState.update { it.copy(isStarting = false) }
                        load()
                    },
                    onFailure = { error ->
                        _uiState.update {
                            it.copy(isStarting = false, errorMessage = error.message ?: "Erro ao iniciar conversa")
                        }
                    },
                )
            }
        }

        fun onErrorShown() = _uiState.update { it.copy(errorMessage = null) }
    }
