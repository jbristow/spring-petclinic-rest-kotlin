package petclinic.api.vets

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import javax.transaction.Transactional

@ExtendWith(SpringExtension::class)
@SpringBootTest
class VetRepositoryTests(
    @Autowired private val vetService: VetRepository
) {

    @Test
    fun shouldFindVets() {
        val vets = vetService.findAll()

        vets.find { it.id == 3 }!!.run {
            assertThat(lastName).isEqualTo("Douglas")
            assertThat(nrOfSpecialties).isEqualTo(2)
            assertThat(specialties)
                .hasSize(2)
                .extracting("name")
                .containsOnly("dentistry", "surgery")
        }
    }

    @Test
    fun shouldFindVetDyId() {
        vetService.findById(1).get().run {
            assertThat(firstName).isEqualTo("James")
            assertThat(lastName).isEqualTo("Carter")
        }
    }

    @Test
    @Transactional
    fun shouldInsertVet() {
        val vets = vetService.findAll()
        val found = vets.count()

        val vet = Vet(
            firstName = "John",
            lastName = "Dow"
        )

        vetService.save(vet)
        assertThat(vet.id).isNotEqualTo(null)

        val vetsActual = vetService.findAll()
        assertThat(vetsActual).hasSize(found + 1)
    }

    @Test
    @Transactional
    fun shouldUpdateVet() {
        val vet = vetService.findById(1).get()
        val newLastName = "${vet.lastName}X"
        vet.lastName = newLastName
        vetService.save(vet)

        val vetActual = vetService.findById(1).get()
        assertThat(vetActual.lastName).isEqualTo(newLastName)
    }

    @Test
    @Transactional
    fun shouldDeleteVet() {
        val vet = vetService.findById(1).get()
        vetService.delete(vet)

        val vetActual = vetService.findById(1)
        assertThat(vetActual).isEmpty
    }
}
