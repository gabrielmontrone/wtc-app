package br.com.fiap.wtcapp.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequestDto(
    val email: String,
    val password: String,
    val role: String,
)

@Serializable
data class RegisterResponseDto(
    val message: String = "",
    val role: String = "",
)
