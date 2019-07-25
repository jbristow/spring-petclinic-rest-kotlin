package petclinic.api.specialties

import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import petclinic.api.BaseController

@RestController
@CrossOrigin(exposedHeaders = ["errors, content-type"])
@RequestMapping("/api/specialties")
class SpecialtyController(specialtyRepository: SpecialtyRepository) :
    BaseController<Specialty, SpecialtyRepository>("specialties", specialtyRepository) {

    override fun notFoundProvider(id: Int) = Specialty.NotFoundException(id)

    override fun updateFn(a: Specialty, b: Specialty) {
        a.name = b.name
    }
}
