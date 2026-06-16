package br.com.fiap.wtcapp.ui.auditoria

import br.com.fiap.wtcapp.FakeAuditRepository
import br.com.fiap.wtcapp.MainDispatcherRule
import br.com.fiap.wtcapp.domain.model.AuditEvent
import br.com.fiap.wtcapp.domain.usecase.GetAuditEventsUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuditoriaViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private fun viewModel(repository: FakeAuditRepository) = AuditoriaViewModel(GetAuditEventsUseCase(repository))

    @Test
    fun `loads audit events on init`() =
        runTest {
            val repository =
                FakeAuditRepository(
                    Result.success(
                        listOf(AuditEvent("1", "SUSPICIOUS_MESSAGE", "ana@wtc.com", "risco HIGH", "2026-06-16T12:00:00Z")),
                    ),
                )

            val state = viewModel(repository).uiState.value

            assertEquals(1, state.events.size)
            assertEquals("SUSPICIOUS_MESSAGE", state.events.first().action)
            assertFalse(state.isLoading)
        }

    @Test
    fun `surfaces an error when loading fails`() =
        runTest {
            val repository = FakeAuditRepository(Result.failure(RuntimeException("falha")))

            val state = viewModel(repository).uiState.value

            assertEquals("falha", state.errorMessage)
            assertFalse(state.isLoading)
        }
}
