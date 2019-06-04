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

package petclinic.api.specialties

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
import java.util.Optional

@WebMvcTest(controllers = [SpecialtyController::class])
@AutoConfigureMockMvc
class SpecialtyControllerTests {

    @MockBean
    private lateinit var specialtyService: SpecialtyRepository

    @Autowired
    private lateinit var mockMvc: MockMvc

    private val radiology = Specialty(id = 1, name = "radiology")
    private val surgery = Specialty(id = 2, name = "surgery")
    private val dentistry = Specialty(id = 3, name = "dentistry")

    @Test
    @Throws(Exception::class)
    fun testGetSpecialtySuccess() {
        given(specialtyService.findById(1)).willReturn(Optional.of(radiology))
        mockMvc.perform(
            get("/api/specialties/1")
                .accept(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType("application/json;charset=UTF-8"))
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("radiology"))
    }

    @Test
    @Throws(Exception::class)
    fun testGetSpecialtyNotFound() {
        given(specialtyService.findById(-1)).willReturn(Optional.empty())
        mockMvc.perform(
            get("/api/specialties/-1")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isNotFound)
    }

    @Test
    @Throws(Exception::class)
    fun testGetAllSpecialtiesSuccess() {
        given(specialtyService.findAll()).willReturn(listOf(surgery, dentistry))
        mockMvc.perform(
            get("/api/specialties/")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType("application/json;charset=UTF-8"))
            .andExpect(jsonPath("$.[0].id").value(2))
            .andExpect(jsonPath("$.[0].name").value("surgery"))
            .andExpect(jsonPath("$.[1].id").value(3))
            .andExpect(jsonPath("$.[1].name").value("dentistry"))
    }

    @Test
    fun testGetAllSpecialtiesNotFound() {
        given(specialtyService.findAll())
            .willReturn(emptyList())
        mockMvc.perform(
            get("/api/specialties/")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(content().string("[]"))
    }

    @Test
    fun testCreateSpecialtySuccess() {
        mockMvc.perform(
            post("/api/specialties/")
                .content(
                    jacksonObjectMapper()
                        .writeValueAsString(
                            Specialty(radiology).apply {
                                id = 999
                            }
                        )
                )
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(status().isCreated)
    }

    @Test
    @Throws(Exception::class)
    fun testCreateSpecialtyError() {
        mockMvc.perform(
            post("/api/specialties/")
                .content(
                    jacksonObjectMapper().writeValueAsString(
                        Specialty(radiology).apply {
                            id = null
                            name = null
                        }
                    )
                )
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    @Throws(Exception::class)
    fun testUpdateSpecialtySuccess() {

        val newSpecialty = Specialty(surgery).apply {
            name = "surgery I"
        }
        given(specialtyService.findById(2))
            .willReturn(Optional.of(surgery))
        given(specialtyService.save(any<Specialty>())).willReturn(newSpecialty)

        val putAction =
            put("/api/specialties/2")
                .content(
                    jacksonObjectMapper()
                        .writeValueAsString(newSpecialty)
                )
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)

        mockMvc.perform(putAction)
            .andExpect(content().contentType("application/json;charset=UTF-8"))
            .andExpect(status().isNoContent)

        mockMvc.perform(
            get("/api/specialties/2")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(status().isOk).andExpect(content().contentType("application/json;charset=UTF-8"))
            .andExpect(jsonPath("$.id").value(2))
            .andExpect(jsonPath("$.name").value("surgery I"))
    }

    @Test
    @Throws(Exception::class)
    fun testUpdateSpecialtyError() {
        mockMvc.perform(
            put("/api/specialties/1")
                .content(jacksonObjectMapper().writeValueAsString(Specialty(radiology).apply { name = "" }))
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        ).andExpect(status().isBadRequest)
    }

    @Test
    @Throws(Exception::class)
    fun testDeleteSpecialtySuccess() {
        given(specialtyService.findById(1)).willReturn(Optional.of(radiology))

        mockMvc.perform(
            delete("/api/specialties/1")
                .content(jacksonObjectMapper().writeValueAsString(Specialty(radiology)))
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        ).andExpect(status().isNoContent)
    }

    @Test
    @Throws(Exception::class)
    fun testDeleteSpecialtyError() {
        given(specialtyService.findById(-1)).willReturn(Optional.empty())

        mockMvc.perform(
            delete("/api/specialties/-1")
                .content(jacksonObjectMapper().writeValueAsString(Specialty(radiology)))
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        ).andExpect(status().isNotFound)
    }
}
