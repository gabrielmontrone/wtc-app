package br.com.fiap.wtcapp.domain.repository

interface AttachmentRepository {
    /**
     * Uploads a photo's [bytes] and returns the public URL where it can be displayed.
     * Internally requests a pre-signed URL from the backend, uploads the bytes to it,
     * and confirms the upload.
     */
    suspend fun uploadPhoto(
        fileName: String,
        contentType: String,
        bytes: ByteArray,
    ): Result<String>
}
