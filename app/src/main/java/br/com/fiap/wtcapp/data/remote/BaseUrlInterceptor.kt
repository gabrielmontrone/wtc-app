package br.com.fiap.wtcapp.data.remote

import br.com.fiap.wtcapp.data.local.ServerConfigStorage
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

/**
 * Redirects every request to the base URL currently configured in [ServerConfigStorage],
 * read fresh on each call. This lets the user switch backends at runtime (emulator vs. a
 * physical phone pointing at the host's LAN IP) without rebuilding the app or recreating
 * Retrofit — Retrofit keeps a fixed placeholder base URL and this swaps the scheme/host/port.
 */
class BaseUrlInterceptor
    @Inject
    constructor(
        private val serverConfig: ServerConfigStorage,
    ) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            val configured = serverConfig.baseUrl().toHttpUrlOrNull()
                ?: return chain.proceed(request)

            val newUrl =
                request.url.newBuilder()
                    .scheme(configured.scheme)
                    .host(configured.host)
                    .port(configured.port)
                    .build()

            return chain.proceed(request.newBuilder().url(newUrl).build())
        }
    }
