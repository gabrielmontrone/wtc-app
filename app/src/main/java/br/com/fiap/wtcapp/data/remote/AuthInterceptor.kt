package br.com.fiap.wtcapp.data.remote

import br.com.fiap.wtcapp.data.local.SessionStorage
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

/**
 * Attaches `Authorization: Bearer <token>` to every outgoing request when a session
 * exists, so authenticated endpoints work through Retrofit without each call passing
 * the token manually. The login request simply has no token yet, so it goes unmodified.
 */
class AuthInterceptor
    @Inject
    constructor(
        private val sessionStorage: SessionStorage,
    ) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val token = sessionStorage.token()
            val request =
                if (token.isNullOrBlank()) {
                    chain.request()
                } else {
                    chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer $token")
                        .build()
                }
            return chain.proceed(request)
        }
    }
