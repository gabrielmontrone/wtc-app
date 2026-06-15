package br.com.fiap.wtcapp.ui.segmentos

import br.com.fiap.wtcapp.domain.model.Segment

data class SegmentosUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val segments: List<Segment> = emptyList(),
    val search: String = "",
) {
    val visibleSegments: List<Segment>
        get() = segments.filter { it.name.contains(search, ignoreCase = true) }
}
