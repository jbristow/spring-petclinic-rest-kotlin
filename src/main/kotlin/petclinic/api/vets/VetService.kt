package petclinic.api.vets

interface VetService {
    fun deleteVet(vet: Vet)
    fun findAllVets(): List<Vet>
    fun findVetById(id: Int): Vet?
    fun findVets(): List<Vet>
    fun saveVet(vet: Vet)
}
