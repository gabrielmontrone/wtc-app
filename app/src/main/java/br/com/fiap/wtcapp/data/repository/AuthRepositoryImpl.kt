package br.com.fiap.wtcapp.data.repository

import br.com.fiap.wtcapp.data.local.SessionStorage
import br.com.fiap.wtcapp.data.remote.WtcApi
import br.com.fiap.wtcapp.data.remote.dto.GoogleLoginRequestDto
import br.com.fiap.wtcapp.data.remote.dto.LoginRequestDto
import br.com.fiap.wtcapp.data.remote.dto.RegisterRequestDto
import br.com.fiap.wtcapp.data.remote.dto.toDomain
import br.com.fiap.wtcapp.di.IoDispatcher
import br.com.fiap.wtcapp.domain.model.Session
import br.com.fiap.wtcapp.domain.repository.AuthRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import javax.inject.Inject

class AuthRepositoryImpl
    @Inject
    constructor(
        private val api: WtcApi,
        private val sessionStorage: SessionStorage,
        @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    ) : AuthRepository {
        override suspend fun login(
            email: String,
            password: String,
        ): Result<Session> =
            withContext(ioDispatcher) {
                runCatching { api.login(LoginRequestDto(email, password)).toDomain() }
                    .onSuccess { session -> sessionStorage.save(session.token, session.role) }
            }

        override suspend fun register(
            email: String,
            password: String,
            role: String,
        ): Result<Unit> =
            withContext(ioDispatcher) {
                runCatching { api.register(RegisterRequestDto(email, password, role)) }
                    .map { }
                    .recoverCatching { throwable ->
                        if (throwable is HttpException && throwable.code() == HTTP_CONFLICT) {
                            error("E-mail já cadastrado")
                        }
                        throw throwable
                    }
            }

        override suspend fun loginWithGoogle(idToken: String): Result<Session> =
            withContext(ioDispatcher) {
                runCatching { api.loginWithGoogle(GoogleLoginRequestDto(idToken)).toDomain() }
                    .onSuccess { session -> sessionStorage.save(session.token, session.role) }
            }

        override fun currentToken(): String? = sessionStorage.token()

        override fun logout() = sessionStorage.clear()

        private companion object {
            const val HTTP_CONFLICT = 409
        }
    }
