package petclinic.api.specialties

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SpecialtyService(private val specialtyRepository: SpecialtyRepository) {

    @Transactional(readOnly = true)
    fun findSpecialtyById(specialtyId: Int): Specialty? =
        specialtyRepository.findById(specialtyId).orElse(null)

    @Transactional(readOnly = true)
    fun findAllSpecialties() =
        specialtyRepository.findAll().toList()

    @Transactional
    fun saveSpecialty(specialty: Specialty) {
        specialtyRepository.save(specialty)
    }

    @Transactional
    fun deleteSpecialty(specialty: Specialty) {
        specialtyRepository.delete(specialty)
    }
}
