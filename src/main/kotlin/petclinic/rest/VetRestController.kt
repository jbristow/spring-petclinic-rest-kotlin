/*
 * Copyright 2016-2018 the original author or authors.
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
import petclinic.model.Vet
import petclinic.service.ClinicService
import java.util.*
import javax.transaction.Transactional
import javax.validation.Valid

/**
 * @author Vitaliy Fedoriv
 */

@RestController
@CrossOrigin(exposedHeaders = ["errors, content-type"])
@RequestMapping("/api/vets")
open class VetRestController {

    @Autowired
    private lateinit var clinicService: ClinicService

    val allVets: ResponseEntity<Collection<Vet>>
        @RequestMapping(value = [""], method = [RequestMethod.GET], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
        get() {
            val vets = ArrayList<Vet>()
            vets.addAll(clinicService.findAllVets())
            return if (vets.isEmpty()) {
                ResponseEntity(HttpStatus.NOT_FOUND)
            } else ResponseEntity(vets, HttpStatus.OK)
        }

    @RequestMapping(
        value = ["/{vetId}"],
        method = [RequestMethod.GET],
        produces = [MediaType.APPLICATION_JSON_UTF8_VALUE]
    )
    fun getVet(@PathVariable("vetId") vetId: Int): ResponseEntity<Vet> {
        val vet = clinicService.findVetById(vetId) ?: return ResponseEntity(HttpStatus.NOT_FOUND)
        return ResponseEntity(vet, HttpStatus.OK)
    }


    @RequestMapping(value = [""], method = [RequestMethod.POST], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    fun addVet(
        @RequestBody @Valid vet: Vet?, bindingResult: BindingResult,
        ucBuilder: UriComponentsBuilder
    ): ResponseEntity<Vet> {
        val errors = BindingErrorsResponse()
        val headers = HttpHeaders()
        if (bindingResult.hasErrors() || vet == null) {
            errors.addAllErrors(bindingResult)
            headers.add("errors", errors.toJSON())
            return ResponseEntity(headers, HttpStatus.BAD_REQUEST)
        }
        clinicService.saveVet(vet)
        headers.location = ucBuilder.path("/api/vets/{id}").buildAndExpand(vet.id).toUri()
        return ResponseEntity(vet, headers, HttpStatus.CREATED)
    }

    @RequestMapping(
        value = ["/{vetId}"],
        method = [RequestMethod.PUT],
        produces = [MediaType.APPLICATION_JSON_UTF8_VALUE]
    )
    fun updateVet(@PathVariable("vetId") vetId: Int, @RequestBody @Valid vet: Vet?, bindingResult: BindingResult): ResponseEntity<Vet> {
        val errors = BindingErrorsResponse()
        val headers = HttpHeaders()
        if (bindingResult.hasErrors() || vet == null) {
            errors.addAllErrors(bindingResult)
            headers.add("errors", errors.toJSON())
            return ResponseEntity(headers, HttpStatus.BAD_REQUEST)
        }
        val currentVet = clinicService.findVetById(vetId) ?: return ResponseEntity(HttpStatus.NOT_FOUND)
        currentVet.firstName = vet.firstName
        currentVet.lastName = vet.lastName
        currentVet.clearSpecialties()
        for (spec in vet.specialties) {
            currentVet.addSpecialty(spec)
        }
        clinicService.saveVet(currentVet)
        return ResponseEntity(currentVet, HttpStatus.NO_CONTENT)
    }

    @RequestMapping(
        value = ["/{vetId}"],
        method = [RequestMethod.DELETE],
        produces = [MediaType.APPLICATION_JSON_UTF8_VALUE]
    )
    @Transactional
    open fun deleteVet(@PathVariable("vetId") vetId: Int): ResponseEntity<Void> {
        val vet = clinicService.findVetById(vetId) ?: return ResponseEntity(HttpStatus.NOT_FOUND)
        clinicService.deleteVet(vet)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }


}
