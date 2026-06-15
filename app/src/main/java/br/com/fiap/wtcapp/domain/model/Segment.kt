package br.com.fiap.wtcapp.domain.model

/** A customer segmentation rule. */
data class Segment(
    val id: String,
    val name: String,
    val vip: Boolean,
    val active: Boolean,
    val minScore: Int?,
    val minLoyalty: Int?,
)
