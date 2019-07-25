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
package petclinic.api.pets

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.springframework.data.jpa.repository.Temporal
import org.springframework.format.annotation.DateTimeFormat
import petclinic.api.RestNotFoundException
import petclinic.api.owners.Owner
import petclinic.api.pettypes.PetType
import petclinic.api.visits.Visit
import petclinic.model.NamedEntity
import java.util.Date
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany

@Entity(name = "pets")
class Pet(
    id: Int = 0,
    name: String,
    @Column(name = "birth_date") @Temporal @DateTimeFormat(pattern = "yyyy/MM/dd") var birthDate: Date = Date(),
    @ManyToOne @JoinColumn(name = "type_id") var type: PetType,
    @JsonIgnoreProperties("pets")
    @ManyToOne @JoinColumn(name = "owner_id") var owner: Owner,
    visits: Set<Visit> = emptySet()
) : NamedEntity(id, name) {

    @JsonIgnoreProperties("pet")
    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "pet", fetch = FetchType.EAGER, targetEntity = Visit::class)
    var visits = visits
        get() {
            return field.toSortedSet(Comparator { o1, o2 -> o1.date.compareTo(o2.date) })
        }

    fun addVisit(visit: Visit) {
        visits = visits + visit
        visit.pet = this
    }

    override fun toString(): String {
        return "Pet(id=$id, name=$name, birthDate=$birthDate, type=$type, owner=$owner)"
    }

    class NotFoundException(id: Int) : RestNotFoundException("Pet", id)
}
