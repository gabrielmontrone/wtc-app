package br.com.fiap.wtcapp.api

import br.com.fiap.wtcapp.BuildConfig

/**
 * Network configuration.
 *
 * [BASE_URL] is supplied at build time via `BuildConfig` (see `app/build.gradle.kts`). It
 * defaults to the hosted demo so the app runs with no setup; override it for local development
 * by setting `apiBaseUrl` in `local.properties` — no source changes required.
 */
object ApiConfig {
    val BASE_URL: String = BuildConfig.BASE_URL
}
