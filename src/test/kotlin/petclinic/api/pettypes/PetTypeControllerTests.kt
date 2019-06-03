/*
 * Copyright 2016-2017 the original author or authors.
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

package petclinic.api.pettypes

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * Test class for [PetTypeController]
 *
 * @author Vitaliy Fedoriv
 */
@WebMvcTest(controllers = [PetTypeController::class])
@AutoConfigureMockMvc
class PetTypeControllerTests {

    @MockBean
    private lateinit var petTypeService: PetTypeService

    @Autowired
    private lateinit var mockMvc: MockMvc

    private val cat = PetType().apply {
        id = 1
        name = "cat"
    }

    private val dog = PetType().apply {
        id = 2
        name = "dog"
    }

    private val snake = PetType().apply {
        id = 4
        name = "snake"
    }

    @Test
    fun testGetPetTypeSuccess() {
        given<PetType>(petTypeService.findPetTypeById(1)).willReturn(cat)
        mockMvc.perform(
            get("/api/pettypes/1")
                .accept(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType("application/json;charset=UTF-8"))
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("cat"))
    }

    @Test
    fun testGetPetTypeNotFound() {
        given<PetType>(petTypeService.findPetTypeById(-1)).willReturn(null)
        mockMvc.perform(
            get("/api/pettypes/-1")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun testGetAllPetTypesSuccess() {
        given(petTypeService.findAllPetTypes()).willReturn(listOf(dog, snake))
        mockMvc.perform(
            get("/api/pettypes/")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType("application/json;charset=UTF-8"))
            .andExpect(jsonPath("$.[0].id").value(2))
            .andExpect(jsonPath("$.[0].name").value("dog"))
            .andExpect(jsonPath("$.[1].id").value(4))
            .andExpect(jsonPath("$.[1].name").value("snake"))
    }

    @Test
    fun testGetAllPetTypesNotFound() {
        given(petTypeService.findAllPetTypes()).willReturn(emptyList())
        mockMvc.perform(
            get("/api/pettypes/")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(content().string("[]"))
    }

    @Test
    fun testCreatePetTypeSuccess() {
        val newPetType = PetType(cat)
        newPetType.id = 999
        val mapper = jacksonObjectMapper()
        val newPetTypeAsJSON = mapper.writeValueAsString(newPetType)
        mockMvc.perform(
            post("/api/pettypes/")
                .content(newPetTypeAsJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(status().isCreated)
    }

    @Test
    fun testCreatePetTypeError() {
        val newPetType = PetType()
        val mapper = jacksonObjectMapper()
        val newPetTypeAsJSON = mapper.writeValueAsString(newPetType)
        mockMvc.perform(
            post("/api/pettypes/")
                .content(newPetTypeAsJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun testUpdatePetTypeSuccess() {
        given<PetType>(petTypeService.findPetTypeById(2)).willReturn(dog)
        val newPetType = PetType(dog)
        newPetType.name = "dog I"
        val mapper = jacksonObjectMapper()
        val newPetTypeAsJSON = mapper.writeValueAsString(newPetType)
        mockMvc.perform(
            put("/api/pettypes/2")
                .content(newPetTypeAsJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(content().contentType("application/json;charset=UTF-8"))
            .andExpect(status().isNoContent)

        mockMvc.perform(
            get("/api/pettypes/2")
                .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType("application/json;charset=UTF-8"))
            .andExpect(jsonPath("$.id").value(2))
            .andExpect(jsonPath("$.name").value("dog I"))
    }

    @Test
    @Throws(Exception::class)
    fun testUpdatePetTypeError() {
        val newPetType = PetType(cat)
        newPetType.name = ""
        val mapper = jacksonObjectMapper()
        val newPetTypeAsJSON = mapper.writeValueAsString(newPetType)
        mockMvc.perform(
            put("/api/pettypes/1")
                .content(newPetTypeAsJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun testDeletePetTypeSuccess() {
        val newPetType = cat
        val mapper = jacksonObjectMapper()
        val newPetTypeAsJSON = mapper.writeValueAsString(newPetType)
        given<PetType>(petTypeService.findPetTypeById(1)).willReturn(cat)
        mockMvc.perform(
            delete("/api/pettypes/1")
                .content(newPetTypeAsJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(status().isNoContent)
    }

    @Test
    fun testDeletePetTypeError() {
        val newPetTypeAsJSON = jacksonObjectMapper().writeValueAsString(PetType())
        given<PetType>(petTypeService.findPetTypeById(-1)).willReturn(null)
        mockMvc.perform(
            delete("/api/pettypes/-1")
                .content(newPetTypeAsJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(status().isNotFound)
    }
}
