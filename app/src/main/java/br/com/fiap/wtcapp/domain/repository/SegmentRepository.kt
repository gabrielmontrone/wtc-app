package br.com.fiap.wtcapp.domain.repository

import br.com.fiap.wtcapp.domain.model.Segment

interface SegmentRepository {
    suspend fun segments(): Result<List<Segment>>

    suspend fun createSegment(
        name: String,
        vip: Boolean,
        active: Boolean,
        minScore: Int?,
        minLoyalty: Int?,
    ): Result<Segment>
}
