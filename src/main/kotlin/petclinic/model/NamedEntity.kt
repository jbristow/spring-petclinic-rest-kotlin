package petclinic.model

import javax.persistence.Column
import javax.persistence.MappedSuperclass
import javax.validation.constraints.NotEmpty

/**
 * Simple JavaBean domain object adds a name property to `BaseEntity`. Used as a base class for objects
 * needing these properties.
 *
 * @author Ken Krebs
 * @author Juergen Hoeller
 */
@MappedSuperclass
open class NamedEntity(
    id: Int? = null,
    @get:NotEmpty @Column var name: String? = null
) : BaseEntity(id) {

    override fun toString(): String {
        return """PetType[id=$id,name="$name"]"""
    }
}
