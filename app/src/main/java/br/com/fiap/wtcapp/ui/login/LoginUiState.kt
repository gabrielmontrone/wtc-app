package br.com.fiap.wtcapp.ui.login

/** Immutable representation of everything the login screen needs to render. */
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val operatorProfile: Boolean = true,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isLoggedIn: Boolean = false,
)
