package petclinic.api.pets

import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import petclinic.api.BaseController
import petclinic.api.owners.Owner
import petclinic.api.owners.OwnerRepository

@RestController
@CrossOrigin(exposedHeaders = ["errors, content-type"])
@RequestMapping("/api/pets")
class PetController(
    petRepository: PetRepository,
    private var ownerRepository: OwnerRepository
) : BaseController<Pet, PetRepository>("pets", petRepository) {

    override fun notFoundProvider(id: Int) = Pet.NotFoundException(id)

    override fun updateFn(a: Pet, b: Pet) {
        a.birthDate = b.birthDate
        a.name = b.name
        a.type = b.type
        a.owner = b.owner
    }

    @GetMapping("/getPetsByOwnerId/{ownerId}")
    fun getPetsByOwnerId(
        @PathVariable("ownerId") ownerId: Int
    ) =
        ownerRepository
            .findById(ownerId)
            .orElseGet { throw Owner.NotFoundException(ownerId) }
            .pets
}
