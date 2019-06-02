package petclinic.service

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import petclinic.api.owners.OwnerService
import petclinic.api.pets.PetService
import petclinic.api.pettypes.PetTypeService
import petclinic.api.vets.Vet
import petclinic.api.vets.VetService
import javax.transaction.Transactional

@ExtendWith(SpringExtension::class)
@SpringBootTest
class VetServiceIntegrationTests(
    @Autowired private val ownerService: OwnerService,
    @Autowired private val petService: PetService,
    @Autowired private val petTypeService: PetTypeService,
    @Autowired private val vetService: VetService
) {

    @Test
    fun shouldFindVets() {
        val vets = vetService.findVets()

        val vet = vets.find { it.id == 3 }!!
        Assertions.assertThat(vet.lastName).isEqualTo("Douglas")
        Assertions.assertThat(vet.nrOfSpecialties).isEqualTo(2)
        Assertions.assertThat(vet.specialties.elementAt(0).name).isEqualTo("dentistry")
        Assertions.assertThat(vet.specialties.elementAt(1).name).isEqualTo("surgery")
    }

    @Test
    fun shouldFindVetDyId() {
        val vet = vetService.findVetById(1)
        Assertions.assertThat(vet?.firstName).isEqualTo("James")
        Assertions.assertThat(vet?.lastName).isEqualTo("Carter")
    }

    @Test
    @Transactional
    fun shouldInsertVet() {
        val vets = vetService.findAllVets()
        val found = vets.size

        val vet = Vet()
        vet.firstName = "John"
        vet.lastName = "Dow"

        vetService.saveVet(vet)
        Assertions.assertThat(vet.id).isNotEqualTo(null)

        val vetsActual = vetService.findAllVets()
        Assertions.assertThat(vetsActual.size).isEqualTo(found + 1)
    }

    @Test
    @Transactional
    fun shouldUpdateVet() {
        val vet = vetService.findVetById(1)!!
        val oldLastName = vet.lastName
        val newLastName = oldLastName + "X"
        vet.lastName = newLastName
        vetService.saveVet(vet)

        val vetActual = vetService.findVetById(1)
        Assertions.assertThat(vetActual?.lastName).isEqualTo(newLastName)
    }

    @Test
    @Transactional
    fun shouldDeleteVet() {
        val vet = vetService.findVetById(1)!!
        vetService.deleteVet(vet)

        val vetActual = vetService.findVetById(1)
        Assertions.assertThat(vetActual).isNull()
    }
}
