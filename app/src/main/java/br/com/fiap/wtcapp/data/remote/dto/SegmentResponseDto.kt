package br.com.fiap.wtcapp.data.remote.dto

import br.com.fiap.wtcapp.domain.model.Segment
import kotlinx.serialization.Serializable

@Serializable
data class SegmentResponseDto(
    val id: String,
    val name: String,
    val vip: Boolean = false,
    val active: Boolean = true,
    val minScore: Int? = null,
    val minLoyalty: Int? = null,
)

@Serializable
data class SegmentRequestDto(
    val name: String,
    val vip: Boolean,
    val active: Boolean,
    val minScore: Int? = null,
    val minLoyalty: Int? = null,
)

fun SegmentResponseDto.toDomain(): Segment =
    Segment(
        id = id,
        name = name,
        vip = vip,
        active = active,
        minScore = minScore,
        minLoyalty = minLoyalty,
    )
