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
import petclinic.model.PetType
import petclinic.service.ClinicService
import java.util.*
import javax.transaction.Transactional
import javax.validation.Valid

@RestController
@CrossOrigin(exposedHeaders = ["errors, content-type"])
@RequestMapping("/api/pettypes")
open class PetTypeRestController {

    @Autowired
    private lateinit var clinicService: ClinicService

    val allPetTypes: ResponseEntity<Collection<PetType>>
        @RequestMapping(value = [""], method = [RequestMethod.GET], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
        get() {
            val petTypes = ArrayList<PetType>()
            petTypes.addAll(this.clinicService!!.findAllPetTypes())
            return if (petTypes.isEmpty()) {
                ResponseEntity(HttpStatus.NOT_FOUND)
            } else ResponseEntity(petTypes, HttpStatus.OK)
        }

    @RequestMapping(value = ["/{petTypeId}"], method = [RequestMethod.GET], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    fun getPetType(@PathVariable("petTypeId") petTypeId: Int): ResponseEntity<PetType> {
        val petType = this.clinicService!!.findPetTypeById(petTypeId) ?: return ResponseEntity(HttpStatus.NOT_FOUND)
        return ResponseEntity(petType, HttpStatus.OK)
    }


    @RequestMapping(value = [""], method = [RequestMethod.POST], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    fun addPetType(@RequestBody @Valid petType: PetType?, bindingResult: BindingResult, ucBuilder: UriComponentsBuilder): ResponseEntity<PetType> {
        val errors = BindingErrorsResponse()
        val headers = HttpHeaders()
        if (bindingResult.hasErrors() || petType == null) {
            errors.addAllErrors(bindingResult)
            headers.add("errors", errors.toJSON())
            return ResponseEntity(headers, HttpStatus.BAD_REQUEST)
        }
        this.clinicService!!.savePetType(petType)
        headers.location = ucBuilder.path("/api/pettypes/{id}").buildAndExpand(petType.id!!).toUri()
        return ResponseEntity(petType, headers, HttpStatus.CREATED)
    }

    @RequestMapping(value = ["/{petTypeId}"], method = [RequestMethod.PUT], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    fun updatePetType(@PathVariable("petTypeId") petTypeId: Int, @RequestBody @Valid petType: PetType?, bindingResult: BindingResult): ResponseEntity<PetType> {
        val errors = BindingErrorsResponse()
        val headers = HttpHeaders()
        if (bindingResult.hasErrors() || petType == null) {
            errors.addAllErrors(bindingResult)
            headers.add("errors", errors.toJSON())
            return ResponseEntity(headers, HttpStatus.BAD_REQUEST)
        }
        val currentPetType = this.clinicService!!.findPetTypeById(petTypeId)
            ?: return ResponseEntity(HttpStatus.NOT_FOUND)
        currentPetType.name = petType.name
        this.clinicService.savePetType(currentPetType)
        return ResponseEntity(currentPetType, HttpStatus.NO_CONTENT)
    }

    @RequestMapping(value = ["/{petTypeId}"], method = [RequestMethod.DELETE], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    @Transactional
    open fun deletePetType(@PathVariable("petTypeId") petTypeId: Int): ResponseEntity<Void> {
        val petType = this.clinicService!!.findPetTypeById(petTypeId) ?: return ResponseEntity(HttpStatus.NOT_FOUND)
        this.clinicService.deletePetType(petType)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }

}
