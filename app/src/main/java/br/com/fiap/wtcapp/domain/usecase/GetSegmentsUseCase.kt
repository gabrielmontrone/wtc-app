package br.com.fiap.wtcapp.domain.usecase

import br.com.fiap.wtcapp.domain.model.Segment
import br.com.fiap.wtcapp.domain.repository.SegmentRepository
import javax.inject.Inject

class GetSegmentsUseCase
    @Inject
    constructor(
        private val repository: SegmentRepository,
    ) {
        suspend operator fun invoke(): Result<List<Segment>> = repository.segments()
    }
