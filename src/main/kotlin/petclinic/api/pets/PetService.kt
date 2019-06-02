package petclinic.api.pets

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PetService(private val petRepository: PetRepository) {

    @Transactional(readOnly = true)
    fun findAllPets(): List<Pet> = petRepository.findAll().toList()

    @Transactional
    fun deletePet(pet: Pet) = petRepository.delete(pet)

    @Transactional(readOnly = true)
    fun findPetById(id: Int): Pet? = petRepository.findById(id).orElse(null)

    @Transactional
    fun savePet(pet: Pet) {
        petRepository.save(pet)
    }
}
