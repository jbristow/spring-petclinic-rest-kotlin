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

package petclinic.api.visits

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
@RequestMapping("/api/visits")
class VisitController(val visitService: VisitRepository) {

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    fun getAllVisits(): Iterable<Visit> = visitService.findAll()

    @GetMapping("/{visitId}")
    @ResponseStatus(HttpStatus.OK)
    fun getVisit(@PathVariable("visitId") visitId: Int): Visit =
        visitService.findById(visitId).orElseThrow { Visit.NotFoundException(visitId) }

    @PostMapping
    fun addVisit(
        @RequestBody @Valid visit: Visit,
        ucBuilder: UriComponentsBuilder
    ): ResponseEntity<Visit> {
        val saved = visitService.save(visit)
        return ResponseEntity.created(ucBuilder.path("/api/visits/{id}").buildAndExpand(visit.id).toUri()).body(saved)
    }

    @PutMapping("/{visitId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun updateVisit(
        @PathVariable("visitId") visitId: Int,
        @RequestBody @Valid visit: Visit
    ): Visit =
        visitService.findById(visitId).orElseThrow { Visit.NotFoundException(visitId) }.apply {
            date = visit.date
            description = visit.description
            pet = visit.pet
            visitService.save(this)
        }

    @DeleteMapping("/{visitId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    fun deleteVisit(@PathVariable("visitId") visitId: Int) {
        val visit = visitService.findById(visitId).orElseThrow { Visit.NotFoundException(visitId) }
        visitService.delete(visit)
    }
}
