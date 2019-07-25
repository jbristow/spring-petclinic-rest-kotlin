package petclinic.api.pettypes

import org.springframework.context.annotation.Profile
import org.springframework.data.repository.CrudRepository

@Profile("spring-data-jpa")
interface PetTypeRepository : CrudRepository<PetType, Int>
