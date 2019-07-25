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
    private lateinit var specialtyRepository: SpecialtyRepository

    @Autowired
    private lateinit var mockMvc: MockMvc

    companion object {
        val radiology: Specialty
            get() = Specialty(id = 1, name = "radiology")
        val surgery: Specialty
            get() = Specialty(id = 2, name = "surgery")
        val dentistry: Specialty
            get() = Specialty(id = 3, name = "dentistry")
        val emptyName: Specialty
            get() = Specialty(id = 1, name = "")
        val renamedSurgery: Specialty
            get() = Specialty(id = 2, name = "surgery I")
        val newSpecialty: Specialty
            get() = Specialty(id = 999, name = "radiology")
    }

    @Test
    @Throws(Exception::class)
    fun testGetSpecialtySuccess() {
        given(specialtyRepository.findById(1)).willReturn(Optional.of(radiology))
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
        given(specialtyRepository.findById(-1)).willReturn(Optional.empty())
        mockMvc.perform(
            get("/api/specialties/-1")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isNotFound)
    }

    @Test
    @Throws(Exception::class)
    fun testGetAllSpecialtiesSuccess() {
        given(specialtyRepository.findAll()).willReturn(listOf(surgery, dentistry))
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
        given(specialtyRepository.findAll())
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
        radiology
        given(specialtyRepository.save(any<Specialty>())).willReturn(newSpecialty)
        mockMvc.perform(
            post("/api/specialties/")
                .content(jacksonObjectMapper().writeValueAsString(newSpecialty))
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
                            id = -1
                            name = ""
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

        val newSpecialty = renamedSurgery
        given(specialtyRepository.findById(2))
            .willReturn(Optional.of(surgery))
        given(specialtyRepository.save(any<Specialty>())).willReturn(newSpecialty)

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
                .content(jacksonObjectMapper().writeValueAsString(emptyName))
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        ).andExpect(status().isBadRequest)
    }

    @Test
    @Throws(Exception::class)
    fun testDeleteSpecialtySuccess() {
        given(specialtyRepository.findById(1)).willReturn(Optional.of(radiology))

        mockMvc.perform(
            delete("/api/specialties/1")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        ).andExpect(status().isNoContent)
    }

    @Test
    @Throws(Exception::class)
    fun testDeleteSpecialtyError() {
        given(specialtyRepository.findById(-1)).willReturn(Optional.empty())

        mockMvc.perform(
            delete("/api/specialties/-1")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        ).andExpect(status().isNotFound)
    }
}
