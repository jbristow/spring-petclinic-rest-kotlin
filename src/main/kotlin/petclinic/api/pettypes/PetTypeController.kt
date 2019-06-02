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

package petclinic.api.pettypes

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
@RequestMapping("/api/pettypes")
class PetTypeController(private val petTypeService: PetTypeService) {

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    fun getAllPetTypes() = petTypeService.findAllPetTypes()

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{petTypeId}")
    fun getPetType(@PathVariable("petTypeId") petTypeId: Int) =
        petTypeService.findPetTypeById(petTypeId) ?: throw ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "PetType $petTypeId not found."
        )

    @PostMapping
    fun addPetType(
        @Valid @RequestBody petType: PetType,
        ucBuilder: UriComponentsBuilder
    ): ResponseEntity<PetType> {
        petTypeService.savePetType(petType)

        val headers = HttpHeaders()
        headers.location = ucBuilder.path("/api/pettypes/{id}").buildAndExpand(petType.id).toUri()

        return ResponseEntity(petType, headers, HttpStatus.CREATED)
    }

    @PutMapping("/{petTypeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun updatePetType(
        @PathVariable("petTypeId") petTypeId: Int,
        @RequestBody @Valid petType: PetType
    ): PetType {
        return petTypeService.findPetTypeById(petTypeId)?.apply {
            name = petType.name
            petTypeService.savePetType(this)
        } ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "PetType $petTypeId not found.")
    }

    @Transactional
    @DeleteMapping("/{petTypeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deletePetType(
        @PathVariable("petTypeId") petTypeId: Int
    ) {
        val petType = petTypeService.findPetTypeById(petTypeId) ?: throw ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "PetType $petTypeId not found."
        )
        petTypeService.deletePetType(petType)
    }
}
