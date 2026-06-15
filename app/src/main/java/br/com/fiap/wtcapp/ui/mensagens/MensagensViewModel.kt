package br.com.fiap.wtcapp.ui.mensagens

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.fiap.wtcapp.MensagensActivity
import br.com.fiap.wtcapp.domain.usecase.GetMessagesUseCase
import br.com.fiap.wtcapp.domain.usecase.SendReplyUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MensagensViewModel
    @Inject
    constructor(
        private val getMessages: GetMessagesUseCase,
        private val sendReply: SendReplyUseCase,
        savedStateHandle: SavedStateHandle,
    ) : ViewModel() {
        private val conversationId: String = savedStateHandle[MensagensActivity.EXTRA_CONVERSATION_ID] ?: ""

        private val _uiState = MutableStateFlow(MensagensUiState())
        val uiState: StateFlow<MensagensUiState> = _uiState.asStateFlow()

        init {
            load()
        }

        fun load() {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            viewModelScope.launch {
                getMessages(conversationId).fold(
                    onSuccess = { messages ->
                        _uiState.update { it.copy(isLoading = false, messages = messages) }
                    },
                    onFailure = { error ->
                        _uiState.update {
                            it.copy(isLoading = false, errorMessage = error.message ?: "Erro ao carregar mensagens")
                        }
                    },
                )
            }
        }

        fun onReplyChange(value: String) = _uiState.update { it.copy(reply = value) }

        fun onErrorShown() = _uiState.update { it.copy(errorMessage = null) }

        fun send() {
            val current = _uiState.value
            if (!current.canSend) return
            _uiState.update { it.copy(isSending = true, errorMessage = null) }
            viewModelScope.launch {
                sendReply(conversationId, current.reply).fold(
                    onSuccess = { sent ->
                        _uiState.update {
                            it.copy(isSending = false, reply = "", messages = it.messages + sent)
                        }
                    },
                    onFailure = { error ->
                        _uiState.update {
                            it.copy(isSending = false, errorMessage = error.message ?: "Erro ao enviar mensagem")
                        }
                    },
                )
            }
        }
    }
