package petclinic.model

import javax.persistence.MappedSuperclass
import javax.validation.constraints.NotEmpty

@MappedSuperclass
abstract class NamedEntity protected constructor(id: Int = 0, @get:NotEmpty var name: String) :
    BaseEntity(id)
