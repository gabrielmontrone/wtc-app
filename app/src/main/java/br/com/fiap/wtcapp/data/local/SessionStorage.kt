package br.com.fiap.wtcapp.data.local

import android.content.Context
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Persists the authenticated session encrypted at rest with EncryptedSharedPreferences
 * (AES-256, key held in the Android Keystore) and exposes the bearer token to
 * [br.com.fiap.wtcapp.data.remote.AuthInterceptor].
 */
@Singleton
class SessionStorage
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) {
        private val prefs by lazy {
            val masterKey =
                MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build()
            EncryptedSharedPreferences.create(
                context,
                PREFS_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
            )
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
            const val PREFS_NAME = "wtc_secure_session"
            const val TOKEN_KEY = "jwt_token"
            const val ROLE_KEY = "user_role"
        }
    }
