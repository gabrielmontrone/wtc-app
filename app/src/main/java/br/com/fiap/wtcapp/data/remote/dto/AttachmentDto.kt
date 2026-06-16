package br.com.fiap.wtcapp.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class UploadRequestDto(
    val fileName: String,
    val contentType: String,
    val fileSize: Long,
)

@Serializable
data class UploadResponseDto(
    val attachmentId: String,
    val uploadUrl: String,
    val fileUrl: String,
)
