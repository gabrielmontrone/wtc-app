package br.com.fiap.wtcapp.ui.contatos

import br.com.fiap.wtcapp.FakeCustomerRepository
import br.com.fiap.wtcapp.MainDispatcherRule
import br.com.fiap.wtcapp.domain.model.Customer
import br.com.fiap.wtcapp.domain.usecase.CreateCustomerUseCase
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

            val viewModel = ContatosViewModel(GetCustomersUseCase(repository), CreateCustomerUseCase(repository))

            val state = viewModel.uiState.value
            assertFalse(state.isLoading)
            assertEquals(1, state.customers.size)
            assertEquals(1, repository.callCount)
        }

    @Test
    fun `failure surfaces an error message`() =
        runTest {
            val repository = FakeCustomerRepository(Result.failure(RuntimeException("rede caiu")))

            val viewModel = ContatosViewModel(GetCustomersUseCase(repository), CreateCustomerUseCase(repository))

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
            val viewModel = ContatosViewModel(GetCustomersUseCase(repository), CreateCustomerUseCase(repository))

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
            val viewModel = ContatosViewModel(GetCustomersUseCase(repository), CreateCustomerUseCase(repository))

            viewModel.onFilterChange(ContatoFiltro.INATIVO)

            assertTrue(viewModel.uiState.value.visibleCustomers.all { !it.active })
        }

    @Test
    fun `saving a valid contact creates it and reloads the list`() =
        runTest {
            val repository = FakeCustomerRepository(Result.success(emptyList()))
            val viewModel = ContatosViewModel(GetCustomersUseCase(repository), CreateCustomerUseCase(repository))
            val loadsAfterInit = repository.callCount

            viewModel.onAddContactClick()
            viewModel.onFormNameChange("Maria Silva")
            viewModel.onFormDocumentChange("12345678901")
            viewModel.onFormVipChange(true)
            viewModel.saveContact()

            assertEquals(1, repository.createCount)
            assertEquals("Maria Silva", repository.lastCreated?.name)
            assertTrue(repository.lastCreated?.vip == true)
            // dialog closed and the list was reloaded
            assertEquals(null, viewModel.uiState.value.addForm)
            assertEquals(loadsAfterInit + 1, repository.callCount)
        }

    @Test
    fun `invalid contact surfaces a validation error without calling the repository`() =
        runTest {
            val repository = FakeCustomerRepository(Result.success(emptyList()))
            val viewModel = ContatosViewModel(GetCustomersUseCase(repository), CreateCustomerUseCase(repository))

            viewModel.onAddContactClick()
            viewModel.onFormNameChange("Jo")
            viewModel.onFormDocumentChange("123")
            viewModel.saveContact()

            assertEquals(0, repository.createCount)
            assertEquals("Nome deve ter ao menos 3 caracteres", viewModel.uiState.value.errorMessage)
            // dialog stays open so the user can fix the input
            assertTrue(viewModel.uiState.value.addForm != null)
            assertFalse(viewModel.uiState.value.addForm!!.isSaving)
        }
}
