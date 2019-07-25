package petclinic.api.visits

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.springframework.format.annotation.DateTimeFormat
import petclinic.api.RestNotFoundException
import petclinic.api.pets.Pet
import petclinic.model.BaseEntity
import java.util.Date
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table
import javax.persistence.Temporal
import javax.persistence.TemporalType
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

@Entity
@Table(name = "visits")
open class Visit(
    id: Int = 0,

    @Column(name = "visit_date")
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy/MM/dd")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd")
    var date: Date = Date(),

    @get:NotEmpty @Column(name = "description") var description: String? = null,

    @JsonIgnoreProperties("visits")
    @ManyToOne @JoinColumn(name = "pet_id") @get:NotNull var pet: Pet? = null
) : BaseEntity(id) {

    class NotFoundException(id: Int) : RestNotFoundException("Visit", id)

    override fun toString() = "Visit(id=$id, pet=$pet, description=$description, date=$date)"
}
