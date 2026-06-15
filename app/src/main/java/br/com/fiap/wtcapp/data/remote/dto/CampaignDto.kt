package br.com.fiap.wtcapp.data.remote.dto

import br.com.fiap.wtcapp.domain.model.Campaign
import kotlinx.serialization.Serializable

@Serializable
data class CampaignResponseDto(
    val id: String,
    val name: String,
    val description: String? = null,
    val type: String = "",
    val content: String = "",
    val segmentTargetId: String? = null,
    val callCode: String = "",
    val status: String = "",
    val totalSends: Long = 0,
    val successSends: Long = 0,
    val failureSends: Long = 0,
    val responseCount: Long = 0,
)

@Serializable
data class CampaignRequestDto(
    val name: String,
    val description: String?,
    val type: String,
    val content: String,
    val segmentTargetId: String?,
    val callCode: String,
)

fun CampaignResponseDto.toDomain(): Campaign =
    Campaign(
        id = id,
        name = name,
        description = description,
        type = type,
        status = status,
        totalSends = totalSends,
        successSends = successSends,
        responseCount = responseCount,
    )
