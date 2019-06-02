package petclinic.api.vets

import org.springframework.cache.annotation.Cacheable
import org.springframework.dao.DataAccessException
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.orm.ObjectRetrievalFailureException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class VetServiceImpl(private val vetRepository: VetRepository) : VetService {
    @Transactional(readOnly = true)
    @Throws(DataAccessException::class)
    override fun findVetById(id: Int): Vet? {
        try {
            return vetRepository.findById(id).orElse(null)
        } catch (e: ObjectRetrievalFailureException) {
            // just ignore not found exceptions for Jdbc/Jpa realization
            return null
        } catch (e: EmptyResultDataAccessException) {
            return null
        }
    }

    @Transactional(readOnly = true)
    @Throws(DataAccessException::class)
    override fun findAllVets(): List<Vet> {
        return vetRepository.findAll().toList()
    }

    @Transactional
    @Throws(DataAccessException::class)
    override fun saveVet(vet: Vet) {
        vetRepository.save(vet)
    }

    @Transactional
    @Throws(DataAccessException::class)
    override fun deleteVet(vet: Vet) {
        vetRepository.delete(vet)
    }


    @Transactional(readOnly = true)
    @Cacheable(value = ["vets"])
    @Throws(DataAccessException::class)
    override fun findVets(): List<Vet> {
        return vetRepository.findAll().toList()
    }
}
