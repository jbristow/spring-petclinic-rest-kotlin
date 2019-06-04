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

package petclinic.api.pets

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
import petclinic.api.owners.Owner
import petclinic.api.owners.OwnerRepository
import javax.transaction.Transactional
import javax.validation.Valid

@RestController
@CrossOrigin(exposedHeaders = ["errors, content-type"])
@RequestMapping("/api/pets")
class PetController(
    private var petRepository: PetRepository,
    private var ownerRepository: OwnerRepository
) {

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    fun getPets(): Iterable<Pet> = petRepository.findAll()

    @GetMapping("/{petId}")
    @ResponseStatus(HttpStatus.OK)
    fun getPet(@PathVariable("petId") petId: Int): Pet =
        petRepository.findById(petId).orElseGet { throw Pet.NotFoundException(petId) }

    @GetMapping("/getPetsByOwnerId/{ownerId}")
    fun getPetsByOwnerId(
        @PathVariable("ownerId") ownerId: Int
    ) =
        ownerRepository
            .findById(ownerId)
            .orElseGet { throw Owner.NotFoundException(ownerId) }
            .pets

    @PostMapping
    fun addPet(
        @Valid @RequestBody pet: Pet,
        ucBuilder: UriComponentsBuilder
    ): ResponseEntity<Pet> {
        val saved = petRepository.save(pet)
        return ResponseEntity
            .created(ucBuilder.path("/api/pets/{id}").buildAndExpand(saved.id).toUri())
            .body(pet)
    }

    @PutMapping("/{petId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun updatePet(
        @PathVariable("petId") petId: Int,
        @Valid @RequestBody pet: Pet
    ) =
        petRepository.findById(petId).orElseGet { throw Pet.NotFoundException(petId) }.apply {
            birthDate = pet.birthDate
            name = pet.name
            type = pet.type
            owner = pet.owner
            petRepository.save(this)
        }

    @Transactional
    @DeleteMapping("/{petId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deletePet(@PathVariable("petId") petId: Int) {
        petRepository.delete(
            petRepository.findById(petId).orElseGet { throw Pet.NotFoundException(petId) }
        )
    }
}
