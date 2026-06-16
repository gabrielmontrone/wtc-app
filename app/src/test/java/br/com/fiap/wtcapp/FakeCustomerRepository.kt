package br.com.fiap.wtcapp

import br.com.fiap.wtcapp.domain.model.Customer
import br.com.fiap.wtcapp.domain.repository.CustomerRepository

class FakeCustomerRepository(
    private var result: Result<List<Customer>> = Result.success(emptyList()),
) : CustomerRepository {
    var callCount = 0
        private set
    var lastVip: Boolean? = null
        private set
    var createCount = 0
        private set
    var lastCreated: Customer? = null
        private set

    private var createResult: Result<Customer>? = null

    fun setResult(result: Result<List<Customer>>) {
        this.result = result
    }

    fun setCreateResult(result: Result<Customer>) {
        this.createResult = result
    }

    override suspend fun customers(
        vip: Boolean?,
        loyalty: Boolean?,
        active: Boolean?,
    ): Result<List<Customer>> {
        callCount++
        lastVip = vip
        return result
    }

    override suspend fun createCustomer(
        name: String,
        document: String,
        vip: Boolean,
        loyalty: Boolean,
        active: Boolean,
    ): Result<Customer> {
        createCount++
        val created = Customer("new-id", name, document, vip, loyalty, active)
        lastCreated = created
        return createResult ?: Result.success(created)
    }
}
