package petclinic.api.visits

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class VisitService(private val visitRepository: VisitRepository) {

    @Transactional(readOnly = true)
    fun findVisitById(visitId: Int): Visit? =
        visitRepository.findById(visitId).orElse(null)

    @Transactional(readOnly = true)
    fun findAllVisits(): List<Visit> =
        visitRepository.findAll().toList()

    @Transactional
    fun deleteVisit(visit: Visit) {
        visitRepository.delete(visit)
    }

    @Transactional
    fun saveVisit(visit: Visit) {
        visitRepository.save(visit)
    }

    @Transactional(readOnly = true)
    fun findVisitsByPetId(petId: Int) =
        visitRepository.findByPetId(petId)
}
