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
import petclinic.model.Owner
import java.io.IOException

/**
 * @author Vitaliy Fedoriv
 */

class JacksonCustomOwnerDeserializer @JvmOverloads constructor(t: Class<Owner>? = null) : StdDeserializer<Owner>(t) {

    @Throws(IOException::class, JsonProcessingException::class)
    override fun deserialize(parser: JsonParser, context: DeserializationContext): Owner {
        val node = parser.codec.readTree<JsonNode>(parser)

        return Owner().apply {

            if (node.get("id").asInt() != 0) {
                id = node.get("id").asInt()
            }
            firstName = node.get("firstName").asText(null)
            lastName = node.get("lastName").asText(null)
            address = node.get("address").asText(null)
            city = node.get("city").asText(null)
            telephone = node.get("telephone").asText(null)
        }
    }

}
