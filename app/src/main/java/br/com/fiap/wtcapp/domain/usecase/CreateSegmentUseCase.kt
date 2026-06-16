package br.com.fiap.wtcapp.domain.usecase

import br.com.fiap.wtcapp.domain.model.Segment
import br.com.fiap.wtcapp.domain.repository.SegmentRepository
import javax.inject.Inject

/**
 * Creates a segmentation rule after validating the form. Validation lives here so the rule
 * stays unit-testable and mirrors the backend's `@NotBlank name` on
 * [com.wtc.segment.dto.CreateSegmentRequest], mirroring [CreateCampaignUseCase].
 */
class CreateSegmentUseCase
    @Inject
    constructor(
        private val repository: SegmentRepository,
    ) {
        suspend operator fun invoke(
            name: String,
            vip: Boolean,
            active: Boolean,
            minScore: Int?,
            minLoyalty: Int?,
        ): Result<Segment> {
            val trimmedName = name.trim()
            if (trimmedName.isBlank()) {
                return Result.failure(IllegalArgumentException("Nome do segmento é obrigatório"))
            }
            return repository.createSegment(trimmedName, vip, active, minScore, minLoyalty)
        }
    }
