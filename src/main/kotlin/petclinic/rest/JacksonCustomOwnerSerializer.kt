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
import petclinic.model.Owner
import java.io.IOException
import java.text.SimpleDateFormat

/**
 * @author Vitaliy Fedoriv
 */

class JacksonCustomOwnerSerializer @JvmOverloads constructor(t: Class<Owner>? = null) : StdSerializer<Owner>(t) {

    @Throws(IOException::class)
    override fun serialize(owner: Owner, jgen: JsonGenerator, provider: SerializerProvider) {
        val formatter = SimpleDateFormat("yyyy/MM/dd")
        jgen.writeStartObject()
        if (owner.id == null) {
            jgen.writeNullField("id")
        } else {
            jgen.writeNumberField("id", owner.id!!)
        }

        jgen.writeStringField("firstName", owner.firstName)
        jgen.writeStringField("lastName", owner.lastName)
        jgen.writeStringField("address", owner.address)
        jgen.writeStringField("city", owner.city)
        jgen.writeStringField("telephone", owner.telephone)
        // write pets array
        jgen.writeArrayFieldStart("pets")
        for (pet in owner.pets) {
            jgen.writeStartObject() // pet
            if (pet.id == null) {
                jgen.writeNullField("id")
            } else {
                jgen.writeNumberField("id", pet.id!!)
            }
            jgen.writeStringField("name", pet.name)
            jgen.writeStringField("birthDate", formatter.format(pet.birthDate))

            pet.type?.let {
                jgen.writeObjectFieldStart("type")
                jgen.writeNumberField("id", it.id!!)
                jgen.writeStringField("name", it.name)
                jgen.writeEndObject() // type
            }

            jgen.writeNumberField("owner", pet.owner?.id!!)
            // write visits array
            jgen.writeArrayFieldStart("visits")
            for (visit in pet.visits) {
                jgen.writeStartObject() // visit
                if (visit.id == null) {
                    jgen.writeNullField("id")
                } else {
                    jgen.writeNumberField("id", visit.id!!)
                }
                jgen.writeStringField("date", formatter.format(visit.date))
                jgen.writeStringField("description", visit.description)
                jgen.writeNumberField("pet", visit.pet?.id!!)
                jgen.writeEndObject() // visit
            }
            jgen.writeEndArray() // visits
            jgen.writeEndObject() // pet
        }
        jgen.writeEndArray() // pets
        jgen.writeEndObject() // owner
    }

}
