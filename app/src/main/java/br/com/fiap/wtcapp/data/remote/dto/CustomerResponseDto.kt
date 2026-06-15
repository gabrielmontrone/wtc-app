package br.com.fiap.wtcapp.data.remote.dto

import br.com.fiap.wtcapp.domain.model.Customer
import kotlinx.serialization.Serializable

@Serializable
data class CustomerResponseDto(
    val id: String,
    val name: String,
    val document: String = "",
    val vip: Boolean = false,
    val fidelidade: Boolean = false,
    val ativo: Boolean = true,
)

fun CustomerResponseDto.toDomain(): Customer =
    Customer(
        id = id,
        name = name,
        document = document,
        vip = vip,
        loyalty = fidelidade,
        active = ativo,
    )
