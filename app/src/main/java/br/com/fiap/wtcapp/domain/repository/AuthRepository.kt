package br.com.fiap.wtcapp.domain.repository

import br.com.fiap.wtcapp.domain.model.Session

/**
 * Abstraction over authentication. The domain layer depends on this interface,
 * while the concrete implementation lives in the data layer (Dependency Inversion).
 */
interface AuthRepository {
    suspend fun login(
        email: String,
        password: String,
    ): Result<Session>

    suspend fun register(
        email: String,
        password: String,
        role: String,
    ): Result<Unit>

    /** Exchanges a Google ID token for an app session (creates the account on first sign-in). */
    suspend fun loginWithGoogle(idToken: String): Result<Session>

    fun currentToken(): String?

    fun logout()
}
