package petclinic.api.vets

import com.fasterxml.jackson.annotation.JsonIgnore
import petclinic.api.RestNotFoundException
import petclinic.api.specialties.Specialty
import petclinic.model.Person
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.JoinTable
import javax.persistence.ManyToMany
import javax.persistence.Table

@Entity
@Table(name = "vets")
class Vet(
    id: Int = 0,
    firstName: String,
    lastName: String,
    specialties: Set<Specialty> = emptySet()
) : Person(id, firstName, lastName) {

    @ManyToMany(fetch = FetchType.EAGER, targetEntity = Specialty::class)
    @JoinTable(
        name = "vet_specialties",
        joinColumns = [JoinColumn(name = "vet_id")],
        inverseJoinColumns = [JoinColumn(name = "specialty_id")]
    )
    var specialties: Set<Specialty> = specialties
        get() {
            return field.sortedBy { it.name.toLowerCase() }.toMutableSet()
        }

    @get:JsonIgnore
    val nrOfSpecialties: Int
        get() = specialties.size

    override fun toString() = "Vet(id=$id, firstName=$firstName, lastName=$lastName, specialties=$specialties)"

    class NotFoundException(id: Int) : RestNotFoundException("Vet", id)
}
