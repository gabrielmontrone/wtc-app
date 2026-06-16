package br.com.fiap.wtcapp.ui.mensagens

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.fiap.wtcapp.MensagensActivity
import br.com.fiap.wtcapp.domain.compliance.MessageRiskAnalyzer
import br.com.fiap.wtcapp.domain.model.Campaign
import br.com.fiap.wtcapp.domain.usecase.GetCampaignsUseCase
import br.com.fiap.wtcapp.domain.usecase.GetMessagesUseCase
import br.com.fiap.wtcapp.domain.usecase.SendReplyUseCase
import br.com.fiap.wtcapp.domain.usecase.UploadPhotoUseCase
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
        private val getCampaigns: GetCampaignsUseCase,
        private val uploadPhoto: UploadPhotoUseCase,
        private val riskAnalyzer: MessageRiskAnalyzer,
        savedStateHandle: SavedStateHandle,
    ) : ViewModel() {
        private val conversationId: String = savedStateHandle[MensagensActivity.EXTRA_CONVERSATION_ID] ?: ""

        private val _uiState = MutableStateFlow(MensagensUiState())
        val uiState: StateFlow<MensagensUiState> = _uiState.asStateFlow()

        init {
            load()
            loadCampaigns()
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

        private fun loadCampaigns() {
            viewModelScope.launch {
                getCampaigns().onSuccess { campaigns ->
                    _uiState.update { it.copy(campaigns = campaigns) }
                }
            }
        }

        fun onReplyChange(value: String) = _uiState.update { it.copy(reply = value) }

        /** Replaces the "/" command with the selected campaign's reusable message. */
        fun onCampaignSelected(campaign: Campaign) = _uiState.update { it.copy(reply = campaign.content) }

        fun onErrorShown() = _uiState.update { it.copy(errorMessage = null) }

        fun onPhotoPicked(
            fileName: String,
            contentType: String,
            bytes: ByteArray,
        ) {
            _uiState.update { it.copy(isUploading = true, errorMessage = null) }
            viewModelScope.launch {
                uploadPhoto(fileName, contentType, bytes).fold(
                    onSuccess = { url ->
                        _uiState.update { it.copy(isUploading = false, pendingImageUrl = url) }
                    },
                    onFailure = { error ->
                        _uiState.update {
                            it.copy(isUploading = false, errorMessage = error.message ?: "Erro ao enviar foto")
                        }
                    },
                )
            }
        }

        fun onRemovePendingPhoto() = _uiState.update { it.copy(pendingImageUrl = null) }

        fun send() {
            val current = _uiState.value
            if (!current.canSend) return
            // DLP: warn before sending if the reply contains sensitive data / risky links.
            val analysis = riskAnalyzer.analyze(current.reply)
            if (analysis.level.isSuspicious()) {
                _uiState.update { it.copy(riskWarning = analysis.flags) }
                return
            }
            performSend()
        }

        /** Sends anyway after the operator acknowledges the DLP warning. */
        fun confirmSend() {
            _uiState.update { it.copy(riskWarning = null) }
            performSend()
        }

        fun dismissRiskWarning() = _uiState.update { it.copy(riskWarning = null) }

        private fun performSend() {
            val current = _uiState.value
            if (!current.canSend) return
            _uiState.update { it.copy(isSending = true, errorMessage = null) }
            viewModelScope.launch {
                sendReply(conversationId, current.reply, current.pendingImageUrl).fold(
                    onSuccess = { sent ->
                        _uiState.update {
                            it.copy(
                                isSending = false,
                                reply = "",
                                pendingImageUrl = null,
                                messages = it.messages + sent,
                            )
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
