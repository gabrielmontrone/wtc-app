package br.com.fiap.wtcapp.domain.usecase

import br.com.fiap.wtcapp.domain.model.Customer
import br.com.fiap.wtcapp.domain.repository.CustomerRepository
import javax.inject.Inject

/** Fetches the CRM contacts, optionally narrowed by classification flags. */
class GetCustomersUseCase
    @Inject
    constructor(
        private val repository: CustomerRepository,
    ) {
        suspend operator fun invoke(
            vip: Boolean? = null,
            loyalty: Boolean? = null,
            active: Boolean? = null,
        ): Result<List<Customer>> = repository.customers(vip, loyalty, active)
    }
