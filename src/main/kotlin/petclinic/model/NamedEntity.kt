package petclinic.model

import javax.persistence.Column
import javax.persistence.MappedSuperclass
import javax.validation.constraints.NotEmpty

@MappedSuperclass
open class NamedEntity(
    id: Int? = null,
    @get:NotEmpty @Column var name: String? = null
) : BaseEntity(id) {

    override fun toString(): String {
        return """PetType[id=$id,name="$name"]"""
    }
}
