package petclinic.api.visits

interface VisitService {
    fun deleteVisit(visit: Visit)
    fun findAllVisits(): List<Visit>
    fun findVisitById(visitId: Int): Visit?
    fun findVisitsByPetId(petId: Int): List<Visit>
    fun saveVisit(visit: Visit)
}
