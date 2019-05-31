package petclinic.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import petclinic.model.Owner
import petclinic.model.Pet
import petclinic.model.PetType
import petclinic.model.Specialty
import petclinic.model.Vet
import petclinic.model.Visit
import petclinic.util.EntityUtils
import java.util.Date
import javax.transaction.Transactional

/**
 *
 *  Integration test using the 'Spring Data' profile.
 *
 * @author Michael Isvy
 */

@ExtendWith(SpringExtension::class)
@SpringBootTest
class ClinicServiceSpringDataJpaTests(
    @Autowired val clinicService: ClinicService
) {

    @Test
    fun shouldFindOwnersByLastName() {
        var owners: Collection<Owner> = clinicService.findOwnerByLastName("Davis")
        assertThat(owners.size).isEqualTo(2)

        owners = clinicService.findOwnerByLastName("Daviss")
        assertThat(owners.isEmpty()).isTrue()
    }

    @Test
    fun shouldFindSingleOwnerWithPet() {
        val owner = clinicService.findOwnerById(1)
        assertThat(owner?.lastName).startsWith("Franklin")
        assertThat(owner?.pets?.size).isEqualTo(1)
        val pet = owner?.pets?.toList()?.get(0)
        assertThat(pet?.type).isNotNull
        assertThat(pet?.type?.name).isEqualTo("cat")
    }

    @Test
    @Transactional
    fun shouldInsertOwner() {
        val owners = clinicService.findOwnerByLastName("Schultz")
        val found = owners.size

        val owner = Owner().apply {
            firstName = "Sam"
            lastName = "Schultz"
            address = "4, Evans Street"
            city = "Wollongong"
            telephone = "4444444444"
        }
        clinicService.saveOwner(owner)
        assertThat(owner.id).isNotNull()

        val ownersActual = clinicService.findOwnerByLastName("Schultz")
        assertThat(ownersActual.size).isEqualTo(found + 1)
    }

    @Test
    @Transactional
    fun shouldUpdateOwner() {
        val owner = clinicService.findOwnerById(1)!!
        val oldLastName = owner.lastName
        val newLastName = "${oldLastName}X"

        owner.lastName = newLastName
        clinicService.saveOwner(owner)

        // retrieving new name from database
        val ownerActual = clinicService.findOwnerById(1)
        assertThat(ownerActual?.lastName).isEqualTo(newLastName)
    }

    @Test
    fun shouldFindPetWithCorrectId() {
        val pet7 = clinicService.findPetById(7)!!
        assertThat(pet7.name).startsWith("Samantha")
        assertThat(pet7.owner?.firstName).isEqualTo("Jean")
    }

    //    @Test
    //    public void shouldFindAllPetTypes() {
    //        Collection<PetType> petTypes = this.clinicService.findPetTypes();
    //
    //        PetType petType1 = EntityUtils.getById(petTypes, PetType.class, 1);
    //        assertThat(petType1.getName()).isEqualTo("cat");
    //        PetType petType4 = EntityUtils.getById(petTypes, PetType.class, 4);
    //        assertThat(petType4.getName()).isEqualTo("snake");
    //    }

    @Test
    @Transactional
    fun shouldInsertPetIntoDatabaseAndGenerateId() {
        val owner6 = clinicService.findOwnerById(6)!!
        val found = owner6.pets.size

        val pet = Pet()
        pet.name = "bowser"
        val types = clinicService.findPetTypes()
        print("types: $types")
        pet.type = EntityUtils.getById(types, PetType::class, 2)
        pet.birthDate = Date()
        owner6.addPet(pet)
        assertThat(owner6.pets.size).isEqualTo(found + 1)

        clinicService.savePet(pet)
        clinicService.saveOwner(owner6)

        val owner6Actual = clinicService.findOwnerById(6)
        assertThat(owner6Actual?.pets?.size).isEqualTo(found + 1)
        // checks that id has been generated
        assertThat(pet.id).isNotNull()
    }

    @Test
    @Transactional
    fun shouldUpdatePetName() {
        val pet7 = clinicService.findPetById(7)!!
        val oldName = pet7.name

        val newName = oldName + "X"
        pet7.name = newName
        clinicService.savePet(pet7)

        val pet7Actual = clinicService.findPetById(7)
        assertThat(pet7Actual?.name).isEqualTo(newName)
    }

    @Test
    fun shouldFindVets() {
        val vets = clinicService.findVets()

        val vet = EntityUtils.getById(vets, Vet::class, 3)
        assertThat(vet.lastName).isEqualTo("Douglas")
        assertThat(vet.nrOfSpecialties).isEqualTo(2)
        assertThat(vet.specialties.elementAt(0).name).isEqualTo("dentistry")
        assertThat(vet.specialties.elementAt(1).name).isEqualTo("surgery")
    }

    @Test
    @Transactional
    fun shouldAddNewVisitForPet() {
        val pet7 = clinicService.findPetById(7)!!
        val found = pet7.visits.size
        val visit = Visit()
        pet7.addVisit(visit)
        visit.description = "test"
        clinicService.saveVisit(visit)
        clinicService.savePet(pet7)

        val pet7Actual = clinicService.findPetById(7)
        assertThat(pet7Actual?.visits?.size).isEqualTo(found + 1)
        assertThat(visit.id).isNotNull()
    }

    @Test
    fun shouldFindVisitsByPetId() {
        val visits = clinicService.findVisitsByPetId(7)
        assertThat(visits.size).isEqualTo(2)
        val visitArr = visits.toTypedArray()
        assertThat(visitArr[0].pet).isNotNull
        assertThat(visitArr[0].date).isNotNull()
        assertThat(visitArr[0].pet?.id).isEqualTo(7)
    }

    @Test
    fun shouldFindAllPets() {
        val pets = clinicService.findAllPets()
        val pet1 = EntityUtils.getById(pets, Pet::class, 1)
        assertThat(pet1.name).isEqualTo("Leo")
        val pet3 = EntityUtils.getById(pets, Pet::class, 3)
        assertThat(pet3.name).isEqualTo("Rosy")
    }

    @Test
    @Transactional
    fun shouldDeletePet() {
        val pet = clinicService.findPetById(1)!!
        clinicService.deletePet(pet)
        val petActual = clinicService.findPetById(1)

        assertThat(petActual).isNull()
    }

    @Test
    fun shouldFindVisitDyId() {
        val visit = clinicService.findVisitById(1)
        assertThat(visit?.id).isEqualTo(1)
        assertThat(visit?.pet?.name).isEqualTo("Samantha")
    }

    @Test
    fun shouldFindAllVisits() {
        val visits = clinicService.findAllVisits()
        val visit1 = EntityUtils.getById(visits, Visit::class, 1)
        assertThat(visit1.pet?.name).isEqualTo("Samantha")
        val visit3 = EntityUtils.getById(visits, Visit::class, 3)
        assertThat(visit3.pet?.name).isEqualTo("Max")
    }

    @Test
    @Transactional
    fun shouldInsertVisit() {
        val visits = clinicService.findAllVisits()
        val found = visits.size

        val pet = clinicService.findPetById(1)!!

        val visit = Visit()
        visit.pet = pet
        visit.date = Date()
        visit.description = "new visit"


        clinicService.saveVisit(visit)
        assertThat(visit.id).isNotNull()

        val visitsActual = clinicService.findAllVisits()
        assertThat(visitsActual.size).isEqualTo(found + 1)
    }

    @Test
    @Transactional
    fun shouldUpdateVisit() {
        val visit = clinicService.findVisitById(1)!!
        val oldDesc = visit.description
        val newDesc = "${oldDesc}X"
        visit.description = newDesc
        clinicService.saveVisit(visit)
        val visitActual = clinicService.findVisitById(1)
        assertThat(visitActual?.description).isEqualTo(newDesc)
    }

    @Test
    @Transactional
    fun shouldDeleteVisit() {
        val visit = clinicService.findVisitById(1)!!
        clinicService.deleteVisit(visit)
        val visitActual = clinicService.findVisitById(1)

        assertThat(visitActual).isNull()
    }

    @Test
    fun shouldFindVetDyId() {
        val vet = clinicService.findVetById(1)
        assertThat(vet?.firstName).isEqualTo("James")
        assertThat(vet?.lastName).isEqualTo("Carter")
    }

    @Test
    @Transactional
    fun shouldInsertVet() {
        val vets = clinicService.findAllVets()
        val found = vets.size

        val vet = Vet()
        vet.firstName = "John"
        vet.lastName = "Dow"

        clinicService.saveVet(vet)
        assertThat(vet.id).isNotEqualTo(null)

        val vetsActual = clinicService.findAllVets()
        assertThat(vetsActual.size).isEqualTo(found + 1)
    }

    @Test
    @Transactional
    fun shouldUpdateVet() {
        val vet = clinicService.findVetById(1)!!
        val oldLastName = vet.lastName
        val newLastName = oldLastName + "X"
        vet.lastName = newLastName
        clinicService.saveVet(vet)

        val vetActual = clinicService.findVetById(1)
        assertThat(vetActual?.lastName).isEqualTo(newLastName)
    }

    @Test
    @Transactional
    fun shouldDeleteVet() {
        val vet = clinicService.findVetById(1)!!
        clinicService.deleteVet(vet)

        val vetActual = clinicService.findVetById(1)
        assertThat(vetActual).isNull()
    }

    @Test
    fun shouldFindAllOwners() {
        val owners = clinicService.findAllOwners()
        val owner1 = EntityUtils.getById(owners, Owner::class, 1)
        assertThat(owner1.firstName).isEqualTo("George")
        val owner3 = EntityUtils.getById(owners, Owner::class, 3)
        assertThat(owner3.firstName).isEqualTo("Eduardo")
    }

    @Test
    @Transactional
    fun shouldDeleteOwner() {
        val owner = clinicService.findOwnerById(1)!!
        clinicService.deleteOwner(owner)

        val ownerActual = clinicService.findOwnerById(1)
        assertThat(ownerActual).isNull()
    }

    @Test
    fun shouldFindPetTypeById() {
        val petType = clinicService.findPetTypeById(1)
        assertThat(petType?.name).isEqualTo("cat")
    }

    @Test
    fun shouldFindAllPetTypes() {
        val petTypes = clinicService.findAllPetTypes()
        val petType1 = EntityUtils.getById(petTypes, PetType::class, 1)
        assertThat(petType1.name).isEqualTo("cat")
        val petType3 = EntityUtils.getById(petTypes, PetType::class, 3)
        assertThat(petType3.name).isEqualTo("lizard")
    }

    @Test
    @Transactional
    fun shouldInsertPetType() {
        val petTypes = clinicService.findAllPetTypes()
        val found = petTypes.size

        val petType = PetType()
        petType.name = "tiger"

        clinicService.savePetType(petType)
        assertThat(petType.id).isNotNull()

        val petTypesActual = clinicService.findAllPetTypes()
        assertThat(petTypesActual.size).isEqualTo(found + 1)
    }

    @Test
    @Transactional
    fun shouldUpdatePetType() {
        val petType = clinicService.findPetTypeById(1)!!
        val oldLastName = petType.name
        val newLastName = "${oldLastName}X"
        petType.name = newLastName

        clinicService.savePetType(petType)

        val petTypeActual = clinicService.findPetTypeById(1)
        assertThat(petTypeActual?.name).isEqualTo(newLastName)
    }

    @Test
    @Transactional
    fun shouldDeletePetType() {
        val petType = clinicService.findPetTypeById(1)!!
        clinicService.deletePetType(petType)

        val petTypeActual = clinicService.findPetTypeById(1)
        assertThat(petTypeActual).isNull()
    }

    @Test
    fun shouldFindSpecialtyById() {
        val specialty = clinicService.findSpecialtyById(1)
        assertThat(specialty?.name).isEqualTo("radiology")
    }

    @Test
    fun shouldFindAllSpecialtys() {
        val specialties = clinicService.findAllSpecialties()
        val specialty1 = EntityUtils.getById(specialties, Specialty::class, 1)
        assertThat(specialty1.name).isEqualTo("radiology")
        val specialty3 = EntityUtils.getById(specialties, Specialty::class, 3)
        assertThat(specialty3.name).isEqualTo("dentistry")
    }

    @Test
    @Transactional
    fun shouldInsertSpecialty() {
        val specialties: Collection<Specialty> = clinicService.findAllSpecialties()
        val found = specialties.size

        val specialty = Specialty()
        specialty.name = "dermatologist"

        clinicService.saveSpecialty(specialty)
        assertThat(specialty.id).isNotEqualTo(null)

        val specialtiesActual = clinicService.findAllSpecialties()
        assertThat(specialtiesActual.size).isEqualTo(found + 1)
    }

    @Test
    @Transactional
    fun shouldUpdateSpecialty() {
        val specialty = clinicService.findSpecialtyById(1)!!
        val oldLastName = specialty.name
        val newLastName = oldLastName + "X"
        specialty.name = newLastName
        clinicService.saveSpecialty(specialty)
        val newSpecialty = clinicService.findSpecialtyById(1)!!
        assertThat(newSpecialty.name).isEqualTo(newLastName)
    }

    @Test
    @Transactional
    fun shouldDeleteSpecialty() {

        val specialty = clinicService.findSpecialtyById(1)!!
        clinicService.deleteSpecialty(specialty)

        val postDelete = clinicService.findSpecialtyById(1)
        assertThat(postDelete).isNull()
    }
}

