package br.com.fiap.wtcapp.data.repository

import br.com.fiap.wtcapp.data.remote.WtcApi
import br.com.fiap.wtcapp.data.remote.dto.SegmentRequestDto
import br.com.fiap.wtcapp.data.remote.dto.toDomain
import br.com.fiap.wtcapp.di.IoDispatcher
import br.com.fiap.wtcapp.domain.model.Segment
import br.com.fiap.wtcapp.domain.repository.SegmentRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SegmentRepositoryImpl
    @Inject
    constructor(
        private val api: WtcApi,
        @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    ) : SegmentRepository {
        override suspend fun segments(): Result<List<Segment>> =
            withContext(ioDispatcher) {
                runCatching { api.listSegments().map { it.toDomain() } }
            }

        override suspend fun createSegment(
            name: String,
            vip: Boolean,
            active: Boolean,
            minScore: Int?,
            minLoyalty: Int?,
        ): Result<Segment> =
            withContext(ioDispatcher) {
                runCatching {
                    api.createSegment(
                        SegmentRequestDto(
                            name = name,
                            vip = vip,
                            active = active,
                            minScore = minScore,
                            minLoyalty = minLoyalty,
                        ),
                    ).toDomain()
                }
            }
    }
