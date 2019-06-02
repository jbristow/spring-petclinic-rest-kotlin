package petclinic.api.owner

interface OwnerService {
    fun findOwnerById(id: Int): Owner?

    fun findAllOwners(): List<Owner>

    fun saveOwner(owner: Owner)

    fun deleteOwner(owner: Owner)

    fun findOwnerByLastName(lastName: String): List<Owner>
}
