package petclinic.api.owners

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OwnerService(private val ownerRepository: OwnerRepository) {
    @Transactional(readOnly = true)
    fun findAllOwners() =
        ownerRepository.findAll().toList()

    @Transactional
    fun deleteOwner(owner: Owner) {
        ownerRepository.delete(owner)
    }

    @Transactional(readOnly = true)
    fun findOwnerById(id: Int): Owner? =
        ownerRepository.findById(id).orElse(null)

    @Transactional
    fun saveOwner(owner: Owner) {
        ownerRepository.save(owner)
    }

    @Transactional(readOnly = true)
    fun findOwnerByLastName(lastName: String) =
        ownerRepository.findByLastName(lastName).toList()
}
