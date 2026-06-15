package br.com.fiap.wtcapp.domain.usecase

import br.com.fiap.wtcapp.domain.model.Campaign
import br.com.fiap.wtcapp.domain.repository.CampaignRepository
import javax.inject.Inject

/**
 * Creates a campaign after validating the form. Keeping validation here (not in the
 * ViewModel) makes the rule unit-testable in isolation, mirroring [LoginUseCase].
 */
class CreateCampaignUseCase
    @Inject
    constructor(
        private val repository: CampaignRepository,
    ) {
        suspend operator fun invoke(
            name: String,
            content: String,
            segmentId: String?,
        ): Result<Campaign> {
            val trimmedName = name.trim()
            val trimmedContent = content.trim()
            val validationError =
                when {
                    trimmedName.isBlank() || trimmedContent.isBlank() -> "Preencha título e mensagem"
                    segmentId.isNullOrBlank() -> "Selecione um segmento"
                    else -> null
                }
            if (validationError != null) {
                return Result.failure(IllegalArgumentException(validationError))
            }
            return repository.create(trimmedName, trimmedContent, segmentId)
        }
    }
