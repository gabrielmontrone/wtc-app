package br.com.fiap.wtcapp.data.local

import android.content.Context
import androidx.core.content.edit
import br.com.fiap.wtcapp.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Stores the backend base URL the app talks to, so it can be changed at runtime
 * (e.g. an emulator uses `http://10.0.2.2:8080/`, a physical phone the host's LAN IP)
 * without rebuilding the APK. Falls back to the build-time default ([BuildConfig.BASE_URL])
 * until the user overrides it from the in-app server settings.
 *
 * Not a secret, so plain SharedPreferences (unlike [SessionStorage], which is encrypted).
 */
@Singleton
class ServerConfigStorage
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) {
        private val prefs by lazy {
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        }

        /** The URL currently in effect — the user's override, or the build-time default. */
        fun baseUrl(): String = prefs.getString(KEY_BASE_URL, null)?.takeIf { it.isNotBlank() } ?: DEFAULT_BASE_URL

        /** The build-time default, shown as the "restore" target in settings. */
        fun defaultBaseUrl(): String = DEFAULT_BASE_URL

        /** Persist a user-entered URL, normalizing scheme and trailing slash. */
        fun saveBaseUrl(raw: String) {
            prefs.edit { putString(KEY_BASE_URL, normalize(raw)) }
        }

        private fun normalize(raw: String): String {
            var url = raw.trim()
            if (url.isEmpty()) return DEFAULT_BASE_URL
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "http://$url"
            }
            if (!url.endsWith("/")) url += "/"
            return url
        }

        private companion object {
            const val PREFS_NAME = "wtc_server_config"
            const val KEY_BASE_URL = "base_url"
            val DEFAULT_BASE_URL: String = BuildConfig.BASE_URL
        }
    }
