package br.com.fiap.wtcapp.data.remote.dto

import kotlinx.serialization.Serializable

/**
 * Minimal mirror of Spring Data's `Page<T>` JSON envelope. Only the fields the app
 * actually consumes are mapped; [Json.ignoreUnknownKeys] discards the rest.
 */
@Serializable
data class PageDto<T>(
    val content: List<T> = emptyList(),
    val totalElements: Long = 0,
    val totalPages: Int = 0,
    val number: Int = 0,
)
