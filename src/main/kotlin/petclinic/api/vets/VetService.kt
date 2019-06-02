package petclinic.api.vets

import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class VetService(private val vetRepository: VetRepository) {
    @Transactional(readOnly = true)
    fun findVetById(id: Int): Vet? =
        vetRepository.findById(id).orElse(null)

    @Transactional(readOnly = true)
    fun findAllVets() =
        vetRepository.findAll().toList()

    @Transactional
    fun saveVet(vet: Vet) {
        vetRepository.save(vet)
    }

    @Transactional
    fun deleteVet(vet: Vet) {
        vetRepository.delete(vet)
    }

    @Transactional(readOnly = true)
    @Cacheable(value = ["vets"])
    fun findVets() = vetRepository.findAll().toList()
}
