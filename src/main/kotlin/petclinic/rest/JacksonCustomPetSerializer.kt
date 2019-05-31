/*
 * Copyright 2016 the original author or authors.
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

package petclinic.rest

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import petclinic.model.Pet
import java.io.IOException
import java.text.SimpleDateFormat

/**
 * @author Vitaliy Fedoriv
 */

class JacksonCustomPetSerializer protected constructor(t: Class<Pet>?) : StdSerializer<Pet>(t) {

    constructor() : this(null) {}

    @Throws(IOException::class)
    override fun serialize(pet: Pet, jgen: JsonGenerator, provider: SerializerProvider) {
        val formatter = SimpleDateFormat("yyyy/MM/dd")
        jgen.writeStartObject() // pet
        if (pet.id == null) {
            jgen.writeNullField("id")
        } else {
            jgen.writeNumberField("id", pet.id!!)
        }
        jgen.writeStringField("name", pet.name)
        jgen.writeStringField("birthDate", pet.birthDate?.let { formatter.format(it) })

        pet.type?.let {
            jgen.writeObjectFieldStart("type")
            jgen.writeNumberField("id", it.id!!)
            jgen.writeStringField("name", it.name)
            jgen.writeEndObject() // type
        }

        pet.owner?.let {
            jgen.writeObjectFieldStart("owner")
            jgen.writeNumberField("id", it.id ?: -1)
            jgen.writeStringField("firstName", it.firstName)
            jgen.writeStringField("lastName", it.lastName)
            jgen.writeStringField("address", it.address)
            jgen.writeStringField("city", it.city)
            jgen.writeStringField("telephone", it.telephone)
            jgen.writeEndObject() // owner
        }
        // write visits array
        jgen.writeArrayFieldStart("visits")
        for (visit in pet.visits) {
            jgen.writeStartObject() // visit
            jgen.writeNumberField("id", visit.id!!)
            jgen.writeStringField("date", formatter.format(visit.date))
            jgen.writeStringField("description", visit.description)
            jgen.writeNumberField("pet", visit.pet?.id!!)
            jgen.writeEndObject() // visit
        }
        jgen.writeEndArray() // visits
        jgen.writeEndObject() // pet
    }

}
