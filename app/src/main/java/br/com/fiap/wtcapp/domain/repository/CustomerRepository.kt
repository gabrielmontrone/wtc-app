package br.com.fiap.wtcapp.domain.repository

import br.com.fiap.wtcapp.domain.model.Customer

interface CustomerRepository {
    suspend fun customers(
        vip: Boolean? = null,
        loyalty: Boolean? = null,
        active: Boolean? = null,
    ): Result<List<Customer>>

    suspend fun createCustomer(
        name: String,
        document: String,
        vip: Boolean,
        loyalty: Boolean,
        active: Boolean,
    ): Result<Customer>
}
