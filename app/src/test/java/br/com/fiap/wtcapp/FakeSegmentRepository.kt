package br.com.fiap.wtcapp

import br.com.fiap.wtcapp.domain.model.Segment
import br.com.fiap.wtcapp.domain.repository.SegmentRepository

class FakeSegmentRepository(
    private var result: Result<List<Segment>> = Result.success(emptyList()),
) : SegmentRepository {
    var callCount = 0
        private set
    var createCount = 0
        private set
    var lastCreated: Segment? = null
        private set

    private var createResult: Result<Segment>? = null

    fun setResult(result: Result<List<Segment>>) {
        this.result = result
    }

    fun setCreateResult(result: Result<Segment>) {
        this.createResult = result
    }

    override suspend fun segments(): Result<List<Segment>> {
        callCount++
        return result
    }

    override suspend fun createSegment(
        name: String,
        vip: Boolean,
        active: Boolean,
        minScore: Int?,
        minLoyalty: Int?,
    ): Result<Segment> {
        createCount++
        val created = Segment("new-seg", name, vip, active, minScore, minLoyalty)
        lastCreated = created
        return createResult ?: Result.success(created)
    }
}
