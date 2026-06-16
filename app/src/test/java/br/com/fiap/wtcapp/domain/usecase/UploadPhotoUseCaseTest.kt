package br.com.fiap.wtcapp.domain.usecase

import br.com.fiap.wtcapp.FakeAttachmentRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class UploadPhotoUseCaseTest {
    @Test
    fun `returns the public url on success`() =
        runTest {
            val repository = FakeAttachmentRepository(Result.success("https://cdn/foto.jpg"))
            val useCase = UploadPhotoUseCase(repository)

            val result = useCase("foto.jpg", "image/jpeg", byteArrayOf(1, 2, 3))

            assertEquals("https://cdn/foto.jpg", result.getOrNull())
            assertEquals("foto.jpg", repository.lastFileName)
        }

    @Test
    fun `rejects empty files without calling the repository`() =
        runTest {
            val repository = FakeAttachmentRepository()
            val useCase = UploadPhotoUseCase(repository)

            val result = useCase("foto.jpg", "image/jpeg", ByteArray(0))

            assertTrue(result.isFailure)
            assertEquals(0, repository.uploadCallCount)
        }
}
