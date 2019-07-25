package petclinic.api.pettypes

import petclinic.api.RestNotFoundException
import petclinic.model.NamedEntity
import javax.persistence.Entity

@Entity(name = "types")
class PetType(id: Int = 0, name: String) : NamedEntity(id, name) {

    constructor(other: PetType) : this(other.id, other.name)

    class NotFoundException(id: Int) : RestNotFoundException("PetType", id)

    override fun toString() = "PetType(id=$id, name=$name)"
}
