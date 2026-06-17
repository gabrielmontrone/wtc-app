package br.com.fiap.wtcapp.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class AttachmentUploadResponseDto(
    val id: String,
    val url: String = "",
)
