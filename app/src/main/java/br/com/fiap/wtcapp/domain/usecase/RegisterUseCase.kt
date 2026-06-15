package br.com.fiap.wtcapp.domain.usecase

import br.com.fiap.wtcapp.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Validates the sign-up form, then delegates to the repository. Mirrors [LoginUseCase],
 * keeping the rules out of the ViewModel so they stay unit-testable.
 */
class RegisterUseCase
    @Inject
    constructor(
        private val authRepository: AuthRepository,
    ) {
        suspend operator fun invoke(
            email: String,
            password: String,
            role: String,
        ): Result<Unit> {
            val normalizedEmail = email.trim()
            val validationError =
                when {
                    normalizedEmail.isBlank() || password.isBlank() -> "Preencha e-mail e senha"
                    password.length < MIN_PASSWORD_LENGTH -> "A senha deve ter ao menos $MIN_PASSWORD_LENGTH caracteres"
                    else -> null
                }
            if (validationError != null) {
                return Result.failure(IllegalArgumentException(validationError))
            }
            return authRepository.register(normalizedEmail, password, role)
        }

        private companion object {
            const val MIN_PASSWORD_LENGTH = 6
        }
    }
