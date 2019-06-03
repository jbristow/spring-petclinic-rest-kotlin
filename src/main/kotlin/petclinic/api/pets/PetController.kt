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
import petclinic.api.owners.OwnerService
import petclinic.api.pettypes.PetTypeService
import javax.transaction.Transactional
import javax.validation.Valid

@RestController
@CrossOrigin(exposedHeaders = ["errors, content-type"])
@RequestMapping("/api/pets")
class PetController(
    private var petService: PetService,
    private var petTypeService: PetTypeService,
    private var ownerService: OwnerService
) {

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    fun getPets() = petService.findAllPets()

    @GetMapping("/pettypes")
    @ResponseStatus(HttpStatus.OK)
    fun getPetTypes() = petTypeService.findAllPetTypes()

    @GetMapping("/{petId}")
    @ResponseStatus(HttpStatus.OK)
    fun getPet(@PathVariable("petId") petId: Int) =
        petService.findPetById(petId) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)

    @GetMapping("/getPetsByOwnerId/{ownerId}")
    fun getPetsByOwnerId(
        @PathVariable("ownerId") ownerId: Int
    ): Set<Pet> {
        return ownerService.findOwnerById(ownerId)?.pets ?: throw ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "owners $ownerId not found"
        )
    }

    @PostMapping
    fun addPet(
        @Valid @RequestBody pet: Pet,
        ucBuilder: UriComponentsBuilder
    ): ResponseEntity<Pet> {
        val headers = HttpHeaders()
        petService.savePet(pet)
        headers.location = ucBuilder.path("/api/pets/{id}").buildAndExpand(pet.id).toUri()
        return ResponseEntity(pet, headers, HttpStatus.CREATED)
    }

    @PutMapping("/{petId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun updatePet(
        @PathVariable("petId") petId: Int,
        @Valid @RequestBody pet: Pet
    ): Pet {
        val currentPet = petService.findPetById(petId) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
        currentPet.apply {
            birthDate = pet.birthDate
            name = pet.name
            type = pet.type
            owner = pet.owner
            petService.savePet(this)
        }
        println("modifying $currentPet")
        return currentPet
    }

    @Transactional
    @DeleteMapping("/{petId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deletePet(@PathVariable("petId") petId: Int) {
        val pet = petService.findPetById(petId) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
        petService.deletePet(pet)
    }
}
