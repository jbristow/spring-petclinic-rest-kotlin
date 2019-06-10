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

package petclinic.api.owners

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import petclinic.api.BaseController

@RestController
@CrossOrigin(exposedHeaders = ["errors, content-type"])
@RequestMapping("/api/owners")
class OwnerController(ownerRepository: OwnerRepository) :
    BaseController<Owner, OwnerRepository>("owners", ownerRepository) {

    override fun notFoundProvider(id: Int) = Owner.NotFoundException(id)

    override fun updateFn(a: Owner, b: Owner) {
        a.address = b.address
        a.city = b.city
        a.firstName = b.firstName
        a.lastName = b.lastName
        a.telephone = b.telephone
    }

    @GetMapping("/*/lastname/{lastName}")
    @ResponseStatus(HttpStatus.OK)
    fun getOwnersList(@PathVariable("lastName") ownerLastName: String) =
        repository.findByLastName(ownerLastName)

    @GetMapping("/{id}/pets")
    fun getPetsForOwner(@PathVariable("id") id: Int) =
        repository
            .findById(id)
            .orElseGet { throw Owner.NotFoundException(id) }
            .pets
}
