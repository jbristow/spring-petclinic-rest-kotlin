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

/**
 * Simple JavaBean domain object representing a visits.
 *
 * @author Ken Krebs
 */
@Entity
@Table(name = "visits")
// @JsonSerialize(using = JacksonCustomVisitSerializer::class)
// @JsonDeserialize(using = JacksonCustomVisitDeserializer::class)
open class Visit(
    id: Int? = null,

    @Column(name = "visit_date")
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy/MM/dd")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd")
    var date: Date = Date(),

    @get:NotEmpty @Column(name = "description") var description: String? = null,

    @JsonIgnoreProperties("visits")
    @ManyToOne @JoinColumn(name = "pet_id") @get:NotNull var pet: Pet? = null
) : BaseEntity(id) {

    constructor(other: Visit) : this(other.id, other.date, other.description, other.pet)

    class NotFoundException(id: Int) : RestNotFoundException("Visit", id)
}
