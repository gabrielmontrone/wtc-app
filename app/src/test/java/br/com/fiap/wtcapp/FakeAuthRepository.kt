package br.com.fiap.wtcapp

import br.com.fiap.wtcapp.domain.model.Session
import br.com.fiap.wtcapp.domain.repository.AuthRepository

/** In-memory test double for [AuthRepository] — lets us drive success/failure paths. */
class FakeAuthRepository(
    private var result: Result<Session> = Result.success(Session("token", "OPERATOR")),
    private var registerResult: Result<Unit> = Result.success(Unit),
    private val roleValue: String? = "OPERADOR",
    private val userIdValue: String? = "user1",
) : AuthRepository {
    var loginCallCount = 0
        private set
    var lastEmail: String? = null
        private set
    var lastPassword: String? = null
        private set
    var registerCallCount = 0
        private set
    var lastRegisterEmail: String? = null
        private set
    var lastRegisterRole: String? = null
        private set
    var googleCallCount = 0
        private set
    var lastGoogleIdToken: String? = null
        private set
    var logoutCallCount = 0
        private set

    private var storedToken: String? = null

    fun setResult(result: Result<Session>) {
        this.result = result
    }

    fun setRegisterResult(result: Result<Unit>) {
        this.registerResult = result
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

    override suspend fun register(
        email: String,
        password: String,
        role: String,
    ): Result<Unit> {
        registerCallCount++
        lastRegisterEmail = email
        lastRegisterRole = role
        return registerResult
    }

    override suspend fun loginWithGoogle(idToken: String): Result<Session> {
        googleCallCount++
        lastGoogleIdToken = idToken
        return result.onSuccess { storedToken = it.token }
    }

    override fun currentToken(): String? = storedToken

    override fun currentRole(): String? = roleValue

    override fun currentUserId(): String? = userIdValue

    override fun logout() {
        logoutCallCount++
        storedToken = null
    }
}
