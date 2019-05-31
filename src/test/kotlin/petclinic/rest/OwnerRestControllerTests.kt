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
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import petclinic.model.Owner
import petclinic.service.ClinicService


/**
 * Test class for [OwnerRestController]
 *
 * @author Vitaliy Fedoriv
 */
@WebMvcTest
@AutoConfigureMockMvc
open class OwnerRestControllerTests {

    @MockBean
    lateinit var clinicService: ClinicService

    @Autowired
    lateinit var mockMvc: MockMvc


    private val owner1 = Owner().apply {
        id = 1
        firstName = "George"
        lastName = "Franklin"
        address = "110 W. Liberty St."
        city = "Madison"
        telephone = "6085551023"
    }
    private val owner2 = Owner().apply {
        id = 2
        firstName = "Betty"
        lastName = "Davis"
        address = "638 Cardinal Ave."
        city = "Sun Prairie"
        telephone = "6085551749"
    }
    private val owner3 = Owner().apply {
        id = 3
        firstName = "Eduardo"
        lastName = "Rodriquez"
        address = "2693 Commerce St."
        city = "McFarland"
        telephone = "6085558763"
    }

    private val owner4 = Owner().apply {
        id = 4
        firstName = "Harold"
        lastName = "Davis"
        address = "563 Friendly St."
        city = "Windsor"
        telephone = "6085553198"
    }

    @Test
    fun testGetOwnerSuccess() {
        given<Owner>(clinicService.findOwnerById(1)).willReturn(owner1)
        mockMvc.perform(get("/api/owners/1")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk)
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("George"))
    }

    @Test
    fun testGetOwnerNotFound() {
        given<Owner>(clinicService?.findOwnerById(-1)).willReturn(null)
        mockMvc.perform(get("/api/owners/-1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound)
    }

    @Test
    @Throws(Exception::class)
    fun testGetOwnersListSuccess() {
        given(clinicService?.findOwnerByLastName("Davis")).willReturn(listOf(owner2, owner4))
        mockMvc.perform(get("/api/owners/*/lastname/Davis")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.[0].id").value(2))
                .andExpect(jsonPath("$.[0].firstName").value("Betty"))
                .andExpect(jsonPath("$.[1].id").value(4))
                .andExpect(jsonPath("$.[1].firstName").value("Harold"))
    }

    @Test
    @Throws(Exception::class)
    fun testGetOwnersListNotFound() {
        given(clinicService.findOwnerByLastName("0")).willReturn(emptyList())
        mockMvc.perform(get("/api/owners/?lastName=0")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound)
    }

    @Test
    @Throws(Exception::class)
    fun testGetAllOwnersSuccess() {
        given(clinicService.findAllOwners()).willReturn(listOf(owner2, owner4))
        mockMvc.perform(get("/api/owners/")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.[0].id").value(2))
                .andExpect(jsonPath("$.[0].firstName").value("Betty"))
                .andExpect(jsonPath("$.[1].id").value(4))
                .andExpect(jsonPath("$.[1].firstName").value("Harold"))
    }

    @Test
    @Throws(Exception::class)
    fun testGetAllOwnersNotFound() {
        given(clinicService?.findAllOwners()).willReturn(emptyList())
        mockMvc.perform(get("/api/owners/")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound)
    }

    @Test
    @Throws(Exception::class)
    fun testCreateOwnerSuccess() {
        val newOwner = owner1
        newOwner.id = 999
        val mapper = ObjectMapper()
        val newOwnerAsJSON = mapper.writeValueAsString(newOwner)
        mockMvc.perform(post("/api/owners/")
                .content(newOwnerAsJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isCreated)
    }

    @Test
    @Throws(Exception::class)
    fun testCreateOwnerError() {
        mockMvc.perform(post("/api/owners/")
                .content("""{"id":0,"firstName":null}""").accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest)
    }

    @Test
    @Throws(Exception::class)
    fun testUpdateOwnerSuccess() {
        given<Owner>(clinicService?.findOwnerById(1)).willReturn(owner1)
        val newOwner = owner1
        newOwner.firstName = "George I"
        val mapper = ObjectMapper()
        val newOwnerAsJSON = mapper.writeValueAsString(newOwner)
        mockMvc.perform(put("/api/owners/1")
                .content(newOwnerAsJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(status().isNoContent)

        mockMvc.perform(get("/api/owners/1")
                .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk)
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("George I"))

    }

    @Test
    @Throws(Exception::class)
    fun testUpdateOwnerError() {
        val newOwner = owner1
        newOwner.firstName = ""
        val mapper = ObjectMapper()
        val newOwnerAsJSON = mapper.writeValueAsString(newOwner)
        mockMvc.perform(put("/api/owners/1")
                .content(newOwnerAsJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest)
    }

    @Test
    fun testDeleteOwnerSuccess() {
        val newOwner = owner1
        val mapper = ObjectMapper()
        val newOwnerAsJSON = mapper.writeValueAsString(newOwner)
        given<Owner>(clinicService?.findOwnerById(1)).willReturn(owner1)
        mockMvc.perform(delete("/api/owners/1")
                .content(newOwnerAsJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNoContent)
    }

    @Test
    fun testDeleteOwnerError() {
        val newOwner = owner1
        val mapper = ObjectMapper()
        val newOwnerAsJSON = mapper.writeValueAsString(newOwner)
        given<Owner>(clinicService?.findOwnerById(-1)).willReturn(null)
        mockMvc.perform(delete("/api/owners/-1")
                .content(newOwnerAsJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound)
    }

}
