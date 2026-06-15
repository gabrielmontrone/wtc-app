package br.com.fiap.wtcapp.ui.register

data class RegisterUiState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val operatorProfile: Boolean = true,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isRegistered: Boolean = false,
) {
    /** Backend role derived from the selected profile. */
    val role: String get() = if (operatorProfile) "OPERADOR" else "CLIENTE"
}
