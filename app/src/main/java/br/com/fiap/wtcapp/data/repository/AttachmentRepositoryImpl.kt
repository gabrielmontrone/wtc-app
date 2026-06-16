package br.com.fiap.wtcapp.data.repository

import br.com.fiap.wtcapp.data.remote.WtcApi
import br.com.fiap.wtcapp.data.remote.dto.UploadRequestDto
import br.com.fiap.wtcapp.di.IoDispatcher
import br.com.fiap.wtcapp.domain.repository.AttachmentRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

class AttachmentRepositoryImpl
    @Inject
    constructor(
        private val api: WtcApi,
        @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    ) : AttachmentRepository {
        override suspend fun uploadPhoto(
            fileName: String,
            contentType: String,
            bytes: ByteArray,
        ): Result<String> =
            withContext(ioDispatcher) {
                runCatching {
                    val upload =
                        api.requestUpload(
                            UploadRequestDto(
                                fileName = fileName,
                                contentType = contentType,
                                fileSize = bytes.size.toLong(),
                            ),
                        )
                    val body = bytes.toRequestBody(contentType.toMediaTypeOrNull())
                    api.uploadFile(upload.uploadUrl, body).close()
                    upload.fileUrl
                }
            }
    }
