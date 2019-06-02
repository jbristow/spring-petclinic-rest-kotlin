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

package petclinic.rest

import com.fasterxml.jackson.databind.ObjectMapper
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
import petclinic.api.owner.Owner
import petclinic.api.owner.OwnerService
import petclinic.api.pets.Pet
import petclinic.api.pets.PetController
import petclinic.api.pets.PetService
import petclinic.api.pettypes.PetType
import petclinic.api.pettypes.PetTypeService
import java.util.Date

/**
 * Test class for [PetController]
 *
 * @author Vitaliy Fedoriv
 */

@WebMvcTest(controllers = [PetController::class])
@AutoConfigureMockMvc
open class PetControllerTests {

    @MockBean
    lateinit var petService: PetService

    @MockBean
    lateinit var petTypeService: PetTypeService

    @MockBean
    lateinit var ownerService: OwnerService

    @Autowired
    private lateinit var mockMvc: MockMvc

    private val petOwner1 = Owner(
        id = 1,
        firstName = "Eduardo",
        lastName = "Rodriquez",
        address = "2693 Commerce St.",
        city = "McFarland",
        telephone = "6085558763"
    )

    private val petTypeDog = PetType(
        id = 2,
        name = "dog"
    )
    private val pet3 = Pet(
        id = 3,
        name = "Rosy",
        birthDate = Date(),
        owner = petOwner1,
        type = petTypeDog
    )
    private val pet4 = Pet(
        id = 4,
        name = "Jewel",
        birthDate = Date(),
        owner = petOwner1,
        type = petTypeDog
    )

    @Test
    fun testGetPetSuccess() {
        given<Pet>(petService.findPetById(3)).willReturn(pet3)
        mockMvc.perform(
            get("/api/pets/3")
                .accept(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType("application/json;charset=UTF-8"))
            .andExpect(jsonPath("$.id").value(3))
            .andExpect(jsonPath("$.name").value("Rosy"))
    }

    @Test
    fun testGetPetNotFound() {
        given<Pet>(petService.findPetById(-1)).willReturn(null)
        mockMvc.perform(
            get("/api/pets/-1")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun testGetAllPetsSuccess() {
        given(petService.findAllPets()).willReturn(listOf(pet3, pet4))
        mockMvc.perform(
            get("/api/pets/")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType("application/json;charset=UTF-8"))
            .andExpect(jsonPath("$.[0].id").value(3))
            .andExpect(jsonPath("$.[0].name").value("Rosy"))
            .andExpect(jsonPath("$.[1].id").value(4))
            .andExpect(jsonPath("$.[1].name").value("Jewel"))
    }

    @Test
    fun testGetAllPetsNotFound() {
        given(petService.findAllPets()).willReturn(emptyList())
        mockMvc.perform(
            get("/api/pets/")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(content().string("[]"))
    }

    @Test
    fun testGetPetsByOwnerIdSuccess() {
        val owner = Owner(
            id = 3,
            pets = listOf(pet3, pet4)
        )
        given<Owner>(ownerService.findOwnerById(3)).willReturn(owner)
        mockMvc.perform(
            get("/api/pets/getPetsByOwnerId/3")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType("application/json;charset=UTF-8"))
            .andExpect(jsonPath("$.[0].id").value(4))
            .andExpect(jsonPath("$.[0].name").value("Jewel"))
            .andExpect(jsonPath("$.[1].id").value(3))
            .andExpect(jsonPath("$.[1].name").value("Rosy"))
    }

    @Test
    fun testGetPetsByOwnerIdOwnerNotFound() {
        given<Owner>(ownerService.findOwnerById(3)).willReturn(null)
        mockMvc.perform(
            get("/api/pets/getPetsByOwnerId/3")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun testGetPetsByOwnerIdPetsNotFound() {
        val owner = Owner().apply {
            id = 3
        }
        given<Owner>(ownerService.findOwnerById(3)).willReturn(owner)
        mockMvc.perform(
            get("/api/pets/getPetsByOwnerId/3")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(content().string("[]"))
    }

    @Test
    fun testCreatePetSuccess() {
        val newPet = Pet(pet3)
        newPet.id = 999
        val mapper = ObjectMapper()
        val newPetAsJSON = mapper.writeValueAsString(newPet)
        mockMvc.perform(
            post("/api/pets/")
                .content(newPetAsJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(status().isCreated)
    }

    @Test
    fun testCreatePetError() {
        val newPet = Pet()
        val newPetAsJSON = jacksonObjectMapper().writeValueAsString(newPet)
        mockMvc.perform(
            post("/api/pets/")
                .content(newPetAsJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun testUpdatePetSuccess() {
        given<Pet>(petService.findPetById(3)).willReturn(pet3)
        val newPet = Pet(pet3)
        newPet.name = "Rosy I"
        val mapper = ObjectMapper()
        val newPetAsJSON = mapper.writeValueAsString(newPet)
        mockMvc.perform(
            put("/api/pets/3")
                .content(newPetAsJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(content().contentType("application/json;charset=UTF-8"))
            .andExpect(status().isNoContent)

        mockMvc.perform(
            get("/api/pets/3")
                .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType("application/json;charset=UTF-8"))
            .andExpect(jsonPath("$.id").value(3))
            .andExpect(jsonPath("$.name").value("Rosy I"))
    }

    @Test
    fun testUpdatePetError() {
        val newPet = Pet(pet3)
        newPet.name = ""
        val mapper = ObjectMapper()
        val newPetAsJSON = mapper.writeValueAsString(newPet)
        mockMvc.perform(
            put("/api/pets/3")
                .content(newPetAsJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun testDeletePetSuccess() {
        val newPet = Pet(pet3)
        val mapper = ObjectMapper()
        val newPetAsJSON = mapper.writeValueAsString(newPet)
        given(petService.findPetById(3)).willReturn(pet3)
        mockMvc.perform(
            delete("/api/pets/3")
                .content(newPetAsJSON)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .characterEncoding("utf-8")
        )
            .andExpect(status().isNoContent)
    }

    @Test
    fun testDeletePetError() {
        val newPet = Pet(pet3)
        val mapper = ObjectMapper()
        val newPetAsJSON = mapper.writeValueAsString(newPet)
        given<Pet>(petService.findPetById(-1)).willReturn(null)
        mockMvc.perform(
            delete("/api/pets/-1")
                .content(newPetAsJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(status().isNotFound)
    }
}

