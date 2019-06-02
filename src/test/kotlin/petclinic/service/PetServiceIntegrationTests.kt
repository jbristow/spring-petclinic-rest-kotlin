package petclinic.service

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.tuple
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import petclinic.api.owners.OwnerService
import petclinic.api.pets.Pet
import petclinic.api.pets.PetService
import petclinic.api.pettypes.PetTypeService
import java.util.Date
import javax.transaction.Transactional

@ExtendWith(SpringExtension::class)
@SpringBootTest
class PetServiceIntegrationTests(
    @Autowired private val ownerService: OwnerService,
    @Autowired private val petService: PetService,
    @Autowired private val petTypeService: PetTypeService
) {
    @Test
    fun shouldFindPetWithCorrectId() {
        val pet7 = petService.findPetById(7)!!
        assertThat(pet7.name).isEqualTo("Samantha")
        assertThat(pet7.owner?.firstName).isEqualTo("Jean")
    }

    @Test
    @Transactional
    fun shouldInsertPetIntoDatabaseAndGenerateId() {
        val owner6 = ownerService.findOwnerById(6)!!
        val found = owner6.pets.size

        val pet = Pet()
        pet.name = "bowser"
        val types = petTypeService.findAllPetTypes()
        print("types: $types")
        pet.type = types.find { it.id == 2 }
        pet.birthDate = Date()
        owner6.addPet(pet)
        assertThat(owner6.pets.size).isEqualTo(found + 1)

        petService.savePet(pet)
        ownerService.saveOwner(owner6)

        val owner6Actual = ownerService.findOwnerById(6)
        assertThat(owner6Actual?.pets).hasSize(found + 1)
        // checks that id has been generated
        assertThat(pet.id).isNotNull()
    }

    @Test
    @Transactional
    fun shouldUpdatePetName() {
        val pet7 = petService.findPetById(7)!!
        val oldName = pet7.name

        val newName = oldName + "X"
        pet7.name = newName
        petService.savePet(pet7)

        val pet7Actual = petService.findPetById(7)
        assertThat(pet7Actual?.name).isEqualTo(newName)
    }

    @Test
    fun shouldFindAllPets() {
        assertThat(petService.findAllPets()).extracting("id", "name")
            .contains(tuple(1, "Leo"), tuple(3, "Rosy"))
    }

    @Test
    @Transactional
    fun shouldDeletePet() {
        val pet = petService.findPetById(1)!!
        petService.deletePet(pet)
        val petActual = petService.findPetById(1)

        assertThat(petActual).isNull()
    }
}
