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
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import petclinic.model.Pet
import petclinic.model.Visit
import java.io.IOException
import java.text.SimpleDateFormat

/**
 * @author Vitaliy Fedoriv
 */

class JacksonCustomVisitDeserializer @JvmOverloads constructor(t: Class<Visit>? = null) : StdDeserializer<Visit>(t) {

    override fun deserialize(parser: JsonParser, context: DeserializationContext): Visit {
        val node = parser.codec.readTree<JsonNode>(parser)

        return Visit().apply {
            if (node.get("id") ?: 0 != 0) {
                id = node.get("id").asInt()
            }
            date = SimpleDateFormat("yyyy/MM/dd").parse(node.get("date").asText(null))
            description = node.get("description").asText(null)
            pet = jacksonObjectMapper().treeToValue(node.get("pet"), Pet::class.java)
        }
    }

}
