package petclinic.api.pettypes

import org.assertj.core.api.Assertions
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
class PetTypeServiceIntegrationTests(
    @Autowired private val petTypeService: PetTypeService
) {

    @Test
    fun shouldFindPetTypeById() {
        val petType = petTypeService.findPetTypeById(1)
        Assertions.assertThat(petType?.name).isEqualTo("cat")
    }

    @Test
    fun shouldFindAllPetTypes() {
        assertThat(petTypeService.findAllPetTypes()).extracting("id", "name")
            .contains(tuple(1, "cat"), tuple(3, "lizard"))
    }

    @Test
    @Transactional
    fun shouldInsertPetType() {
        val petTypes = petTypeService.findAllPetTypes()
        val found = petTypes.size

        val petType = PetType()
        petType.name = "tiger"

        petTypeService.savePetType(petType)
        Assertions.assertThat(petType.id).isNotNull()

        val petTypesActual = petTypeService.findAllPetTypes()
        Assertions.assertThat(petTypesActual.size).isEqualTo(found + 1)
    }

    @Test
    @Transactional
    fun shouldUpdatePetType() {
        val petType = petTypeService.findPetTypeById(1)!!
        val oldLastName = petType.name
        val newLastName = "${oldLastName}X"
        petType.name = newLastName

        petTypeService.savePetType(petType)

        val petTypeActual = petTypeService.findPetTypeById(1)
        Assertions.assertThat(petTypeActual?.name).isEqualTo(newLastName)
    }

    @Test
    @Transactional
    fun shouldDeletePetType() {
        val petType = petTypeService.findPetTypeById(1)!!
        petTypeService.deletePetType(petType)

        val petTypeActual = petTypeService.findPetTypeById(1)
        Assertions.assertThat(petTypeActual).isNull()
    }
}
