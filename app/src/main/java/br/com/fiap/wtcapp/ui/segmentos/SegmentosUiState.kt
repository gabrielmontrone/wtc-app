package br.com.fiap.wtcapp.ui.segmentos

import br.com.fiap.wtcapp.domain.model.Segment

data class SegmentosUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val segments: List<Segment> = emptyList(),
    val search: String = "",
    val addForm: AddSegmentForm? = null,
) {
    val visibleSegments: List<Segment>
        get() = segments.filter { it.name.contains(search, ignoreCase = true) }
}

/** State of the "Novo segmento" dialog. Present only while the dialog is open. */
data class AddSegmentForm(
    val name: String = "",
    val vip: Boolean = false,
    val active: Boolean = true,
    val minScore: String = "",
    val minLoyalty: String = "",
    val isSaving: Boolean = false,
)
