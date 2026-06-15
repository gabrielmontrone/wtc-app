package br.com.fiap.wtcapp.domain.usecase

import br.com.fiap.wtcapp.domain.model.Session
import br.com.fiap.wtcapp.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Encapsulates the login business rule: validate input, then delegate to the repository.
 * Keeping this logic out of the ViewModel makes it trivially unit-testable.
 */
class LoginUseCase
    @Inject
    constructor(
        private val authRepository: AuthRepository,
    ) {
        suspend operator fun invoke(
            email: String,
            password: String,
        ): Result<Session> {
            val normalizedEmail = email.trim()
            if (normalizedEmail.isBlank() || password.isBlank()) {
                return Result.failure(IllegalArgumentException("Preencha e-mail e senha"))
            }
            return authRepository.login(normalizedEmail, password)
        }
    }
