package br.com.fiap.wtcapp.domain.usecase

import br.com.fiap.wtcapp.domain.model.Customer
import br.com.fiap.wtcapp.domain.repository.CustomerRepository
import javax.inject.Inject

/**
 * Creates a CRM contact after validating the form. Validation lives here (not in the
 * ViewModel) so the rules stay unit-testable in isolation and mirror the backend
 * constraints on [com.wtc.customer.dto.CreateCustomerRequest], mirroring [CreateCampaignUseCase].
 */
class CreateCustomerUseCase
    @Inject
    constructor(
        private val repository: CustomerRepository,
    ) {
        suspend operator fun invoke(
            name: String,
            document: String,
            vip: Boolean,
            loyalty: Boolean,
            active: Boolean,
        ): Result<Customer> {
            val trimmedName = name.trim()
            val trimmedDocument = document.trim()
            val validationError =
                when {
                    trimmedName.length < MIN_NAME_LENGTH -> "Nome deve ter ao menos 3 caracteres"
                    !trimmedDocument.all { it.isDigit() } -> "Documento deve conter apenas números"
                    trimmedDocument.length !in MIN_DOCUMENT_LENGTH..MAX_DOCUMENT_LENGTH -> "Documento inválido (11 a 14 dígitos)"
                    else -> null
                }
            if (validationError != null) {
                return Result.failure(IllegalArgumentException(validationError))
            }
            return repository.createCustomer(trimmedName, trimmedDocument, vip, loyalty, active)
        }

        private companion object {
            const val MIN_NAME_LENGTH = 3
            const val MIN_DOCUMENT_LENGTH = 11
            const val MAX_DOCUMENT_LENGTH = 14
        }
    }
