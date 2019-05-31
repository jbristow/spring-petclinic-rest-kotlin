/*
 * Copyright 2002-2017 the original author or authors.
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
package petclinic.service

import org.springframework.stereotype.Service
import petclinic.model.Owner
import petclinic.model.Pet
import petclinic.model.PetType
import petclinic.model.Specialty
import petclinic.model.Vet
import petclinic.model.Visit

/**
 * Mostly used as a facade so all controllers have a single point of entry
 *
 * @author Michael Isvy
 * @author Vitaliy Fedoriv
 */
@Service
interface ClinicService {

    fun findPetById(id: Int): Pet?

    fun findAllPets(): List<Pet>

    fun savePet(pet: Pet)

    fun deletePet(pet: Pet)

    fun findVisitsByPetId(petId: Int): List<Visit>
    fun findVisitById(visitId: Int): Visit?

    fun findAllVisits(): List<Visit>

    fun saveVisit(visit: Visit)

    fun deleteVisit(visit: Visit)

    fun findVetById(id: Int): Vet?

    fun findVets(): List<Vet>

    fun findAllVets(): List<Vet>

    fun saveVet(vet: Vet)

    fun deleteVet(vet: Vet)

    fun findOwnerById(id: Int): Owner?

    fun findAllOwners(): List<Owner>

    fun saveOwner(owner: Owner)

    fun deleteOwner(owner: Owner)

    fun findOwnerByLastName(lastName: String): List<Owner>

    fun findPetTypeById(petTypeId: Int): PetType?

    fun findAllPetTypes(): List<PetType>

    fun findPetTypes(): List<PetType>

    fun savePetType(petType: PetType)

    fun deletePetType(petType: PetType)

    fun findSpecialtyById(specialtyId: Int): Specialty?

    fun findAllSpecialties(): List<Specialty>

    fun saveSpecialty(specialty: Specialty)

    fun deleteSpecialty(specialty: Specialty)
}
