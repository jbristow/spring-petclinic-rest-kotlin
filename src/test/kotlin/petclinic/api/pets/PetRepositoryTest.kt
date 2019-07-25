package petclinic.api.pets

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.tuple
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import petclinic.api.owners.OwnerRepository
import petclinic.api.pettypes.PetTypeRepository
import javax.transaction.Transactional

@ExtendWith(SpringExtension::class)
@SpringBootTest
class PetRepositoryTest(
    @Autowired private val ownerRepository: OwnerRepository,
    @Autowired private val petRepository: PetRepository,
    @Autowired private val petTypeRepository: PetTypeRepository
) {
    @Test
    fun shouldFindPetWithCorrectId() {
        val pet7 = petRepository.findById(7).get()
        assertThat(pet7.name).isEqualTo("Samantha")
        assertThat(pet7.owner.firstName).isEqualTo("Jean")
    }

    @Test
    @Transactional
    fun shouldInsertPetIntoDatabaseAndGenerateId() {
        val owner6 = ownerRepository.findById(6).get()
        val found = owner6.pets.size

        val pet = Pet(
            name = "bowser",
            owner = owner6,
            type = petTypeRepository.findAll().find { it.id == 2 }!!
        )
        owner6.addPet(pet)
        assertThat(owner6.pets.size).isEqualTo(found + 1)

        petRepository.save(pet)
        ownerRepository.save(owner6)

        val owner6Actual = ownerRepository.findById(6).get()
        assertThat(owner6Actual.pets).hasSize(found + 1)

        // checks that id has been generated
        assertThat(pet.id).isNotZero()
    }

    @Test
    @Transactional
    fun shouldUpdatePetName() {
        val pet7 = petRepository.findById(7).get()
        val oldName = pet7.name

        val newName = oldName + "X"
        pet7.name = newName
        petRepository.save(pet7)

        val pet7Actual = petRepository.findById(7)
        assertThat(pet7Actual).isNotEmpty
        assertThat(pet7Actual.get().name).isEqualTo(newName)
    }

    @Test
    fun shouldFindAllPets() {
        assertThat(petRepository.findAll()).extracting("id", "name")
            .contains(tuple(1, "Leo"), tuple(3, "Rosy"))
    }

    @Test
    @Transactional
    fun shouldDeletePet() {
        val pet = petRepository.findById(1).get()
        petRepository.delete(pet)
        val petActual = petRepository.findById(1)

        assertThat(petActual).isEmpty
    }
}
