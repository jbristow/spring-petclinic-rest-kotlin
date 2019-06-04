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
import org.springframework.web.util.UriComponentsBuilder
import javax.transaction.Transactional
import javax.validation.Valid

@RestController
@CrossOrigin(exposedHeaders = ["errors, content-type"])
@RequestMapping("/api")
class PetTypeController(private val petTypeRepository: PetTypeRepository) {

    @GetMapping(value = ["pettypes", "pets/pettypes"])
    @ResponseStatus(HttpStatus.OK)
    fun getAllPetTypes(): Iterable<PetType> = petTypeRepository.findAll()

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("pettypes/{petTypeId}")
    fun getPetType(@PathVariable("petTypeId") petTypeId: Int): PetType =
        petTypeRepository.findById(petTypeId).orElseThrow { PetType.NotFoundException(petTypeId) }

    @PostMapping("pettypes")
    fun addPetType(
        @Valid @RequestBody petType: PetType,
        ucBuilder: UriComponentsBuilder
    ): ResponseEntity<PetType> {
        val saved = petTypeRepository.save(petType)
        return ResponseEntity
            .created(ucBuilder.path("/api/pettypes/{id}").buildAndExpand(petType.id).toUri())
            .body(saved)
    }

    @PutMapping("pettypes/{petTypeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun updatePetType(
        @PathVariable("petTypeId") petTypeId: Int,
        @RequestBody @Valid petType: PetType
    ): PetType = petTypeRepository.findById(petTypeId)
        .orElseThrow { PetType.NotFoundException(petTypeId) }
        .apply {
            name = petType.name
            petTypeRepository.save(this)
        }

    @Transactional
    @DeleteMapping("pettypes/{petTypeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deletePetType(
        @PathVariable("petTypeId") petTypeId: Int
    ) {
        val petType = petTypeRepository.findById(petTypeId).orElseThrow { PetType.NotFoundException(petTypeId) }
        petTypeRepository.delete(petType)
    }
}
