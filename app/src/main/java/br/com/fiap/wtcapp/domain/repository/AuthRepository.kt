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

    fun currentToken(): String?

    fun logout()
}
