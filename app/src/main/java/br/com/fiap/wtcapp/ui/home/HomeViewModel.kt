package br.com.fiap.wtcapp.ui.home

import androidx.lifecycle.ViewModel
import br.com.fiap.wtcapp.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Exposes the current session's role so the Home menu can adapt: an OPERADOR sees the full
 * console (CRM, campanhas, segmentos, auditoria), while a CLIENTE only reaches their own
 * conversation. The client's conversation id equals their user id (set at registration).
 */
@HiltViewModel
class HomeViewModel
    @Inject
    constructor(
        private val authRepository: AuthRepository,
    ) : ViewModel() {
        val isOperator: Boolean = authRepository.currentRole().equals("OPERADOR", ignoreCase = true)
        val conversationId: String? = authRepository.currentUserId()

        /** Clears the stored session so the user returns to the login flow. */
        fun logout() = authRepository.logout()
    }
