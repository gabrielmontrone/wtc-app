package br.com.fiap.wtcapp.domain.usecase

import br.com.fiap.wtcapp.domain.model.Campaign
import br.com.fiap.wtcapp.domain.repository.CampaignRepository
import javax.inject.Inject

class GetCampaignsUseCase
    @Inject
    constructor(
        private val repository: CampaignRepository,
    ) {
        suspend operator fun invoke(): Result<List<Campaign>> = repository.campaigns()
    }
