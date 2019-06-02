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
package petclinic.api.owner

import org.springframework.core.style.ToStringCreator
import petclinic.model.Person
import petclinic.api.pets.Pet
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.OneToMany
import javax.persistence.Table
import javax.validation.constraints.Digits
import javax.validation.constraints.NotEmpty

/**
 * Simple JavaBean domain object representing an owner.
 *
 * @author Ken Krebs
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @author Michael Isvy
 */
@Table(name = "owners")
// @JsonSerialize(using = JacksonCustomOwnerSerializer::class)
// @JsonDeserialize(using = JacksonCustomOwnerDeserializer::class)
@Entity
class Owner(
    id: Int? = null,
    firstName: String?,
    lastName: String?,
    @Column @get:NotEmpty var address: String?,
    @Column @get:NotEmpty var city: String?,
    @Column @get:NotEmpty @Digits(fraction = 0, integer = 10) var telephone: String?,
    pets: List<Pet> = emptyList()
) : Person(id, firstName, lastName) {

    constructor() : this(
        null,
        null,
        null,
        null,
        null,
        null,
        emptyList<Pet>()
    )

    @Column
    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "owner", fetch = FetchType.EAGER, targetEntity = Pet::class)
    var pets: List<Pet> = pets
        get() {
            return field.sortedBy { it.name ?: "" }
        }

    fun addPet(pet: Pet) {
        pets = pets + pet
        pet.owner = this
    }

    /**
     * Return the Pet with the given name, or null if none found for this owner.
     *
     * @param name to test
     * @return true if pets name is already in use
     */
    fun getPet(name: String, ignoreNew: Boolean = false): Pet? =
        pets.firstOrNull { (!ignoreNew || !it.isNew) && name.equals(it.name, ignoreCase = true) }

    override fun toString(): String {
        return ToStringCreator(this)

            .append("id", this.id)
            .append("new", this.isNew)
            .append("lastName", this.lastName)
            .append("firstName", this.firstName)
            .append("address", this.address)
            .append("city", this.city)
            .append("telephone", this.telephone)
            .toString()
    }
}
