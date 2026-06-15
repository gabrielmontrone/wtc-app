package br.com.fiap.wtcapp

import br.com.fiap.wtcapp.domain.model.Campaign
import br.com.fiap.wtcapp.domain.repository.CampaignRepository

class FakeCampaignRepository(
    private var listResult: Result<List<Campaign>> = Result.success(emptyList()),
    private var createResult: Result<Campaign> =
        Result.success(Campaign("c1", "Nova", null, "MESSAGE", "DRAFT", 0, 0, 0)),
) : CampaignRepository {
    var createCallCount = 0
        private set
    var lastName: String? = null
        private set
    var lastSegmentId: String? = null
        private set

    fun setCreateResult(result: Result<Campaign>) {
        createResult = result
    }

    override suspend fun campaigns(): Result<List<Campaign>> = listResult

    override suspend fun create(
        name: String,
        content: String,
        segmentId: String?,
    ): Result<Campaign> {
        createCallCount++
        lastName = name
        lastSegmentId = segmentId
        return createResult
    }
}
