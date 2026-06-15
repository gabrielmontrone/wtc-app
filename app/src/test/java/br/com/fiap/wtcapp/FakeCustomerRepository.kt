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

    fun setResult(result: Result<List<Customer>>) {
        this.result = result
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
}
