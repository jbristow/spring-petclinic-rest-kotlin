package petclinic.api.pettypes

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.tuple
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import javax.transaction.Transactional

@ExtendWith(SpringExtension::class)
@SpringBootTest
class PetTypeRepositoryTests(
    @Autowired private val petTypeRepository: PetTypeRepository
) {

    @Test
    fun shouldFindPetTypeById() {
        val petType = petTypeRepository.findById(1).get()
        assertThat(petType.name).isEqualTo("cat")
    }

    @Test
    fun shouldFindAllPetTypes() {
        assertThat(petTypeRepository.findAll()).extracting("id", "name")
            .contains(tuple(1, "cat"), tuple(3, "lizard"))
    }

    @Test
    @Transactional
    fun shouldInsertPetType() {
        val petTypes = petTypeRepository.findAll()
        val found = petTypes.count()

        val petType = PetType()
        petType.name = "tiger"

        petTypeRepository.save(petType)
        assertThat(petType.id).isNotNull()

        val petTypesActual = petTypeRepository.findAll()
        assertThat(petTypesActual).hasSize(found + 1)
    }

    @Test
    @Transactional
    fun shouldUpdatePetType() {
        val petType = petTypeRepository.findById(1).get()
        val oldLastName = petType.name
        val newLastName = "${oldLastName}X"
        petType.name = newLastName

        petTypeRepository.save(petType)

        val petTypeActual = petTypeRepository.findById(1).get()
        assertThat(petTypeActual.name).isEqualTo(newLastName)
    }

    @Test
    @Transactional
    fun shouldDeletePetType() {
        val petType = petTypeRepository.findById(1).get()
        petTypeRepository.delete(petType)

        val petTypeActual = petTypeRepository.findById(1)
        assertThat(petTypeActual).isEmpty
    }
}
