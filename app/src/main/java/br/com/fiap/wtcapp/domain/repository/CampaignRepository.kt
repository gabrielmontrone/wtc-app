package br.com.fiap.wtcapp.domain.repository

import br.com.fiap.wtcapp.domain.model.Campaign

interface CampaignRepository {
    suspend fun campaigns(): Result<List<Campaign>>

    suspend fun create(
        name: String,
        content: String,
        segmentId: String?,
    ): Result<Campaign>
}
