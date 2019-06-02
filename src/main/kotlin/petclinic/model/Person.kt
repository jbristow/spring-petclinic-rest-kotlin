/*
 * Copyright 2002-2013 the original author or authors.
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
package petclinic.model

import javax.persistence.Column
import javax.persistence.MappedSuperclass
import javax.validation.constraints.NotEmpty

/**
 * Simple JavaBean domain object representing an person.
 *
 * @author Ken Krebs
 */
@MappedSuperclass
open class Person(id: Int? = null, firstName: String?, lastName: String?) : BaseEntity(id) {

    constructor() : this(null, null, null)

    @Column(name = "firstName")
    @get:NotEmpty
    var firstName: String? = null

    @Column(name = "last_name")
    @get:NotEmpty
    var lastName: String? = null

    init {
        this.firstName = firstName
        this.lastName = lastName
    }
}
