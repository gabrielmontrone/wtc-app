package br.com.fiap.wtcapp.domain.repository

import br.com.fiap.wtcapp.domain.model.Segment

interface SegmentRepository {
    suspend fun segments(): Result<List<Segment>>
}
