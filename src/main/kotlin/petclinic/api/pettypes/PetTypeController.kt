package petclinic.api.pettypes

import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import petclinic.api.BaseController

@RestController
@CrossOrigin(exposedHeaders = ["errors, content-type"])
@RequestMapping("/api/pettypes")
class PetTypeController(petTypeRepository: PetTypeRepository) :
    BaseController<PetType, PetTypeRepository>("pettypes", petTypeRepository) {

    override fun notFoundProvider(id: Int) = PetType.NotFoundException(id)

    override fun updateFn(a: PetType, b: PetType) {
        a.name = b.name
    }
}
