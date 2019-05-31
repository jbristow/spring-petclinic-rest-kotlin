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

package petclinic.rest

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder
import petclinic.model.Pet
import petclinic.model.PetType
import petclinic.service.ClinicService
import javax.transaction.Transactional
import javax.validation.Valid

/**
 * @author Vitaliy Fedoriv
 */

@RestController
@CrossOrigin(exposedHeaders = ["errors, content-type"])
@RequestMapping("/api/pets")
open class PetRestController {

    @Autowired
    private lateinit var clinicService: ClinicService

    val pets: ResponseEntity<Collection<Pet>>
        @RequestMapping(value = [""], method = [RequestMethod.GET], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
        get() {
            val pets = this.clinicService!!.findAllPets()
            return if (pets.isEmpty()) {
                ResponseEntity(HttpStatus.NOT_FOUND)
            } else ResponseEntity(pets, HttpStatus.OK)
        }

    val petTypes: ResponseEntity<Collection<PetType>>
        @RequestMapping(value = ["/pettypes"], method = [RequestMethod.GET], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
        get() = ResponseEntity(this.clinicService!!.findPetTypes(), HttpStatus.OK)

    @RequestMapping(value = ["/{petId}"], method = [RequestMethod.GET], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    fun getPet(@PathVariable("petId") petId: Int): ResponseEntity<Pet> {
        val pet = this.clinicService!!.findPetById(petId) ?: return ResponseEntity(HttpStatus.NOT_FOUND)
        return ResponseEntity(pet, HttpStatus.OK)
    }

    @RequestMapping(value = ["/getPetsByOwnerId/{ownerId}"], method = [RequestMethod.GET], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    fun getPetsByOwnerId(@PathVariable("ownerId") ownerId: Int): ResponseEntity<Collection<Pet>> {
        val owner = this.clinicService!!.findOwnerById(ownerId) ?: return ResponseEntity(HttpStatus.NOT_FOUND)
        val pets = owner.pets
        return if (pets.isEmpty()) {
            ResponseEntity(HttpStatus.NOT_FOUND)
        } else ResponseEntity(pets, HttpStatus.OK)
    }

    @RequestMapping(value = [""], method = [RequestMethod.POST], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    fun addPet(@RequestBody @Valid pet: Pet?, bindingResult: BindingResult, ucBuilder: UriComponentsBuilder): ResponseEntity<Pet> {
        val errors = BindingErrorsResponse()
        val headers = HttpHeaders()
        if (bindingResult.hasErrors() || pet == null) {
            errors.addAllErrors(bindingResult)
            headers.add("errors", errors.toJSON())
            return ResponseEntity(headers, HttpStatus.BAD_REQUEST)
        }
        this.clinicService!!.savePet(pet)
        headers.location = ucBuilder.path("/api/pets/{id}").buildAndExpand(pet.id!!).toUri()
        return ResponseEntity(pet, headers, HttpStatus.CREATED)
    }

    @RequestMapping(value = ["/{petId}"], method = [RequestMethod.PUT], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    fun updatePet(@PathVariable("petId") petId: Int, @RequestBody @Valid pet: Pet?, bindingResult: BindingResult): ResponseEntity<Pet> {
        val errors = BindingErrorsResponse()
        val headers = HttpHeaders()
        if (bindingResult.hasErrors() || pet == null) {
            errors.addAllErrors(bindingResult)
            headers.add("errors", errors.toJSON())
            return ResponseEntity(headers, HttpStatus.BAD_REQUEST)
        }
        val currentPet = this.clinicService!!.findPetById(petId) ?: return ResponseEntity(HttpStatus.NOT_FOUND)
        currentPet.birthDate = pet.birthDate
        currentPet.name = pet.name
        currentPet.type = pet.type
        currentPet.owner = pet.owner
        this.clinicService.savePet(currentPet)
        return ResponseEntity(currentPet, HttpStatus.NO_CONTENT)
    }

    @RequestMapping(value = ["/{petId}"], method = [RequestMethod.DELETE], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    @Transactional
    open fun deletePet(@PathVariable("petId") petId: Int): ResponseEntity<Void> {
        val pet = this.clinicService!!.findPetById(petId) ?: return ResponseEntity(HttpStatus.NOT_FOUND)
        this.clinicService.deletePet(pet)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }


}
