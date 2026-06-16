package br.com.fiap.wtcapp.domain.usecase

import br.com.fiap.wtcapp.FakeCustomerRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CreateCustomerUseCaseTest {
    @Test
    fun `short name fails without hitting the repository`() =
        runTest {
            val repository = FakeCustomerRepository()
            val useCase = CreateCustomerUseCase(repository)

            val result = useCase("Jo", "12345678901", vip = false, loyalty = false, active = true)

            assertTrue(result.isFailure)
            assertEquals(0, repository.createCount)
        }

    @Test
    fun `non-numeric document fails without hitting the repository`() =
        runTest {
            val repository = FakeCustomerRepository()
            val useCase = CreateCustomerUseCase(repository)

            val result = useCase("Maria Silva", "123.456.789-01", vip = false, loyalty = false, active = true)

            assertTrue(result.isFailure)
            assertEquals(0, repository.createCount)
        }

    @Test
    fun `document with wrong length fails without hitting the repository`() =
        runTest {
            val repository = FakeCustomerRepository()
            val useCase = CreateCustomerUseCase(repository)

            val result = useCase("Maria Silva", "123", vip = false, loyalty = false, active = true)

            assertTrue(result.isFailure)
            assertEquals(0, repository.createCount)
        }

    @Test
    fun `valid input trims and delegates to the repository`() =
        runTest {
            val repository = FakeCustomerRepository()
            val useCase = CreateCustomerUseCase(repository)

            val result = useCase("  Maria Silva  ", "  12345678901  ", vip = true, loyalty = false, active = true)

            assertTrue(result.isSuccess)
            assertEquals(1, repository.createCount)
            assertEquals("Maria Silva", repository.lastCreated?.name)
            assertEquals("12345678901", repository.lastCreated?.document)
            assertEquals(true, repository.lastCreated?.vip)
        }
}
