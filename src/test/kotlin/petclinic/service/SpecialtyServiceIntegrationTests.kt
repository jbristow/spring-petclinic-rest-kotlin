package petclinic.service

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.tuple
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import petclinic.api.specialties.Specialty
import petclinic.api.specialties.SpecialtyService
import javax.transaction.Transactional

/**
 *
 *  Integration test using the 'Spring Data' profile.
 *
 * @author Michael Isvy
 */

@ExtendWith(SpringExtension::class)
@SpringBootTest
class SpecialtyServiceIntegrationTests(
    @Autowired private val specialtyService: SpecialtyService
) {

    @Test
    fun shouldFindSpecialtyById() {
        val specialty = specialtyService.findSpecialtyById(1)
        assertThat(specialty?.name).isEqualTo("radiology")
    }

    @Test
    fun shouldFindAllSpecialtys() {
        val specialties = specialtyService.findAllSpecialties()
        assertThat(specialties).extracting("id", "name")
            .contains(tuple(1, "radiology"), tuple(3, "dentistry"))
    }

    @Test
    @Transactional
    fun shouldInsertSpecialty() {
        val specialties: Collection<Specialty> = specialtyService.findAllSpecialties()
        val found = specialties.size

        val specialty = Specialty()
        specialty.name = "dermatologist"

        specialtyService.saveSpecialty(specialty)
        assertThat(specialty.id).isNotEqualTo(null)

        val specialtiesActual = specialtyService.findAllSpecialties()
        assertThat(specialtiesActual.size).isEqualTo(found + 1)
    }

    @Test
    @Transactional
    fun shouldUpdateSpecialty() {
        val specialty = specialtyService.findSpecialtyById(1)!!
        val oldLastName = specialty.name
        val newLastName = oldLastName + "X"
        specialty.name = newLastName
        specialtyService.saveSpecialty(specialty)
        val newSpecialty = specialtyService.findSpecialtyById(1)!!
        assertThat(newSpecialty.name).isEqualTo(newLastName)
    }

    @Test
    @Transactional
    fun shouldDeleteSpecialty() {

        val specialty = specialtyService.findSpecialtyById(1)!!
        specialtyService.deleteSpecialty(specialty)

        val postDelete = specialtyService.findSpecialtyById(1)
        assertThat(postDelete).isNull()
    }
}

