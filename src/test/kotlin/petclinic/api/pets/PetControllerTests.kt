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

package petclinic.api.pets

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.any
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
import petclinic.api.owners.Owner
import petclinic.api.owners.OwnerRepository
import petclinic.api.pettypes.PetType
import java.util.Date
import java.util.Optional

@WebMvcTest(controllers = [PetController::class])
@AutoConfigureMockMvc
open class PetControllerTests {

    @MockBean
    lateinit var petRepository: PetRepository

    @MockBean
    lateinit var ownerRepository: OwnerRepository

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
        given(petRepository.findById(3)).willReturn(Optional.of(pet3))
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
        given(petRepository.findById(-1)).willReturn(Optional.empty())
        mockMvc.perform(
            get("/api/pets/-1")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun testGetAllPetsSuccess() {
        given(petRepository.findAll()).willReturn(listOf(pet3, pet4))
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
        given(petRepository.findAll()).willReturn(emptyList())
        mockMvc.perform(
            get("/api/pets/")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(content().string("[]"))
    }

    @Test
    fun testGetPetsByOwnerIdSuccess() {
        given(ownerRepository.findById(3)).willReturn(
            Optional.of(Owner(id = 3, pets = setOf(pet3, pet4)))
        )
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
        given(ownerRepository.findById(3)).willReturn(Optional.empty())
        mockMvc.perform(
            get("/api/pets/getPetsByOwnerId/3")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun testGetPetsByOwnerIdPetsNotFound() {
        given(ownerRepository.findById(3)).willReturn(
            Optional.of(Owner().apply { id = 3 })
        )
        mockMvc.perform(
            get("/api/pets/getPetsByOwnerId/3")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(content().string("[]"))
    }

    @Test
    fun testCreatePetSuccess() {
        val newPet = Pet(pet3).apply { id = 999 }
        given(petRepository.save(any<Pet>())).willReturn(newPet)
        mockMvc.perform(
            post("/api/pets/")
                .content(jacksonObjectMapper().writeValueAsString(newPet))
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(status().isCreated)
    }

    @Test
    fun testCreatePetError() {
        val newPet = Pet()
        mockMvc.perform(
            post("/api/pets/")
                .content(jacksonObjectMapper().writeValueAsString(newPet))
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun testUpdatePetSuccess() {
        given(petRepository.findById(3)).willReturn(Optional.of(pet3))
        mockMvc.perform(
            put("/api/pets/3")
                .content(jacksonObjectMapper().writeValueAsString(Pet(pet3).apply { name = "Rosy I" }))
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(content().contentType("application/json;charset=UTF-8"))
            .andExpect(status().isNoContent)

        mockMvc.perform(
            get("/api/pets/3")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType("application/json;charset=UTF-8"))
            .andExpect(jsonPath("$.id").value(3))
            .andExpect(jsonPath("$.name").value("Rosy I"))
    }

    @Test
    fun testUpdatePetError() {

        mockMvc.perform(
            put("/api/pets/3")
                .content(jacksonObjectMapper().writeValueAsString(Pet(pet3).apply { name = "" }))
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun testDeletePetSuccess() {
        given(petRepository.findById(3)).willReturn(Optional.of(pet3))

        mockMvc.perform(
            delete("/api/pets/3")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .characterEncoding("utf-8")
        )
            .andExpect(status().isNoContent)
    }

    @Test
    fun testDeletePetError() {
        given(petRepository.findById(-1)).willReturn(Optional.empty())
        mockMvc.perform(
            delete("/api/pets/-1")
                .content(jacksonObjectMapper().writeValueAsString(pet3))
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(status().isNotFound)
    }
}
