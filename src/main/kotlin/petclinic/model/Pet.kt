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
package petclinic.model

import org.springframework.format.annotation.DateTimeFormat
import java.util.Date
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.persistence.Table
import javax.persistence.Temporal
import javax.persistence.TemporalType

/**
 * Simple business object representing a pet.
 *
 * @author Ken Krebs
 * @author Juergen Hoeller
 * @author Sam Brannen
 */
@Entity
@Table(name = "pets")
// @JsonSerialize(using = JacksonCustomPetSerializer::class)
// @JsonDeserialize(using = JacksonCustomPetDeserializer::class)
open class Pet : NamedEntity() {

    @Column(name = "birth_date")
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy/MM/dd")
    open var birthDate: Date? = null

    @ManyToOne
    @JoinColumn(name = "type_id")
    open var type: PetType? = null

    @ManyToOne
    @JoinColumn(name = "owner_id")
    open var owner: Owner? = null

    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "pet", fetch = FetchType.EAGER, targetEntity = Visit::class)
    open var visits = emptySet<Visit>()
        get() {
            return field.toSortedSet(kotlin.Comparator { o1, o2 -> o1.date.compareTo(o2.date) })
        }

    fun addVisit(visit: Visit) {
        visits = visits + visit
        visit.pet = this
    }

    companion object {
        fun from(other: Pet): Pet {
            return Pet().apply {
                name = other.name
                id = other.id
                birthDate = other.birthDate
                type = other.type
                owner = other.owner
                visits = other.visits
            }
        }
    }
}
