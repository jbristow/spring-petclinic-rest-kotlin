package petclinic.api.specialties

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
class SpecialtyRepositoryTests(
    @Autowired private val specialtyRepository: SpecialtyRepository
) {

    @Test
    fun shouldFindSpecialtyById() {
        val specialty = specialtyRepository.findById(1).get()
        assertThat(specialty.name).isEqualTo("radiology")
    }

    @Test
    fun shouldFindAllSpecialties() {
        val specialties = specialtyRepository.findAll()
        println(specialties)
        assertThat(specialties).extracting("id", "name")
            .contains(tuple(1, "radiology"), tuple(3, "dentistry"))
    }

    @Test
    @Transactional
    fun shouldInsertSpecialty() {
        val specialties = specialtyRepository.findAll()
        val found = specialties.count()

        val specialty = Specialty(name = "dermatologist")

        specialtyRepository.save(specialty)
        assertThat(specialty.id).isNotEqualTo(null)

        val specialtiesActual = specialtyRepository.findAll()
        assertThat(specialtiesActual).hasSize(found + 1)
    }

    @Test
    @Transactional
    fun shouldUpdateSpecialty() {
        val specialty = specialtyRepository.findById(1).get()
        val newLastName = "${specialty.name}X"
        specialty.name = newLastName
        specialtyRepository.save(specialty)
        val newSpecialty = specialtyRepository.findById(1).get()
        assertThat(newSpecialty.name).isEqualTo(newLastName)
    }

    @Test
    @Transactional
    fun shouldDeleteSpecialty() {

        val specialty = specialtyRepository.findById(1).get()
        specialtyRepository.delete(specialty)

        val postDelete = specialtyRepository.findById(1)
        assertThat(postDelete).isEmpty
    }
}
