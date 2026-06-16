package br.com.fiap.wtcapp.domain.usecase

import br.com.fiap.wtcapp.domain.model.Session
import br.com.fiap.wtcapp.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Exchanges a Google ID token (obtained on-device via Credential Manager) for an app
 * session. Validation of the token itself happens server-side.
 */
class LoginWithGoogleUseCase
    @Inject
    constructor(
        private val authRepository: AuthRepository,
    ) {
        suspend operator fun invoke(idToken: String): Result<Session> {
            if (idToken.isBlank()) {
                return Result.failure(IllegalArgumentException("Token do Google ausente"))
            }
            return authRepository.loginWithGoogle(idToken)
        }
    }
