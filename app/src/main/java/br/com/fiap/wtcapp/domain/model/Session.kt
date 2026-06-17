package br.com.fiap.wtcapp.domain.model

/**
 * Authenticated user session. Pure domain model with no Android or network dependencies.
 */
data class Session(
    val token: String,
    val role: String,
    val userId: String? = null,
)
