package br.com.fiap.wtcapp.data.repository

import br.com.fiap.wtcapp.data.remote.WtcApi
import br.com.fiap.wtcapp.data.remote.dto.toDomain
import br.com.fiap.wtcapp.di.IoDispatcher
import br.com.fiap.wtcapp.domain.model.Customer
import br.com.fiap.wtcapp.domain.repository.CustomerRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CustomerRepositoryImpl
    @Inject
    constructor(
        private val api: WtcApi,
        @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    ) : CustomerRepository {
        override suspend fun customers(
            vip: Boolean?,
            loyalty: Boolean?,
            active: Boolean?,
        ): Result<List<Customer>> =
            withContext(ioDispatcher) {
                runCatching {
                    api.listCustomers(vip, loyalty, active, page = 0, size = PAGE_SIZE)
                        .content.map { it.toDomain() }
                }
            }

        private companion object {
            const val PAGE_SIZE = 50
        }
    }
