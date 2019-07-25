package petclinic.model

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.MappedSuperclass

@MappedSuperclass
abstract class BaseEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Int = 0
) {
    @get:JsonIgnore
    val isNew: Boolean
        get() = this.id == 0
}
