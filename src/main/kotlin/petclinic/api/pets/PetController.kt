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

import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import petclinic.api.BaseController
import petclinic.api.owners.Owner
import petclinic.api.owners.OwnerRepository

@RestController
@CrossOrigin(exposedHeaders = ["errors, content-type"])
@RequestMapping("/api/pets")
class PetController(
    petRepository: PetRepository,
    private var ownerRepository: OwnerRepository
) : BaseController<Pet, PetRepository>("pets", petRepository) {

    override fun notFoundProvider(id: Int) = Pet.NotFoundException(id)

    override fun updateFn(a: Pet, b: Pet) {
        a.birthDate = b.birthDate
        a.name = b.name
        a.type = b.type
        a.owner = b.owner
    }

    @GetMapping("/getPetsByOwnerId/{ownerId}")
    fun getPetsByOwnerId(
        @PathVariable("ownerId") ownerId: Int
    ) =
        ownerRepository
            .findById(ownerId)
            .orElseGet { throw Owner.NotFoundException(ownerId) }
            .pets
}
