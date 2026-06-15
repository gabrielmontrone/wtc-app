package br.com.fiap.wtcapp

import br.com.fiap.wtcapp.domain.model.Segment
import br.com.fiap.wtcapp.domain.repository.SegmentRepository

class FakeSegmentRepository(
    private var result: Result<List<Segment>> = Result.success(emptyList()),
) : SegmentRepository {
    var callCount = 0
        private set

    override suspend fun segments(): Result<List<Segment>> {
        callCount++
        return result
    }
}
