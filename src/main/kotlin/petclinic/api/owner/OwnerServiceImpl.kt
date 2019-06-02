package petclinic.api.owner

import org.springframework.dao.DataAccessException
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.orm.ObjectRetrievalFailureException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OwnerServiceImpl(private val ownerRepository: OwnerRepository) : OwnerService {
    @Transactional(readOnly = true)
    @Throws(DataAccessException::class)
    override fun findAllOwners(): List<Owner> {
        return ownerRepository.findAll().toList()
    }

    @Transactional
    @Throws(DataAccessException::class)
    override fun deleteOwner(owner: Owner) {
        ownerRepository.delete(owner)
    }

    @Transactional(readOnly = true)
    override fun findOwnerById(id: Int): Owner? {
        return try {
            println("findOwnerId $id")
            ownerRepository.findById(id).orElse(null)
        } catch (e: ObjectRetrievalFailureException) {
            // just ignore not found exceptions for Jdbc/Jpa realization
            println("PORBLEM $e")
            null
        } catch (e: EmptyResultDataAccessException) {
            println("PORBLEM $e")
            null
        }
    }

    @Transactional
    @Throws(DataAccessException::class)
    override fun saveOwner(owner: Owner) {
        ownerRepository.save(owner)
    }


    @Transactional(readOnly = true)
    @Throws(DataAccessException::class)
    override fun findOwnerByLastName(lastName: String): List<Owner> {
        return ownerRepository.findByLastName(lastName).toList()
    }
}
