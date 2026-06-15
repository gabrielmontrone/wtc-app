package br.com.fiap.wtcapp.data.local

import android.content.Context
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Persists the authenticated session and exposes the bearer token to [AuthInterceptor].
 *
 * TODO (security wave): back this with EncryptedSharedPreferences.
 */
@Singleton
class SessionStorage
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) {
        private val prefs by lazy {
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        }

        fun save(
            token: String,
            role: String,
        ) {
            prefs.edit {
                putString(TOKEN_KEY, token)
                putString(ROLE_KEY, role)
            }
        }

        fun token(): String? = prefs.getString(TOKEN_KEY, null)

        fun role(): String? = prefs.getString(ROLE_KEY, null)

        fun clear() = prefs.edit { clear() }

        private companion object {
            const val PREFS_NAME = "wtc_auth"
            const val TOKEN_KEY = "jwt_token"
            const val ROLE_KEY = "user_role"
        }
    }
