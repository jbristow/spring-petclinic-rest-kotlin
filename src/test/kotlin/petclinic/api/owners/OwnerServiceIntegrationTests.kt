package petclinic.api.owners

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
class OwnerServiceIntegrationTests(
    @Autowired private val ownerService: OwnerService
) {

    @Test
    fun shouldFindOwnersByLastName() {
        var owners: Collection<Owner> = ownerService.findOwnerByLastName("Davis")
        Assertions.assertThat(owners.size).isEqualTo(2)

        owners = ownerService.findOwnerByLastName("Daviss")
        Assertions.assertThat(owners.isEmpty()).isTrue()
    }

    @Test
    fun shouldFindSingleOwnerWithPet() {
        val owner = ownerService.findOwnerById(1)
        Assertions.assertThat(owner?.lastName).startsWith("Franklin")
        Assertions.assertThat(owner?.pets?.size).isEqualTo(1)
        val pet = owner?.pets?.toList()?.get(0)
        Assertions.assertThat(pet?.type).isNotNull
        Assertions.assertThat(pet?.type?.name).isEqualTo("cat")
    }

    @Test
    @Transactional
    fun shouldInsertOwner() {
        val owners = ownerService.findOwnerByLastName("Schultz")
        val found = owners.size

        val owner = Owner().apply {
            firstName = "Sam"
            lastName = "Schultz"
            address = "4, Evans Street"
            city = "Wollongong"
            telephone = "4444444444"
        }
        ownerService.saveOwner(owner)
        Assertions.assertThat(owner.id).isNotNull()

        val ownersActual = ownerService.findOwnerByLastName("Schultz")
        Assertions.assertThat(ownersActual.size).isEqualTo(found + 1)
    }

    @Test
    @Transactional
    fun shouldUpdateOwner() {
        val owner = ownerService.findOwnerById(1)!!
        val oldLastName = owner.lastName
        val newLastName = "${oldLastName}X"

        owner.lastName = newLastName
        ownerService.saveOwner(owner)

        // retrieving new name from database
        val ownerActual = ownerService.findOwnerById(1)
        Assertions.assertThat(ownerActual?.lastName).isEqualTo(newLastName)
    }

    @Test
    fun shouldFindAllOwners() {
        val owners = ownerService.findAllOwners()
        assertThat(owners).extracting("id", "firstName").contains(
            tuple(1, "George"), tuple(3, "Eduardo")
        )
    }

    @Test
    @Transactional
    fun shouldDeleteOwner() {
        val owner = ownerService.findOwnerById(1)!!
        ownerService.deleteOwner(owner)

        val ownerActual = ownerService.findOwnerById(1)
        Assertions.assertThat(ownerActual).isNull()
    }
}
