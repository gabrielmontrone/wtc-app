package br.com.fiap.wtcapp.ui.contatos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.fiap.wtcapp.domain.usecase.CreateCustomerUseCase
import br.com.fiap.wtcapp.domain.usecase.GetCustomersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContatosViewModel
    @Inject
    constructor(
        private val getCustomers: GetCustomersUseCase,
        private val createCustomer: CreateCustomerUseCase,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(ContatosUiState())
        val uiState: StateFlow<ContatosUiState> = _uiState.asStateFlow()

        init {
            load()
        }

        fun load() {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            viewModelScope.launch {
                getCustomers().fold(
                    onSuccess = { customers ->
                        _uiState.update { it.copy(isLoading = false, customers = customers) }
                    },
                    onFailure = { error ->
                        _uiState.update {
                            it.copy(isLoading = false, errorMessage = error.message ?: "Erro ao carregar contatos")
                        }
                    },
                )
            }
        }

        fun onSearchChange(value: String) = _uiState.update { it.copy(search = value) }

        fun onFilterChange(filter: ContatoFiltro) = _uiState.update { it.copy(filter = filter) }

        fun onErrorShown() = _uiState.update { it.copy(errorMessage = null) }

        fun onAddContactClick() = _uiState.update { it.copy(addForm = AddContactForm()) }

        fun onAddContactDismiss() = _uiState.update { it.copy(addForm = null) }

        fun onFormNameChange(value: String) = updateForm { it.copy(name = value) }

        fun onFormDocumentChange(value: String) = updateForm { it.copy(document = value) }

        fun onFormVipChange(value: Boolean) = updateForm { it.copy(vip = value) }

        fun onFormLoyaltyChange(value: Boolean) = updateForm { it.copy(loyalty = value) }

        fun onFormActiveChange(value: Boolean) = updateForm { it.copy(active = value) }

        fun saveContact() {
            val form = _uiState.value.addForm ?: return
            if (form.isSaving) return
            updateForm { it.copy(isSaving = true) }
            viewModelScope.launch {
                createCustomer(form.name, form.document, form.vip, form.loyalty, form.active).fold(
                    onSuccess = {
                        _uiState.update { it.copy(addForm = null) }
                        load()
                    },
                    onFailure = { error ->
                        _uiState.update {
                            it.copy(
                                addForm = it.addForm?.copy(isSaving = false),
                                errorMessage = error.message ?: "Erro ao criar contato",
                            )
                        }
                    },
                )
            }
        }

        private fun updateForm(transform: (AddContactForm) -> AddContactForm) =
            _uiState.update { state -> state.addForm?.let { state.copy(addForm = transform(it)) } ?: state }
    }
