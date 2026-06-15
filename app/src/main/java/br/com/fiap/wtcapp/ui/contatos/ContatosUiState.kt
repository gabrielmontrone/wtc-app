package br.com.fiap.wtcapp.ui.contatos

import br.com.fiap.wtcapp.domain.model.Customer

/** Filters offered above the contact list. */
enum class ContatoFiltro(val label: String) {
    TODOS("Todos"),
    VIP("VIP"),
    FIDELIDADE("Fidelidade"),
    ATIVO("Ativo"),
    INATIVO("Inativo"),
}

data class ContatosUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val customers: List<Customer> = emptyList(),
    val search: String = "",
    val filter: ContatoFiltro = ContatoFiltro.TODOS,
) {
    /** Contacts after applying the active filter and the search term. */
    val visibleCustomers: List<Customer>
        get() =
            customers.filter { customer ->
                matchesFilter(customer) && matchesSearch(customer)
            }

    private fun matchesFilter(customer: Customer): Boolean =
        when (filter) {
            ContatoFiltro.TODOS -> true
            ContatoFiltro.VIP -> customer.vip
            ContatoFiltro.FIDELIDADE -> customer.loyalty
            ContatoFiltro.ATIVO -> customer.active
            ContatoFiltro.INATIVO -> !customer.active
        }

    private fun matchesSearch(customer: Customer): Boolean =
        search.isBlank() ||
            customer.name.contains(search, ignoreCase = true) ||
            customer.document.contains(search, ignoreCase = true)
}
