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
package petclinic.api.vets

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
@RequestMapping("/api/vets")
class VetController(private var vetRepository: VetRepository) {

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    fun getAllVets(): Iterable<Vet> = vetRepository.findAll()

    @GetMapping("/{vetId}")
    @ResponseStatus(HttpStatus.OK)
    fun getVet(@PathVariable("vetId") vetId: Int): Vet =
        vetRepository.findById(vetId).orElseThrow { Vet.NotFoundException(vetId) }

    @PostMapping
    fun addVet(
        @RequestBody @Valid vet: Vet,
        ucBuilder: UriComponentsBuilder
    ): ResponseEntity<Vet> {
        val saved = vetRepository.save(vet)
        return ResponseEntity.created(ucBuilder.path("/api/vets/{id}").buildAndExpand(vet.id).toUri())
            .body(saved)
    }

    @PutMapping("/{vetId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun updateVet(
        @PathVariable("vetId") vetId: Int,
        @RequestBody @Valid vet: Vet
    ) =
        vetRepository.findById(vetId).orElseThrow { Vet.NotFoundException(vetId) }.apply {
            firstName = vet.firstName
            lastName = vet.lastName
            specialties = vet.specialties
            vetRepository.save(this)
        } ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Vet $vetId not found")

    @DeleteMapping("/{vetId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    fun deleteVet(@PathVariable("vetId") vetId: Int) {
        val vet =
            vetRepository.findById(vetId).orElseThrow { Vet.NotFoundException(vetId) }
        vetRepository.delete(vet)
    }
}
