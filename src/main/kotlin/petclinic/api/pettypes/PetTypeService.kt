package petclinic.api.pettypes

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PetTypeService(private val petTypeRepository: PetTypeRepository) {

    @Transactional(readOnly = true)
    fun findPetTypeById(petTypeId: Int): PetType? =
        petTypeRepository.findById(petTypeId).orElse(null)

    @Transactional(readOnly = true)
    fun findAllPetTypes() =
        petTypeRepository.findAll().toList()

    @Transactional
    fun savePetType(petType: PetType) {
        petTypeRepository.save(petType)
    }

    @Transactional
    fun deletePetType(petType: PetType) {
        petTypeRepository.delete(petType)
    }
}

