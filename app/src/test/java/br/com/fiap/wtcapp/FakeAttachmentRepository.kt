package br.com.fiap.wtcapp

import br.com.fiap.wtcapp.domain.repository.AttachmentRepository

class FakeAttachmentRepository(
    private var uploadResult: Result<String> = Result.success("https://cdn/foto.jpg"),
) : AttachmentRepository {
    var lastFileName: String? = null
        private set
    var lastContentType: String? = null
        private set
    var uploadCallCount = 0
        private set

    fun setUploadResult(result: Result<String>) {
        uploadResult = result
    }

    override suspend fun uploadPhoto(
        fileName: String,
        contentType: String,
        bytes: ByteArray,
    ): Result<String> {
        uploadCallCount++
        lastFileName = fileName
        lastContentType = contentType
        return uploadResult
    }
}
