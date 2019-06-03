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

package petclinic.api.vets

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

@WebMvcTest(controllers = [VetController::class])
@AutoConfigureMockMvc
class VetControllerTests {

    @MockBean
    private lateinit var vetService: VetService

    @Autowired
    private lateinit var mockMvc: MockMvc

    private val vet1 = Vet().apply {
        id = 1
        firstName = "James"
        lastName = "Carter"
    }

    private val vet2 = Vet().apply {
        id = 2
        firstName = "Helen"
        lastName = "Leary"
    }

    @Test
    @Throws(Exception::class)
    fun testGetVetSuccess() {
        given<Vet>(vetService.findVetById(1)).willReturn(Vet(vet1))
        mockMvc.perform(
            get("/api/vets/1")
                .accept(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType("application/json;charset=UTF-8"))
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.firstName").value("James"))
    }

    @Test
    @Throws(Exception::class)
    fun testGetVetNotFound() {
        given<Vet>(vetService.findVetById(-1)).willReturn(null)
        mockMvc.perform(
            get("/api/vets/-1")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isNotFound)
    }

    @Test
    @Throws(Exception::class)
    fun testGetAllVetsSuccess() {
        given(vetService.findAllVets()).willReturn(listOf(vet1, vet2))
        mockMvc.perform(
            get("/api/vets/")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType("application/json;charset=UTF-8"))
            .andExpect(jsonPath("$.[0].id").value(1))
            .andExpect(jsonPath("$.[0].firstName").value("James"))
            .andExpect(jsonPath("$.[1].id").value(2))
            .andExpect(jsonPath("$.[1].firstName").value("Helen"))
    }

    @Test
    @Throws(Exception::class)
    fun testGetAllVetsNotFound() {
        given(vetService.findAllVets()).willReturn(emptyList())
        mockMvc.perform(
            get("/api/vets/")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(content().string("[]"))
    }

    @Test
    fun testCreateVetSuccess() {
        val newVet = Vet(vet1)
        newVet.id = 999
        val mapper = jacksonObjectMapper()
        val newVetAsJSON = mapper.writeValueAsString(newVet)
        mockMvc.perform(
            post("/api/vets/")
                .content(newVetAsJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(status().isCreated)
    }

    @Test
    @Throws(Exception::class)
    fun testCreateVetError() {
        val newVet = Vet(vet1)
        newVet.id = null
        newVet.firstName = ""
        val mapper = jacksonObjectMapper()
        val newVetAsJSON = mapper.writeValueAsString(newVet)
        mockMvc.perform(
            post("/api/vets/")
                .content(newVetAsJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    @Throws(Exception::class)
    fun testUpdateVetSuccess() {
        given<Vet>(vetService.findVetById(1)).willReturn(Vet(vet1))
        val newVet = Vet(vet1)
        newVet.firstName = "James"
        val mapper = jacksonObjectMapper()
        val newVetAsJSON = mapper.writeValueAsString(newVet)
        mockMvc.perform(
            put("/api/vets/1")
                .content(newVetAsJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(content().contentType("application/json;charset=UTF-8"))
            .andExpect(status().isNoContent)

        mockMvc.perform(
            get("/api/vets/1")
                .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType("application/json;charset=UTF-8"))
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.firstName").value("James"))
    }

    @Test
    @Throws(Exception::class)
    fun testUpdateVetError() {
        val newVet = Vet(vet1)
        newVet.firstName = ""
        val mapper = jacksonObjectMapper()
        val newVetAsJSON = mapper.writeValueAsString(newVet)
        mockMvc.perform(
            put("/api/vets/1")
                .content(newVetAsJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    @Throws(Exception::class)
    fun testDeleteVetSuccess() {
        val newVet = Vet(vet1)
        val mapper = jacksonObjectMapper()
        val newVetAsJSON = mapper.writeValueAsString(newVet)
        given<Vet>(vetService.findVetById(1)).willReturn(Vet(vet1))
        mockMvc.perform(
            delete("/api/vets/1")
                .content(newVetAsJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(status().isNoContent)
    }

    @Test
    fun testDeleteVetError() {
        val newVet = Vet(vet1)
        val mapper = jacksonObjectMapper()
        val newVetAsJSON = mapper.writeValueAsString(newVet)
        given<Vet>(vetService.findVetById(-1)).willReturn(null)
        mockMvc.perform(
            delete("/api/vets/-1")
                .content(newVetAsJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(status().isNotFound)
    }
}

