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

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.util.UriComponentsBuilder
import javax.transaction.Transactional
import javax.validation.Valid

@RestController
@CrossOrigin(exposedHeaders = ["errors, content-type"])
@RequestMapping("/api/specialties")
class SpecialtyController {

    @Autowired
    private lateinit var specialtyService: SpecialtyService

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    fun getAllSpecialties() =
        specialtyService.findAllSpecialties()

    @GetMapping("/{specialtyId}")
    @ResponseStatus(HttpStatus.OK)
    fun getSpecialty(@PathVariable("specialtyId") specialtyId: Int): Specialty {
        return specialtyService.findSpecialtyById(specialtyId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Specialty $specialtyId not found.")
    }

    @PostMapping
    fun addSpecialty(
        @RequestBody @Valid specialty: Specialty,
        ucBuilder: UriComponentsBuilder
    ): ResponseEntity<Specialty> {
        val headers = HttpHeaders()
        specialtyService.saveSpecialty(specialty)
        headers.location = ucBuilder.path("/api/specialties/{id}").buildAndExpand(specialty.id).toUri()
        return ResponseEntity(specialty, headers, HttpStatus.CREATED)
    }

    @PutMapping("/{specialtyId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun updateSpecialty(
        @PathVariable("specialtyId") specialtyId: Int,
        @RequestBody @Valid specialty: Specialty
    ) =
        specialtyService.findSpecialtyById(specialtyId)?.apply {
            name = specialty.name
            specialtyService.saveSpecialty(this)
        } ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Specialty $specialtyId not found.")

    @Transactional
    @DeleteMapping("/{specialtyId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteSpecialty(@PathVariable("specialtyId") specialtyId: Int) {
        val specialty = specialtyService.findSpecialtyById(specialtyId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Specialty $specialtyId not found.")
        specialtyService.deleteSpecialty(specialty)
    }
}
