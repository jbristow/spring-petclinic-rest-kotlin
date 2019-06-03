package petclinic.api.visits

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.tuple
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import petclinic.api.pets.PetService
import java.util.Date
import javax.transaction.Transactional

@ExtendWith(SpringExtension::class)
@SpringBootTest
class VisitServiceIntegrationTests(
    @Autowired private val visitService: VisitService,
    @Autowired private val petService: PetService

) {

    @Test
    @Transactional
    fun shouldAddNewVisitForPet() {
        val pet7 = petService.findPetById(7)!!
        val found = pet7.visits.size
        val visit = Visit()
        pet7.addVisit(visit)
        visit.description = "test"
        visitService.saveVisit(visit)
        petService.savePet(pet7)

        val pet7Actual = petService.findPetById(7)
        Assertions.assertThat(pet7Actual?.visits?.size).isEqualTo(found + 1)
        Assertions.assertThat(visit.id).isNotNull()
    }

    @Test
    fun shouldFindVisitsByPetId() {
        val visits = visitService.findVisitsByPetId(7)
        Assertions.assertThat(visits.size).isEqualTo(2)
        val visitArr = visits.toTypedArray()
        Assertions.assertThat(visitArr[0].pet).isNotNull
        Assertions.assertThat(visitArr[0].date).isNotNull()
        Assertions.assertThat(visitArr[0].pet?.id).isEqualTo(7)
    }

    @Test
    fun shouldFindVisitDyId() {
        val visit = visitService.findVisitById(1)
        Assertions.assertThat(visit?.id).isEqualTo(1)
        Assertions.assertThat(visit?.pet?.name).isEqualTo("Samantha")
    }

    @Test
    fun shouldFindAllVisits() {
        val visits = visitService.findAllVisits()
        assertThat(visits).extracting("id", "pet.name")
            .contains(tuple(1, "Samantha"), tuple(3, "Max"))
    }

    @Test
    @Transactional
    fun shouldInsertVisit() {
        val visits = visitService.findAllVisits()
        val found = visits.size

        val pet = petService.findPetById(1)!!

        val visit = Visit()
        visit.pet = pet
        visit.date = Date()
        visit.description = "new visits"


        visitService.saveVisit(visit)
        Assertions.assertThat(visit.id).isNotNull()

        val visitsActual = visitService.findAllVisits()
        Assertions.assertThat(visitsActual.size).isEqualTo(found + 1)
    }

    @Test
    @Transactional
    fun shouldUpdateVisit() {
        val visit = visitService.findVisitById(1)!!
        val oldDesc = visit.description
        val newDesc = "${oldDesc}X"
        visit.description = newDesc
        visitService.saveVisit(visit)
        val visitActual = visitService.findVisitById(1)
        Assertions.assertThat(visitActual?.description).isEqualTo(newDesc)
    }

    @Test
    @Transactional
    fun shouldDeleteVisit() {
        val visit = visitService.findVisitById(1)!!
        visitService.deleteVisit(visit)
        val visitActual = visitService.findVisitById(1)

        Assertions.assertThat(visitActual).isNull()
    }
}
