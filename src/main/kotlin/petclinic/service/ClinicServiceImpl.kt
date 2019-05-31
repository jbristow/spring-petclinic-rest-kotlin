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

import org.springframework.cache.annotation.Cacheable
import org.springframework.dao.DataAccessException
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.orm.ObjectRetrievalFailureException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import petclinic.model.Owner
import petclinic.model.Pet
import petclinic.model.PetType
import petclinic.model.Specialty
import petclinic.model.Vet
import petclinic.model.Visit
import petclinic.repository.OwnerRepository
import petclinic.repository.PetRepository
import petclinic.repository.PetTypeRepository
import petclinic.repository.SpecialtyRepository
import petclinic.repository.VetRepository
import petclinic.repository.VisitRepository

/**
 * Mostly used as a facade for all Petclinic controllers
 * Also a placeholder for @Transactional and @Cacheable annotations
 *
 * @author Michael Isvy
 * @author Vitaliy Fedoriv
 */
@Service
class ClinicServiceImpl(
    private val ownerRepository: OwnerRepository,
    private val petRepository: PetRepository,
    private val petTypeRepository: PetTypeRepository,
    private val specialtyRepository: SpecialtyRepository,
    private val vetRepository: VetRepository,
    private val visitRepository: VisitRepository
) : ClinicService {

    @Transactional(readOnly = true)
    @Throws(DataAccessException::class)
    override fun findAllPets(): List<Pet> {
        return petRepository.findAll().toList()
    }

    @Transactional
    @Throws(DataAccessException::class)
    override fun deletePet(pet: Pet) {
        petRepository.delete(pet)
    }

    @Transactional(readOnly = true)
    @Throws(DataAccessException::class)
    override fun findVisitById(visitId: Int): Visit? {
        return try {
            visitRepository.findById(visitId).orElse(null)
        } catch (e: ObjectRetrievalFailureException) {
            // just ignore not found exceptions for Jdbc/Jpa realization
            null
        } catch (e: EmptyResultDataAccessException) {
            null
        }
    }

    @Transactional(readOnly = true)
    @Throws(DataAccessException::class)
    override fun findAllVisits(): List<Visit> {
        return visitRepository.findAll().toList()
    }

    @Transactional
    @Throws(DataAccessException::class)
    override fun deleteVisit(visit: Visit) {
        visitRepository.delete(visit)
    }

    @Transactional(readOnly = true)
    @Throws(DataAccessException::class)
    override fun findVetById(id: Int): Vet? {
        try {
            return vetRepository.findById(id).orElse(null)
        } catch (e: ObjectRetrievalFailureException) {
            // just ignore not found exceptions for Jdbc/Jpa realization
            return null
        } catch (e: EmptyResultDataAccessException) {
            return null
        }
    }

    @Transactional(readOnly = true)
    @Throws(DataAccessException::class)
    override fun findAllVets(): List<Vet> {
        return vetRepository.findAll().toList()
    }

    @Transactional
    @Throws(DataAccessException::class)
    override fun saveVet(vet: Vet) {
        vetRepository.save(vet)
    }

    @Transactional
    @Throws(DataAccessException::class)
    override fun deleteVet(vet: Vet) {
        vetRepository.delete(vet)
    }

    @Transactional(readOnly = true)
    @Throws(DataAccessException::class)
    override fun findAllOwners(): List<Owner> {
        return ownerRepository.findAll().toList()
    }

    @Transactional
    @Throws(DataAccessException::class)
    override fun deleteOwner(owner: Owner) {
        ownerRepository.delete(owner)
    }

    @Transactional(readOnly = true)
    override fun findPetTypeById(petTypeId: Int): PetType? {
        return try {
            petTypeRepository.findById(petTypeId).orElse(null)
        } catch (e: ObjectRetrievalFailureException) {
            // just ignore not found exceptions for Jdbc/Jpa realization
            null
        } catch (e: EmptyResultDataAccessException) {
            null
        }
    }

    @Transactional(readOnly = true)
    @Throws(DataAccessException::class)
    override fun findAllPetTypes(): List<PetType> {
        return petTypeRepository.findAll().toList()
    }

    @Transactional
    @Throws(DataAccessException::class)
    override fun savePetType(petType: PetType) {
        petTypeRepository.save(petType)
    }

    @Transactional
    @Throws(DataAccessException::class)
    override fun deletePetType(petType: PetType) {
        petTypeRepository.delete(petType)
    }

    @Transactional(readOnly = true)
    override fun findSpecialtyById(specialtyId: Int): Specialty? {
        return try {
            specialtyRepository.findById(specialtyId).orElse(null)
        } catch (e: ObjectRetrievalFailureException) {
            // just ignore not found exceptions for Jdbc/Jpa realization
            null
        } catch (e: EmptyResultDataAccessException) {
            null
        }
    }

    @Transactional(readOnly = true)
    @Throws(DataAccessException::class)
    override fun findAllSpecialties(): List<Specialty> {
        return specialtyRepository.findAll().toList()
    }

    @Transactional
    @Throws(DataAccessException::class)
    override fun saveSpecialty(specialty: Specialty) {
        specialtyRepository.save(specialty)
    }

    @Transactional
    @Throws(DataAccessException::class)
    override fun deleteSpecialty(specialty: Specialty) {
        specialtyRepository.delete(specialty)
    }

    @Transactional(readOnly = true)
    @Throws(DataAccessException::class)
    override fun findPetTypes(): List<PetType> {
        return petRepository.findPetTypes()
    }

    @Transactional(readOnly = true)
    override fun findOwnerById(id: Int): Owner? {
        return try {
            println("findOwnerId $id")
            ownerRepository.findById(id).orElse(null)
        } catch (e: ObjectRetrievalFailureException) {
            // just ignore not found exceptions for Jdbc/Jpa realization
            println("PORBLEM $e")
            null
        } catch (e: EmptyResultDataAccessException) {
            println("PORBLEM $e")
            null
        }
    }

    @Transactional(readOnly = true)
    @Throws(DataAccessException::class)
    override fun findPetById(id: Int): Pet? {
        try {
            return petRepository.findById(id).orElse(null)
        } catch (e: ObjectRetrievalFailureException) {
            // just ignore not found exceptions for Jdbc/Jpa realization
            return null
        } catch (e: EmptyResultDataAccessException) {
            return null
        }
    }

    @Transactional
    @Throws(DataAccessException::class)
    override fun savePet(pet: Pet) {
        petRepository.save(pet)
    }

    @Transactional
    @Throws(DataAccessException::class)
    override fun saveVisit(visit: Visit) {
        visitRepository.save(visit)
    }

    @Transactional(readOnly = true)
    @Cacheable(value = ["vets"])
    @Throws(DataAccessException::class)
    override fun findVets(): List<Vet> {
        return vetRepository.findAll().toList()
    }

    @Transactional
    @Throws(DataAccessException::class)
    override fun saveOwner(owner: Owner) {
        ownerRepository.save(owner)
    }

    @Transactional(readOnly = true)
    @Throws(DataAccessException::class)
    override fun findOwnerByLastName(lastName: String): List<Owner> {
        return ownerRepository.findByLastName(lastName).toList()
    }

    @Transactional(readOnly = true)
    override fun findVisitsByPetId(petId: Int): List<Visit> {
        return visitRepository.findByPetId(petId)
    }
}
