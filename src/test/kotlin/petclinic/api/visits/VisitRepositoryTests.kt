package petclinic.api.visits

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.tuple
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import petclinic.api.pets.PetRepository
import java.util.Date
import javax.transaction.Transactional

@ExtendWith(SpringExtension::class)
@SpringBootTest
class VisitRepositoryTests(
    @Autowired private val visitService: VisitRepository,
    @Autowired private val petService: PetRepository

) {

    @Test
    @Transactional
    fun shouldAddNewVisitForPet() {
        val pet7 = petService.findById(7).get()
        val found = pet7.visits.size

        val visit = Visit(description = "test")
        pet7.addVisit(visit)

        visitService.save(visit)
        petService.save(pet7)

        val pet7Actual = petService.findById(7).get()
        assertThat(pet7Actual.visits).hasSize(found + 1)

        assertThat(visit.id).isNotNull()
    }

    @Test
    fun shouldFindVisitsByPetId() {
        val visits = visitService.findByPetId(7)
        assertThat(visits).hasSize(2)
        visits[0].run {
            assertThat(pet).isNotNull
            assertThat(date).isNotNull()
            assertThat(pet?.id).isEqualTo(7)
        }
    }

    @Test
    fun shouldFindVisitDyId() {
        val visit = visitService.findById(1).get()
        assertThat(visit.id).isEqualTo(1)
        assertThat(visit.pet?.name).isEqualTo("Samantha")
    }

    @Test
    fun shouldFindAllVisits() {
        val visits = visitService.findAll()
        assertThat(visits).extracting("id", "pet.name")
            .contains(tuple(1, "Samantha"), tuple(3, "Max"))
    }

    @Test
    @Transactional
    fun shouldInsertVisit() {
        val visits = visitService.findAll()
        val found = visits.count()

        val pet = petService.findById(1).get()

        val visit = Visit(pet = pet, date = Date(), description = "new visits")

        visitService.save(visit)
        assertThat(visit.id).isNotNull()

        val visitsActual = visitService.findAll()
        assertThat(visitsActual).hasSize(found + 1)
    }

    @Test
    @Transactional
    fun shouldUpdateVisit() {
        val visit = visitService.findById(1).get()
        val oldDesc = visit.description
        val newDesc = "${oldDesc}X"
        visit.description = newDesc
        visitService.save(visit)
        val visitActual = visitService.findById(1).get()
        assertThat(visitActual.description).isEqualTo(newDesc)
    }

    @Test
    @Transactional
    fun shouldDeleteVisit() {
        val visit = visitService.findById(1).get()
        visitService.delete(visit)
        val visitActual = visitService.findById(1)

        assertThat(visitActual).isEmpty
    }
}
