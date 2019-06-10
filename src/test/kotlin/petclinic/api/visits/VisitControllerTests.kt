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

package petclinic.api.visits

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
import petclinic.api.pets.PetControllerTests.Companion.pet8
import java.util.Date
import java.util.Optional

@WebMvcTest(controllers = [VisitController::class])
@AutoConfigureMockMvc
class VisitControllerTests {

    @MockBean
    private lateinit var visitRepository: VisitRepository

    @Autowired
    private lateinit var mockMvc: MockMvc

    companion object {
        val visit2: Visit
            get() = Visit(id = 2, pet = pet8, date = Date(), description = "rabies shot")

        val visit3: Visit
            get() = Visit(id = 3, pet = pet8, date = Date(), description = "neutered")

        val newVisit: Visit
            get() = Visit(id = 999, pet = pet8, date = Date(), description = "rabies shot")

        val visit2UpdatedDescription: Visit
            get() = Visit(id = 2, pet = pet8, date = Date(), description = "rabies shot test")

        val visit2NullPet: Visit
            get() = Visit(id = 2, date = Date(), description = "rabies shot")

        val visit2Invalid: Visit
            get() = Visit(date = Date(), description = "rabies shot")
    }

    @Test
    fun testGetVisitSuccess() {
        given(visitRepository.findById(2)).willReturn(Optional.of(visit2))
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
    fun testGetVisitNotFound() {
        given(visitRepository.findById(-1)).willReturn(Optional.empty())
        mockMvc.perform(
            get("/api/visits/-1")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun testGetAllVisitsSuccess() {
        given(visitRepository.findAll()).willReturn(listOf(visit2, visit3))
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
    fun testGetAllVisitsNotFound() {
        given(visitRepository.findAll()).willReturn(emptyList())
        mockMvc.perform(
            get("/api/visits/")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(content().string("[]"))
    }

    @Test
    fun testCreateVisitSuccess() {
        given(visitRepository.save(any<Visit>())).willReturn(newVisit)
        mockMvc.perform(
            post("/api/visits/")
                .content(jacksonObjectMapper().writeValueAsString(newVisit))
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(status().isCreated)
    }

    @Test
    fun testCreateVisitError() {
        mockMvc.perform(
            post("/api/visits/")
                .content(
                    jacksonObjectMapper().writeValueAsString(visit2Invalid)
                )
                .accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun testUpdateVisitSuccess() {
        given(visitRepository.findById(2)).willReturn(Optional.of(visit2))
        mockMvc.perform(
            put("/api/visits/2")
                .content(
                    jacksonObjectMapper().writeValueAsString(visit2UpdatedDescription)
                )
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
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
        mockMvc.perform(
            put("/api/visits/2")
                .content(jacksonObjectMapper().writeValueAsString(visit2NullPet))
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun testDeleteVisitSuccess() {
        given(visitRepository.findById(2)).willReturn(Optional.of(visit2))
        mockMvc.perform(
            delete("/api/visits/2")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(status().isNoContent)
    }

    @Test
    @Throws(Exception::class)
    fun testDeleteVisitError() {
        given(visitRepository.findById(-1)).willReturn(Optional.empty())
        mockMvc.perform(delete("/api/visits/-1").accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isNotFound)
    }
}
