package br.com.fiap.wtcapp.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class GoogleLoginRequestDto(
    val idToken: String,
)
