package br.com.fiap.wtcapp.data.remote.dto

import br.com.fiap.wtcapp.domain.model.Session
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponseDto(
    val token: String,
    val role: String = "",
    val userId: String? = null,
)

/** Maps the network DTO to the domain model, keeping the two layers decoupled. */
fun LoginResponseDto.toDomain(): Session = Session(token = token, role = role, userId = userId)
