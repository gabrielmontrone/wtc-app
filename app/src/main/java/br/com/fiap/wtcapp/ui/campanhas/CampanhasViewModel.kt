package br.com.fiap.wtcapp.ui.campanhas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.fiap.wtcapp.domain.usecase.GetCampaignsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CampanhasViewModel
    @Inject
    constructor(
        private val getCampaigns: GetCampaignsUseCase,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(CampanhasUiState())
        val uiState: StateFlow<CampanhasUiState> = _uiState.asStateFlow()

        init {
            load()
        }

        fun load() {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            viewModelScope.launch {
                getCampaigns().fold(
                    onSuccess = { campaigns ->
                        _uiState.update { it.copy(isLoading = false, campaigns = campaigns) }
                    },
                    onFailure = { error ->
                        _uiState.update {
                            it.copy(isLoading = false, errorMessage = error.message ?: "Erro ao carregar campanhas")
                        }
                    },
                )
            }
        }

        fun onErrorShown() = _uiState.update { it.copy(errorMessage = null) }
    }
