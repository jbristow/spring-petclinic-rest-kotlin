/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
    id: Int? = null,
    firstName: String? = null,
    lastName: String? = null,
    specialties: Set<Specialty> = emptySet()
) : Person(id, firstName, lastName) {

    constructor(other: Vet) : this(other.id, other.firstName, other.lastName, other.specialties)

    @ManyToMany(fetch = FetchType.EAGER, targetEntity = Specialty::class)
    @JoinTable(
        name = "vet_specialties",
        joinColumns = [JoinColumn(name = "vet_id")],
        inverseJoinColumns = [JoinColumn(name = "specialty_id")]
    )
    var specialties: Set<Specialty> = specialties
        get() {
            return field.sortedBy { it.name?.toLowerCase() }.toMutableSet()
        }

    @get:JsonIgnore
    val nrOfSpecialties: Int
        get() = specialties.size

    fun addSpecialty(specialty: Specialty) {
        specialties = specialties + specialty
    }

    fun clearSpecialties() {
        specialties = emptySet()
    }

    override fun toString() = "Vet(id=$id, firstName=$firstName, lastName=$lastName, specialties=$specialties)"

    class NotFoundException(id: Int) : RestNotFoundException("Vet", id)
}
