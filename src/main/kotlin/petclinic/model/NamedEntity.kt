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
open class NamedEntity : BaseEntity() {

    @Column
    @NotEmpty
    var name: String? = null

    override fun toString(): String {
        return name ?: ""
    }
}
