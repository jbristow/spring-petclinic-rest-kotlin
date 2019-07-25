package petclinic.api.vets

import org.springframework.context.annotation.Profile
import org.springframework.data.repository.CrudRepository

@Profile("spring-data-jpa")
interface VetRepository : CrudRepository<Vet, Int>
