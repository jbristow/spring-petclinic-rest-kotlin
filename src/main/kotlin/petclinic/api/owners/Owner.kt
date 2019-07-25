/*
 * Copyright 2002-2013 the original author or authors.
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
package petclinic.api.owners

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import petclinic.api.RestNotFoundException
import petclinic.api.pets.Pet
import petclinic.model.Person
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.OneToMany
import javax.validation.constraints.Digits
import javax.validation.constraints.NotEmpty

@Entity(name = "owners")
class Owner(
    id: Int = 0,
    firstName: String = "",
    lastName: String = "",
    @Column @get:NotEmpty var address: String = "",
    @Column @get:NotEmpty var city: String = "",
    @Column @get:NotEmpty @Digits(fraction = 0, integer = 10) var telephone: String = "",
    pets: Set<Pet> = emptySet()
) : Person(id, firstName, lastName) {

    private class PetComparator : Comparator<Pet> {
        override fun compare(o1: Pet, o2: Pet) = o1.name.compareTo(o2.name)
    }

    @Column
    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "owner", fetch = FetchType.EAGER, targetEntity = Pet::class)
    @JsonIgnoreProperties("owners")
    var pets: Set<Pet> = pets.toSortedSet(PetComparator())
        set(value) {
            field = value.toSortedSet(PetComparator())
        }

    fun addPet(pet: Pet) {
        pets = pets + pet
        pet.owner = this
    }

    override fun toString() =
        "Owner(id=$id, firstName=$firstName, lastName=$lastName, address=$address, city=$city, telephone=$telephone, pets=$pets)"

    class NotFoundException(ownerId: Int) : RestNotFoundException("Owner", ownerId)
}
