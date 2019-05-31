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
import petclinic.model.Specialty
import petclinic.service.ClinicService
import java.util.*
import javax.transaction.Transactional
import javax.validation.Valid

/**
 * @author Vitaliy Fedoriv
 */

@RestController
@CrossOrigin(exposedHeaders = ["errors, content-type"])
@RequestMapping("/api/specialties")
open class SpecialtyRestController {

    @Autowired
    private lateinit var clinicService: ClinicService

    val allSpecialtys: ResponseEntity<Collection<Specialty>>
        @RequestMapping(value = [""], method = [RequestMethod.GET], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
        get() {
            val specialties = ArrayList<Specialty>()
            specialties.addAll(clinicService!!.findAllSpecialties())
            return if (specialties.isEmpty()) {
                ResponseEntity(HttpStatus.NOT_FOUND)
            } else ResponseEntity(specialties, HttpStatus.OK)
        }

    @RequestMapping(value = ["/{specialtyId}"], method = [RequestMethod.GET], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    fun getSpecialty(@PathVariable("specialtyId") specialtyId: Int): ResponseEntity<Specialty> {
        val specialty = clinicService!!.findSpecialtyById(specialtyId)
            ?: return ResponseEntity(HttpStatus.NOT_FOUND)
        return ResponseEntity(specialty, HttpStatus.OK)
    }


    @RequestMapping(value = [""], method = [RequestMethod.POST], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    fun addSpecialty(@RequestBody @Valid specialty: Specialty?, bindingResult: BindingResult, ucBuilder: UriComponentsBuilder): ResponseEntity<Specialty> {
        val errors = BindingErrorsResponse()
        val headers = HttpHeaders()
        if (bindingResult.hasErrors() || specialty == null) {
            errors.addAllErrors(bindingResult)
            headers.add("errors", errors.toJSON())
            return ResponseEntity(headers, HttpStatus.BAD_REQUEST)
        }
        clinicService!!.saveSpecialty(specialty)
        headers.location = ucBuilder.path("/api/specialtys/{id}").buildAndExpand(specialty.id!!).toUri()
        return ResponseEntity(specialty, headers, HttpStatus.CREATED)
    }

    @RequestMapping(value = ["/{specialtyId}"], method = [RequestMethod.PUT], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    fun updateSpecialty(@PathVariable("specialtyId") specialtyId: Int, @RequestBody @Valid specialty: Specialty?, bindingResult: BindingResult): ResponseEntity<Specialty> {
        val errors = BindingErrorsResponse()
        val headers = HttpHeaders()
        if (bindingResult.hasErrors() || specialty == null) {
            errors.addAllErrors(bindingResult)
            headers.add("errors", errors.toJSON())
            return ResponseEntity(headers, HttpStatus.BAD_REQUEST)
        }
        val currentSpecialty = clinicService!!.findSpecialtyById(specialtyId)
            ?: return ResponseEntity(HttpStatus.NOT_FOUND)
        currentSpecialty.name = specialty.name
        clinicService.saveSpecialty(currentSpecialty)
        return ResponseEntity(currentSpecialty, HttpStatus.NO_CONTENT)
    }

    @RequestMapping(value = ["/{specialtyId}"], method = [RequestMethod.DELETE], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    @Transactional
    open fun deleteSpecialty(@PathVariable("specialtyId") specialtyId: Int): ResponseEntity<Void> {
        val specialty = clinicService!!.findSpecialtyById(specialtyId)
            ?: return ResponseEntity(HttpStatus.NOT_FOUND)
        clinicService.deleteSpecialty(specialty)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }

}
