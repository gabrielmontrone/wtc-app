package br.com.fiap.wtcapp.domain.model

/**
 * Data required to create a CRM contact. [email] is optional: when present it links the contact
 * to an existing client account so the operator shares a conversation with it; when null the
 * contact is CRM-only.
 */
data class NewContact(
    val name: String,
    val document: String,
    val vip: Boolean,
    val loyalty: Boolean,
    val active: Boolean,
    val email: String? = null,
)
