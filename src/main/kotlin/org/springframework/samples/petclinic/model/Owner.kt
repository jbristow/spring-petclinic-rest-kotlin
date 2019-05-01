package org.springframework.samples.petclinic.model

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

import java.util.HashSet

import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.OneToMany
import javax.persistence.Table
import javax.validation.constraints.Digits

import org.hibernate.validator.constraints.NotEmpty
import org.springframework.core.style.ToStringCreator
import org.springframework.samples.petclinic.rest.JacksonCustomOwnerDeserializer
import org.springframework.samples.petclinic.rest.JacksonCustomOwnerSerializer

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize

/**
 * Simple JavaBean domain object representing an owner.
 *
 * @author Ken Krebs
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @author Michael Isvy
 */
@Entity
@Table(name = "owners")
@JsonSerialize(using = JacksonCustomOwnerSerializer::class)
@JsonDeserialize(using = JacksonCustomOwnerDeserializer::class)
class Owner : Person() {
    @Column(name = "address")
    @NotEmpty
    lateinit var address: String

    @Column(name = "city")
    @NotEmpty
    lateinit var city: String

    @Column(name = "telephone")
    @NotEmpty
    @Digits(fraction = 0, integer = 10)
    lateinit var telephone: String

    // This is funky due to integration with Hibernate
    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "owner", fetch = FetchType.EAGER)
    protected var petsInternal: MutableSet<Pet> = HashSet()
        @JsonIgnore
        get() {
            if (field == null) {
                field = HashSet()
            }
            return field
        }

    val pets: List<Pet>
        get() = petsInternal.sortedBy { it.name.toLowerCase() }

    fun addPet(pet: Pet) {
        petsInternal.add(pet)
        pet.owner = this
    }

    /**
     * Return the Pet with the given name, or null if none found for this Owner.
     *
     * @param name to test
     * @return true if pet name is already in use
     */
    @JvmOverloads
    fun getPet(name: String, ignoreNew: Boolean = false): Pet? {
        return petsInternal.find {
            if (!ignoreNew || !it.isNew) {
                return@find it.name.toLowerCase() == name.toLowerCase()
            }
            false
        }
    }

    override fun toString(): String {
        return ToStringCreator(this)

                .append("id", this.id)
                .append("new", this.isNew)
                .append("lastName", this.getLastName())
                .append("firstName", this.getFirstName())
                .append("address", this.address)
                .append("city", this.city)
                .append("telephone", this.telephone)
                .toString()
    }
}
