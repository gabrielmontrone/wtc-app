package br.com.fiap.wtcapp.data.repository

import br.com.fiap.wtcapp.data.local.ServerConfigStorage
import br.com.fiap.wtcapp.data.remote.WtcApi
import br.com.fiap.wtcapp.di.IoDispatcher
import br.com.fiap.wtcapp.domain.repository.AttachmentRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

class AttachmentRepositoryImpl
    @Inject
    constructor(
        private val api: WtcApi,
        private val serverConfig: ServerConfigStorage,
        @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    ) : AttachmentRepository {
        override suspend fun uploadPhoto(
            fileName: String,
            contentType: String,
            bytes: ByteArray,
        ): Result<String> =
            withContext(ioDispatcher) {
                runCatching {
                    val body = bytes.toRequestBody(contentType.toMediaTypeOrNull())
                    val part = MultipartBody.Part.createFormData("file", fileName, body)
                    val response = api.uploadAttachment(part)
                    // Backend returns a relative URL ("/api/v1/attachments/{id}"); make it absolute
                    // against the currently configured backend so Coil can load it on-device.
                    serverConfig.baseUrl().trimEnd('/') + response.url
                }
            }
    }
