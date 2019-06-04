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
import java.util.Optional

@WebMvcTest(controllers = [VetController::class])
@AutoConfigureMockMvc
class VetControllerTests {

    @MockBean
    private lateinit var vetRepository: VetRepository

    @Autowired
    private lateinit var mockMvc: MockMvc

    private val vet1 = Vet(id = 1, firstName = "James", lastName = "Carter")
    private val vet2 = Vet(id = 2, firstName = "Helen", lastName = "Leary")

    @Test
    @Throws(Exception::class)
    fun testGetVetSuccess() {
        given(vetRepository.findById(1)).willReturn(Optional.of(Vet(vet1)))
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
        given(vetRepository.findById(-1)).willReturn(Optional.empty())
        mockMvc.perform(
            get("/api/vets/-1")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isNotFound)
    }

    @Test
    @Throws(Exception::class)
    fun testGetAllVetsSuccess() {
        given(vetRepository.findAll()).willReturn(listOf(vet1, vet2))
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
        given(vetRepository.findAll()).willReturn(emptyList())
        mockMvc.perform(
            get("/api/vets/")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(content().string("[]"))
    }

    @Test
    fun testCreateVetSuccess() {
        val newVet = Vet(vet1).apply {
            id = 999
        }
        mockMvc.perform(
            post("/api/vets/")
                .content(jacksonObjectMapper().writeValueAsString(newVet)).accept(MediaType.APPLICATION_JSON_VALUE).contentType(
                    MediaType.APPLICATION_JSON_VALUE
                )
        )
            .andExpect(status().isCreated)
    }

    @Test
    @Throws(Exception::class)
    fun testCreateVetError() {

        val newVet = Vet(vet1).apply {
            id = null
            firstName = ""
        }
        mockMvc.perform(
            post("/api/vets/")
                .content(jacksonObjectMapper().writeValueAsString(newVet))
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun testUpdateVetSuccess() {
        given(vetRepository.findById(1)).willReturn(Optional.of(Vet(vet1)))
        val newVet = Vet(vet1).apply {
            firstName = "James"
        }
        mockMvc.perform(
            put("/api/vets/1")
                .content(jacksonObjectMapper().writeValueAsString(newVet))
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
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
    fun testUpdateVetError() {
        mockMvc.perform(
            put("/api/vets/1")
                .content(jacksonObjectMapper().writeValueAsString(Vet(vet1).apply { firstName = "" }))
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun testDeleteVetSuccess() {
        val newVet = Vet(vet1)

        given(vetRepository.findById(1)).willReturn(Optional.of(Vet(vet1)))

        mockMvc.perform(
            delete("/api/vets/1")
                .content(jacksonObjectMapper().writeValueAsString(newVet))
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(status().isNoContent)
    }

    @Test
    fun testDeleteVetError() {
        given(vetRepository.findById(-1)).willReturn(Optional.empty())

        mockMvc.perform(
            delete("/api/vets/-1")
                .content(jacksonObjectMapper().writeValueAsString(Vet(vet1)))
                .accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(status().isNotFound)
    }
}
