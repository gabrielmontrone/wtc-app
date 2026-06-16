package br.com.fiap.wtcapp.domain.usecase

import br.com.fiap.wtcapp.domain.repository.AttachmentRepository
import javax.inject.Inject

class UploadPhotoUseCase
    @Inject
    constructor(
        private val repository: AttachmentRepository,
    ) {
        suspend operator fun invoke(
            fileName: String,
            contentType: String,
            bytes: ByteArray,
        ): Result<String> {
            if (bytes.isEmpty()) {
                return Result.failure(IllegalArgumentException("Arquivo vazio"))
            }
            return repository.uploadPhoto(fileName, contentType, bytes)
        }
    }
