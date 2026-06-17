package br.com.fiap.wtcapp.domain.usecase

import br.com.fiap.wtcapp.FakeCustomerRepository
import br.com.fiap.wtcapp.domain.model.NewContact
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CreateCustomerUseCaseTest {
    private fun contact(
        name: String = "Maria Silva",
        document: String = "12345678901",
        email: String? = null,
        vip: Boolean = false,
    ) = NewContact(name = name, document = document, vip = vip, loyalty = false, active = true, email = email)

    @Test
    fun `short name fails without hitting the repository`() =
        runTest {
            val repository = FakeCustomerRepository()
            val useCase = CreateCustomerUseCase(repository)

            val result = useCase(contact(name = "Jo"))

            assertTrue(result.isFailure)
            assertEquals(0, repository.createCount)
        }

    @Test
    fun `non-numeric document fails without hitting the repository`() =
        runTest {
            val repository = FakeCustomerRepository()
            val useCase = CreateCustomerUseCase(repository)

            val result = useCase(contact(document = "123.456.789-01"))

            assertTrue(result.isFailure)
            assertEquals(0, repository.createCount)
        }

    @Test
    fun `document with wrong length fails without hitting the repository`() =
        runTest {
            val repository = FakeCustomerRepository()
            val useCase = CreateCustomerUseCase(repository)

            val result = useCase(contact(document = "123"))

            assertTrue(result.isFailure)
            assertEquals(0, repository.createCount)
        }

    @Test
    fun `valid input trims and delegates to the repository`() =
        runTest {
            val repository = FakeCustomerRepository()
            val useCase = CreateCustomerUseCase(repository)

            val result = useCase(contact(name = "  Maria Silva  ", document = "  12345678901  ", vip = true))

            assertTrue(result.isSuccess)
            assertEquals(1, repository.createCount)
            assertEquals("Maria Silva", repository.lastCreated?.name)
            assertEquals("12345678901", repository.lastCreated?.document)
            assertEquals(true, repository.lastCreated?.vip)
        }

    @Test
    fun `blank email is forwarded as null so the contact stays CRM-only`() =
        runTest {
            val repository = FakeCustomerRepository()
            val useCase = CreateCustomerUseCase(repository)

            val result = useCase(contact(email = "  "))

            assertTrue(result.isSuccess)
            assertEquals(null, repository.lastCreatedEmail)
        }

    @Test
    fun `valid email is trimmed and forwarded to link the contact to a client account`() =
        runTest {
            val repository = FakeCustomerRepository()
            val useCase = CreateCustomerUseCase(repository)

            val result = useCase(contact(email = "  maria@acme.com "))

            assertTrue(result.isSuccess)
            assertEquals("maria@acme.com", repository.lastCreatedEmail)
        }

    @Test
    fun `malformed email fails without hitting the repository`() =
        runTest {
            val repository = FakeCustomerRepository()
            val useCase = CreateCustomerUseCase(repository)

            val result = useCase(contact(email = "not-an-email"))

            assertTrue(result.isFailure)
            assertEquals(0, repository.createCount)
        }
}
