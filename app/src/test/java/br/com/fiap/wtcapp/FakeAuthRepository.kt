package br.com.fiap.wtcapp

import br.com.fiap.wtcapp.domain.model.Session
import br.com.fiap.wtcapp.domain.repository.AuthRepository

/** In-memory test double for [AuthRepository] — lets us drive success/failure paths. */
class FakeAuthRepository(
    private var result: Result<Session> = Result.success(Session("token", "OPERATOR")),
) : AuthRepository {
    var loginCallCount = 0
        private set
    var lastEmail: String? = null
        private set
    var lastPassword: String? = null
        private set

    private var storedToken: String? = null

    fun setResult(result: Result<Session>) {
        this.result = result
    }

    override suspend fun login(
        email: String,
        password: String,
    ): Result<Session> {
        loginCallCount++
        lastEmail = email
        lastPassword = password
        return result.onSuccess { storedToken = it.token }
    }

    override fun currentToken(): String? = storedToken

    override fun logout() {
        storedToken = null
    }
}
