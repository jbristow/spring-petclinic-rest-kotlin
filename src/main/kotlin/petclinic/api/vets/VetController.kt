package petclinic.api.vets

import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import petclinic.api.BaseController

@RestController
@CrossOrigin(exposedHeaders = ["errors, content-type"])
@RequestMapping("/api/vets")
class VetController(vetRepository: VetRepository) : BaseController<Vet, VetRepository>("vets", vetRepository) {
    override fun notFoundProvider(id: Int) = Vet.NotFoundException(id)

    override fun updateFn(a: Vet, b: Vet) {
        a.firstName = b.firstName
        a.lastName = b.lastName
        a.specialties = b.specialties
    }
}
