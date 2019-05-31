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

package petclinic.util

import org.springframework.orm.ObjectRetrievalFailureException
import petclinic.model.BaseEntity
import kotlin.reflect.KClass

/**
 * Utility methods for handling entities. Separate from the BaseEntity class mainly because of dependency on the
 * ORM-associated ObjectRetrievalFailureException.
 *
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @see BaseEntity
 *
 * @since 29.10.2003
 */
object EntityUtils {

    /**
     * Look up the entity of the given class with the given id in the given collection.
     *
     * @param entities    the collection to search
     * @param entityClass the entity class to look up
     * @param entityId    the entity id to look up
     * @return the found entity
     * @throws ObjectRetrievalFailureException if the entity was not found
     */
    @Throws(ObjectRetrievalFailureException::class)
    fun <T : BaseEntity> getById(entities: Collection<T>, entityClass: Class<T>, entityId: Int): T {
        for (entity in entities) {
            if (entity.id == entityId && entityClass.isInstance(entity)) {
                return entity
            }
        }
        throw ObjectRetrievalFailureException(entityClass, entityId)
    }

    @Throws(ObjectRetrievalFailureException::class)
    fun <T : BaseEntity> getById(entities: Collection<T>, entityClass: KClass<T>, entityId: Int): T {
        for (entity in entities) {
            if (entity.id == entityId && entityClass.isInstance(entity)) {
                return entity
            }
        }
        throw ObjectRetrievalFailureException(entityClass.java, entityId)
    }
}
