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
import petclinic.model.Visit
import petclinic.service.ClinicService
import java.util.*
import javax.transaction.Transactional
import javax.validation.Valid

/**
 * @author Vitaliy Fedoriv
 */

@RestController
@CrossOrigin(exposedHeaders = ["errors, content-type"])
@RequestMapping("/api/visits")
open class VisitRestController {

    @Autowired
    private lateinit var clinicService: ClinicService

    @RequestMapping(value = [""], method = [RequestMethod.GET], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    fun getAllVisits(): ResponseEntity<List<Visit>> {
        val visits = ArrayList<Visit>()
        visits.addAll(this.clinicService.findAllVisits())
        return if (visits.isEmpty()) {
            ResponseEntity(HttpStatus.NOT_FOUND)
        } else ResponseEntity(visits, HttpStatus.OK)
    }

    @RequestMapping(value = ["/{visitId}"], method = [RequestMethod.GET], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    fun getVisit(@PathVariable("visitId") visitId: Int): ResponseEntity<Visit> {
        val visit = this.clinicService.findVisitById(visitId) ?: return ResponseEntity(HttpStatus.NOT_FOUND)
        return ResponseEntity(visit, HttpStatus.OK)
    }


    @RequestMapping(value = [""], method = [RequestMethod.POST], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    fun addVisit(@RequestBody @Valid visit: Visit?, bindingResult: BindingResult, ucBuilder: UriComponentsBuilder): ResponseEntity<Visit> {
        val errors = BindingErrorsResponse()
        val headers = HttpHeaders()
        if (bindingResult.hasErrors() || visit == null || visit.pet == null) {
            errors.addAllErrors(bindingResult)
            headers.add("errors", errors.toJSON())
            return ResponseEntity(headers, HttpStatus.BAD_REQUEST)
        }
        this.clinicService.saveVisit(visit)
        headers.location = ucBuilder.path("/api/visits/{id}").buildAndExpand(visit.id).toUri()
        return ResponseEntity(visit, headers, HttpStatus.CREATED)
    }

    @RequestMapping(value = ["/{visitId}"], method = [RequestMethod.PUT], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    fun updateVisit(@PathVariable("visitId") visitId: Int, @RequestBody @Valid visit: Visit?, bindingResult: BindingResult): ResponseEntity<Visit> {
        val errors = BindingErrorsResponse()
        val headers = HttpHeaders()
        if (bindingResult.hasErrors() || visit == null || visit.pet == null) {
            errors.addAllErrors(bindingResult)
            headers.add("errors", errors.toJSON())
            return ResponseEntity(headers, HttpStatus.BAD_REQUEST)
        }
        val currentVisit = this.clinicService.findVisitById(visitId) ?: return ResponseEntity(HttpStatus.NOT_FOUND)
        currentVisit.date = visit.date
        currentVisit.description = visit.description
        currentVisit.pet = visit.pet
        clinicService.saveVisit(currentVisit)
        return ResponseEntity(currentVisit, HttpStatus.NO_CONTENT)
    }

    @RequestMapping(value = ["/{visitId}"], method = [RequestMethod.DELETE], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    @Transactional
    open fun deleteVisit(@PathVariable("visitId") visitId: Int): ResponseEntity<Void> {
        val visit = this.clinicService.findVisitById(visitId) ?: return ResponseEntity(HttpStatus.NOT_FOUND)
        this.clinicService.deleteVisit(visit)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }

}
