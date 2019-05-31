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

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import petclinic.model.Owner
import petclinic.model.Pet
import petclinic.model.PetType
import java.io.IOException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author Vitaliy Fedoriv
 */

class JacksonCustomPetDeserializer @JvmOverloads constructor(t: Class<Pet>? = null) : StdDeserializer<Pet>(t) {

    @Throws(IOException::class, JsonProcessingException::class)
    override fun deserialize(parser: JsonParser, context: DeserializationContext): Pet {
        val formatter = SimpleDateFormat("yyyy/MM/dd")
        val pet = Pet()
        val owner: Owner
        val petType: PetType
        val mapper = ObjectMapper()
        var birthDate: Date?
        val node = parser.codec.readTree<JsonNode>(parser)
        val owner_node = node.get("owner")
        val type_node = node.get("type")
        owner = mapper.treeToValue(owner_node, Owner::class.java)
        petType = mapper.treeToValue(type_node, PetType::class.java)
        val petId = node.get("id").asInt()
        val name = node.get("name").asText(null)
        val birthDateStr = node.get("birthDate").asText(null)
        try {
            birthDate = formatter.parse(birthDateStr)
        } catch (e: ParseException) {
            e.printStackTrace()
            throw IOException(e)
        }

        if (petId != 0) {
            pet.id = petId
        }
        pet.name = name
        pet.birthDate = birthDate
        pet.owner = owner
        pet.type = petType
        return pet
    }

}
