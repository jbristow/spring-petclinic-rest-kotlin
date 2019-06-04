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
package petclinic.api.owners

import org.springframework.context.annotation.Profile
import org.springframework.data.repository.CrudRepository

/**
 * Repository class for `owners` domain objects All method names are compliant with Spring Data naming
 * conventions so this interface can easily be extended for Spring Data See here: http://static.springsource.org/spring-data/jpa/docs/current/reference/html/jpa.repositories.html#jpa.query-methods.query-creation
 *
 * @author Ken Krebs
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @author Michael Isvy
 * @author Vitaliy Fedoriv
 */
@Profile("spring-data-jpa")
interface OwnerRepository : CrudRepository<Owner, Int> {

    /**
     * Retrieve `owners`s from the data store by last name, returning all owners whose last name *starts*
     * with the given name.
     *
     * @param lastName Value to search for
     * @return a `Collection` of matching `owners`s (or an empty `Collection` if none
     * found)
     */

    fun findByLastName(lastName: String): List<Owner>
}
