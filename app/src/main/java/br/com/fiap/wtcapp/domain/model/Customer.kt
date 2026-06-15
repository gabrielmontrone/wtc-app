package br.com.fiap.wtcapp.domain.model

/** A CRM contact. Pure domain model, free of network/Android concerns. */
data class Customer(
    val id: String,
    val name: String,
    val document: String,
    val vip: Boolean,
    val loyalty: Boolean,
    val active: Boolean,
)
