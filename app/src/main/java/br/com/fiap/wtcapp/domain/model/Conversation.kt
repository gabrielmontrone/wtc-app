package br.com.fiap.wtcapp.domain.model

/** A conversation thread between an operator and a customer. */
data class Conversation(
    val id: String,
    val customerId: String,
    val operatorId: String?,
    val status: String,
)
