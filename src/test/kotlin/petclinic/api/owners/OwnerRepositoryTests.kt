package petclinic.api.owners

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
class OwnerRepositoryTests(
    @Autowired private val ownerRepository: OwnerRepository
) {

    @Test
    fun shouldFindOwnersByLastName() {
        ownerRepository.run {
            assertThat(findByLastName("Davis")).hasSize(2)
            assertThat(findByLastName("Daviss")).isEmpty()
        }
    }

    @Test
    fun shouldFindSingleOwnerWithPet() {
        val owner = ownerRepository.findById(1).get()
        assertThat(owner.lastName).startsWith("Franklin")
        assertThat(owner.pets).hasSize(1)

        val pet = owner.pets.toList()[0]
        assertThat(pet.type).isNotNull.hasFieldOrPropertyWithValue("name", "cat")
    }

    @Test
    @Transactional
    fun shouldInsertOwner() {
        val owners = ownerRepository.findByLastName("Schultz")
        val found = owners.size

        val owner = Owner(
            firstName = "Sam",
            lastName = "Schultz",
            address = "4, Evans Street",
            city = "Wollongong",
            telephone = "4444444444"
        )
        ownerRepository.save(owner)

        assertThat(owner.id).isNotEqualTo(0)

        val ownersActual = ownerRepository.findByLastName("Schultz")
        assertThat(ownersActual.size).isEqualTo(found + 1)
    }

    @Test
    @Transactional
    fun shouldUpdateOwner() {
        val owner = ownerRepository.findById(1).get()
        val newLastName = "${owner.lastName}X"

        owner.lastName = newLastName
        ownerRepository.save(owner)

        // retrieving new name from database
        ownerRepository.findById(1).get().run {
            assertThat(lastName).isEqualTo(newLastName)
        }
    }

    @Test
    fun shouldFindAllOwners() {
        assertThat(ownerRepository.findAll())
            .extracting("id", "firstName")
            .contains(
                tuple(1, "George"),
                tuple(3, "Eduardo")
            )
    }

    @Test
    @Transactional
    fun shouldDeleteOwner() {
        val owner = ownerRepository.findById(1).get()
        ownerRepository.delete(owner)

        assertThat(ownerRepository.findById(1)).isEmpty
    }
}
