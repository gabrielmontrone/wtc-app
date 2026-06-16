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
            val original = chain.request()

            // Pre-signed uploads carry their own signature; never attach our bearer token.
            if (original.header(WtcApi.NO_AUTH_HEADER) != null) {
                val cleaned = original.newBuilder().removeHeader(WtcApi.NO_AUTH_HEADER).build()
                return chain.proceed(cleaned)
            }

            val token = sessionStorage.token()
            val request =
                if (token.isNullOrBlank()) {
                    original
                } else {
                    original.newBuilder()
                        .addHeader("Authorization", "Bearer $token")
                        .build()
                }
            return chain.proceed(request)
        }
    }
