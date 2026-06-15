package br.com.fiap.wtcapp.domain.model

/** A messaging campaign together with its operational metrics. */
data class Campaign(
    val id: String,
    val name: String,
    val description: String?,
    val type: String,
    val status: String,
    val totalSends: Long,
    val successSends: Long,
    val responseCount: Long,
)
