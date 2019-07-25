package petclinic.api.specialties

import org.springframework.context.annotation.Profile
import org.springframework.data.repository.CrudRepository

@Profile("spring-data-jpa")
interface SpecialtyRepository : CrudRepository<Specialty, Int>
