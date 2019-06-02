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

package petclinic.api.owner

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.util.UriComponentsBuilder
import petclinic.api.BindingErrorsResponse
import javax.transaction.Transactional
import javax.validation.Valid

/**
 * @author Vitaliy Fedoriv
 */

@RestController
@CrossOrigin(exposedHeaders = ["errors, content-type"])
@RequestMapping("/api/owners")
class OwnerRestController(val ownerService: OwnerService) {

    val owners: ResponseEntity<Collection<Owner>>
        @RequestMapping(value = [""], method = [RequestMethod.GET], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
        get() {
            val owners = ownerService.findAllOwners()
            return if (owners.isEmpty()) {
                ResponseEntity(HttpStatus.NOT_FOUND)
            } else ResponseEntity(owners, HttpStatus.OK)
        }

    @RequestMapping(
        value = ["/*/lastname/{lastName}"],
        method = [RequestMethod.GET],
        produces = [MediaType.APPLICATION_JSON_UTF8_VALUE]
    )
    fun getOwnersList(@PathVariable("lastName") ownerLastName: String?): ResponseEntity<Collection<Owner>> {
        val owners = ownerService.findOwnerByLastName(ownerLastName ?: "")
        return if (owners.isEmpty()) {
            ResponseEntity(HttpStatus.NOT_FOUND)
        } else ResponseEntity(owners, HttpStatus.OK)
    }

    @RequestMapping(
        value = ["/{ownerId}"],
        method = [RequestMethod.GET],
        produces = [MediaType.APPLICATION_JSON_UTF8_VALUE]
    )
    fun getOwner(@PathVariable("ownerId") ownerId: Int): ResponseEntity<Owner> {
        println("ownerID: $ownerId, CLINICSERVICE: $ownerService")
        val owner = ownerService.findOwnerById(ownerId)
        return if (owner == null) {
            ResponseEntity(HttpStatus.NOT_FOUND)
        } else ResponseEntity(owner, HttpStatus.OK)
    }

    @RequestMapping(value = [""], method = [RequestMethod.POST], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    fun addOwner(
        @RequestBody @Valid owner: Owner, bindingResult: BindingResult,
        ucBuilder: UriComponentsBuilder
    ): ResponseEntity<Owner> {
        val errors = BindingErrorsResponse()
        val headers = HttpHeaders()
        if (bindingResult.hasErrors()) {
            errors.addAllErrors(bindingResult)
            headers.add("errors", errors.toJSON())
            return ResponseEntity(headers, HttpStatus.BAD_REQUEST)
        }
        ownerService.saveOwner(owner)
        headers.location = ucBuilder.path("/api/owners/{id}").buildAndExpand(owner.id).toUri()
        return ResponseEntity(owner, headers, HttpStatus.CREATED)
    }

    @RequestMapping(
        value = ["/{ownerId}"],
        method = [RequestMethod.PUT],
        produces = [MediaType.APPLICATION_JSON_UTF8_VALUE]
    )
    fun updateOwner(
        @PathVariable("ownerId") ownerId: Int,
        @RequestBody @Valid owner: Owner,
        bindingResult: BindingResult
    ): ResponseEntity<Owner> {

        if (bindingResult.hasErrors()) {
            return ResponseEntity(HttpHeaders().apply {
                add("errors", BindingErrorsResponse().apply {
                    addAllErrors(bindingResult)
                }.toJSON())
            }, HttpStatus.BAD_REQUEST)
        }

        val currentOwner = ownerService.findOwnerById(ownerId)?.apply {
            address = owner.address
            city = owner.city
            firstName = owner.firstName
            lastName = owner.lastName
            telephone = owner.telephone
        }

        return if (currentOwner == null) {
            ResponseEntity(HttpStatus.NOT_FOUND)
        } else {
            ownerService.saveOwner(currentOwner)
            ResponseEntity(currentOwner, HttpStatus.NO_CONTENT)
        }
    }

    @RequestMapping(
        value = ["/{ownerId}"],
        method = [RequestMethod.DELETE],
        produces = [MediaType.APPLICATION_JSON_UTF8_VALUE]
    )
    @Transactional
    open fun deleteOwner(@PathVariable("ownerId") ownerId: Int): ResponseEntity<Void> {
        val owner = ownerService.findOwnerById(ownerId) ?: return ResponseEntity(HttpStatus.NOT_FOUND)
        ownerService.deleteOwner(owner)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }
}
