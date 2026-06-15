package br.com.fiap.wtcapp.ui.criarcampanha

import br.com.fiap.wtcapp.domain.model.Segment

data class CriarCampanhaUiState(
    val title: String = "",
    val message: String = "",
    val segments: List<Segment> = emptyList(),
    val selectedSegmentId: String? = null,
    val isLoadingSegments: Boolean = true,
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
    val isCreated: Boolean = false,
) {
    val canSubmit: Boolean
        get() = !isSubmitting && !isLoadingSegments && segments.isNotEmpty()
}
