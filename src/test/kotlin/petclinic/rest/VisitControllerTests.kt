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
import petclinic.api.pets.Pet
import petclinic.api.pettypes.PetType
import petclinic.api.visits.Visit
import petclinic.api.visits.VisitController
import petclinic.api.visits.VisitService
import java.util.Date

/**
 * Test class for [VisitController]
 *
 * @author Vitaliy Fedoriv
 */
@WebMvcTest(controllers = [VisitController::class])
@AutoConfigureMockMvc
class VisitControllerTests {

    @MockBean
    private lateinit var visitService: VisitService

    @Autowired
    private lateinit var mockMvc: MockMvc

    private val owner1 = Owner().apply {
        id = 1
        firstName = "Eduardo"
        lastName = "Rodriquez"
        address = "2693 Commerce St."
        city = "McFarland"
        telephone = "6085558763"
    }

    private val dog = PetType().apply {
        id = 2
        name = "dog"
    }

    private val pet8 = Pet().apply {
        id = 8
        name = "Rosy"
        birthDate = Date()
        owner = owner1
        type = dog
    }

    private val visit2 = Visit().apply {
        id = 2
        pet = pet8
        date = Date()
        description = "rabies shot"
    }

    private val visit3 = Visit().apply {
        id = 3
        pet = pet8
        date = Date()
        description = "neutered"
    }

    @Test
    @Throws(Exception::class)
    fun testGetVisitSuccess() {
        given<Visit>(visitService.findVisitById(2)).willReturn(visit2)
        mockMvc.perform(
            get("/api/visits/2")
                .accept(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType("application/json;charset=UTF-8"))
            .andExpect(jsonPath("$.id").value(2))
            .andExpect(jsonPath("$.description").value("rabies shot"))
    }

    @Test
    @Throws(Exception::class)
    fun testGetVisitNotFound() {
        given<Visit>(visitService.findVisitById(-1)).willReturn(null)
        mockMvc.perform(
            get("/api/visits/-1")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isNotFound)
    }

    @Test
    @Throws(Exception::class)
    fun testGetAllVisitsSuccess() {
        given(visitService.findAllVisits()).willReturn(listOf(visit2, visit3))
        mockMvc.perform(
            get("/api/visits/")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType("application/json;charset=UTF-8"))
            .andExpect(jsonPath("$.[0].id").value(2))
            .andExpect(jsonPath("$.[0].description").value("rabies shot"))
            .andExpect(jsonPath("$.[1].id").value(3))
            .andExpect(jsonPath("$.[1].description").value("neutered"))
    }

    @Test
    @Throws(Exception::class)
    fun testGetAllVisitsNotFound() {
        given(visitService.findAllVisits()).willReturn(emptyList())
        mockMvc.perform(
            get("/api/visits/")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isNotFound)
    }

    @Test
    @Throws(Exception::class)
    fun testCreateVisitSuccess() {
        val newVisit = Visit(visit2)
        newVisit.id = 999
        val mapper = jacksonObjectMapper()
        val newVisitAsJSON = mapper.writeValueAsString(newVisit)
        println("newVisitAsJSON $newVisitAsJSON")
        mockMvc.perform(
            post("/api/visits/")
                .content(newVisitAsJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(status().isCreated)
    }

    @Test
    fun testCreateVisitError() {
        val newVisit = Visit(visit2)
        newVisit.id = null
        newVisit.pet = null
        val newVisitAsJSON = jacksonObjectMapper().writeValueAsString(newVisit)
        mockMvc.perform(
            post("/api/visits/")
                .content(newVisitAsJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun testUpdateVisitSuccess() {
        given<Visit>(visitService.findVisitById(2)).willReturn(visit2)
        val newVisit = Visit(visit2)
        newVisit.description = "rabies shot test"
        val mapper = jacksonObjectMapper()
        val newVisitAsJSON = mapper.writeValueAsString(newVisit)
        mockMvc.perform(
            put("/api/visits/2")
                .content(newVisitAsJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(content().contentType("application/json;charset=UTF-8"))
            .andExpect(status().isNoContent)

        mockMvc.perform(
            get("/api/visits/2")
                .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType("application/json;charset=UTF-8"))
            .andExpect(jsonPath("$.id").value(2))
            .andExpect(jsonPath("$.description").value("rabies shot test"))
    }

    @Test
    fun testUpdateVisitError() {
        val newVisit = Visit(visit2)
        newVisit.pet = null
        val newVisitAsJSON = jacksonObjectMapper().writeValueAsString(newVisit)
        mockMvc.perform(
            put("/api/visits/2")
                .content(newVisitAsJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun testDeleteVisitSuccess() {
        val newVisit = Visit(visit2)
        val mapper = jacksonObjectMapper()
        val newVisitAsJSON = mapper.writeValueAsString(newVisit)
        given<Visit>(visitService.findVisitById(2)).willReturn(visit2)
        mockMvc.perform(
            delete("/api/visits/2")
                .content(newVisitAsJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(status().isNoContent)
    }

    @Test
    @Throws(Exception::class)
    fun testDeleteVisitError() {
        val newVisit = Visit(visit2)
        val mapper = jacksonObjectMapper()
        val newVisitAsJSON = mapper.writeValueAsString(newVisit)
        given<Visit>(visitService.findVisitById(-1)).willReturn(null)
        mockMvc.perform(
            delete("/api/visits/-1")
                .content(newVisitAsJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(status().isNotFound)
    }
}
