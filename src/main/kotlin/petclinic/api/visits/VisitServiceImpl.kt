package petclinic.api.visits

import org.springframework.dao.DataAccessException
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.orm.ObjectRetrievalFailureException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class VisitServiceImpl(private val visitRepository: VisitRepository) :
    VisitService {

    @Transactional(readOnly = true)
    @Throws(DataAccessException::class)
    override fun findVisitById(visitId: Int): Visit? {
        return try {
            visitRepository.findById(visitId).orElse(null)
        } catch (e: ObjectRetrievalFailureException) {
            // just ignore not found exceptions for Jdbc/Jpa realization
            null
        } catch (e: EmptyResultDataAccessException) {
            null
        }
    }

    @Transactional(readOnly = true)
    @Throws(DataAccessException::class)
    override fun findAllVisits(): List<Visit> {
        return visitRepository.findAll().toList()
    }

    @Transactional
    @Throws(DataAccessException::class)
    override fun deleteVisit(visit: Visit) {
        visitRepository.delete(visit)
    }

    @Transactional
    @Throws(DataAccessException::class)
    override fun saveVisit(visit: Visit) {
        visitRepository.save(visit)
    }

    @Transactional(readOnly = true)
    override fun findVisitsByPetId(petId: Int): List<Visit> {
        return visitRepository.findByPetId(petId)
    }
}
