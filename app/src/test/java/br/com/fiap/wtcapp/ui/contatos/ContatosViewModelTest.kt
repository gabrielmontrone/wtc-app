package br.com.fiap.wtcapp.ui.contatos

import br.com.fiap.wtcapp.FakeCustomerRepository
import br.com.fiap.wtcapp.MainDispatcherRule
import br.com.fiap.wtcapp.domain.model.Customer
import br.com.fiap.wtcapp.domain.usecase.GetCustomersUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ContatosViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private fun customer(
        id: String,
        name: String,
        vip: Boolean = false,
        loyalty: Boolean = false,
        active: Boolean = true,
    ) = Customer(id, name, "doc-$id", vip, loyalty, active)

    @Test
    fun `loads customers on init`() =
        runTest {
            val repository = FakeCustomerRepository(Result.success(listOf(customer("1", "Ana"))))

            val viewModel = ContatosViewModel(GetCustomersUseCase(repository))

            val state = viewModel.uiState.value
            assertFalse(state.isLoading)
            assertEquals(1, state.customers.size)
            assertEquals(1, repository.callCount)
        }

    @Test
    fun `failure surfaces an error message`() =
        runTest {
            val repository = FakeCustomerRepository(Result.failure(RuntimeException("rede caiu")))

            val viewModel = ContatosViewModel(GetCustomersUseCase(repository))

            assertEquals("rede caiu", viewModel.uiState.value.errorMessage)
        }

    @Test
    fun `vip filter and search narrow the visible list`() =
        runTest {
            val repository =
                FakeCustomerRepository(
                    Result.success(
                        listOf(
                            customer("1", "Ana Souza", vip = true),
                            customer("2", "Carlos Lima", vip = false),
                            customer("3", "Ana Vip", vip = true),
                        ),
                    ),
                )
            val viewModel = ContatosViewModel(GetCustomersUseCase(repository))

            viewModel.onFilterChange(ContatoFiltro.VIP)
            viewModel.onSearchChange("ana")

            val visible = viewModel.uiState.value.visibleCustomers
            assertEquals(listOf("1", "3"), visible.map { it.id })
        }

    @Test
    fun `inactive filter keeps only inactive contacts`() =
        runTest {
            val repository =
                FakeCustomerRepository(
                    Result.success(
                        listOf(
                            customer("1", "Ativo", active = true),
                            customer("2", "Inativo", active = false),
                        ),
                    ),
                )
            val viewModel = ContatosViewModel(GetCustomersUseCase(repository))

            viewModel.onFilterChange(ContatoFiltro.INATIVO)

            assertTrue(viewModel.uiState.value.visibleCustomers.all { !it.active })
        }
}
