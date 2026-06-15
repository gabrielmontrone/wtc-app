package br.com.fiap.wtcapp.data.repository

import br.com.fiap.wtcapp.data.remote.WtcApi
import br.com.fiap.wtcapp.data.remote.dto.CampaignRequestDto
import br.com.fiap.wtcapp.data.remote.dto.toDomain
import br.com.fiap.wtcapp.di.IoDispatcher
import br.com.fiap.wtcapp.domain.model.Campaign
import br.com.fiap.wtcapp.domain.repository.CampaignRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

class CampaignRepositoryImpl
    @Inject
    constructor(
        private val api: WtcApi,
        @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    ) : CampaignRepository {
        override suspend fun campaigns(): Result<List<Campaign>> =
            withContext(ioDispatcher) {
                runCatching { api.listCampaigns().map { it.toDomain() } }
            }

        override suspend fun create(
            name: String,
            content: String,
            segmentId: String?,
        ): Result<Campaign> =
            withContext(ioDispatcher) {
                runCatching {
                    api.createCampaign(
                        CampaignRequestDto(
                            name = name,
                            description = null,
                            type = "MESSAGE",
                            content = content,
                            segmentTargetId = segmentId,
                            callCode = "CALL-${UUID.randomUUID().toString().take(CALL_CODE_LENGTH)}",
                        ),
                    ).toDomain()
                }
            }

        private companion object {
            const val CALL_CODE_LENGTH = 8
        }
    }
