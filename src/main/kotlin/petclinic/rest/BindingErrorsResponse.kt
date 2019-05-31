/*
 * Copyright 2016 the original author or authors.
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

package petclinic.rest

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.validation.BindingResult
import java.util.ArrayList

/**
 * @author Vitaliy Fedoriv
 */

class BindingErrorsResponse {

    private var bindingErrors: MutableList<BindingError> = ArrayList()

    fun getBindingErrors(): List<BindingError> {
        return bindingErrors
    }

    fun setBindingErrors(bindingErrors: MutableList<BindingError>) {
        this.bindingErrors = bindingErrors
    }

    fun addError(bindingError: BindingError) {
        this.bindingErrors.add(bindingError)
    }

    fun addAllErrors(bindingResult: BindingResult) {
        for (fieldError in bindingResult.fieldErrors) {
            val error = BindingError().apply {
                objectName = fieldError.objectName
                fieldName = fieldError.field
                fieldValue = fieldError.rejectedValue?.toString()
                errorMessage = fieldError.defaultMessage
            }
            addError(error)
        }
    }

    fun toJSON(): String {
        val mapper = ObjectMapper()
        mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY)
        var errorsAsJSON = ""
        try {
            errorsAsJSON = mapper.writeValueAsString(bindingErrors)
        } catch (e: JsonProcessingException) {
            e.printStackTrace()
        }

        return errorsAsJSON
    }

    override fun toString(): String {
        return "BindingErrorsResponse [bindingErrors=$bindingErrors]"
    }

    inner class BindingError {

        var objectName: String = ""
        var fieldName: String = ""
        var fieldValue: String? = null
        var errorMessage: String? = null

        override fun toString(): String {
            return ("BindingError [objectName=$objectName, fieldName=$fieldName, fieldValue=$fieldValue, errorMessage=$errorMessage]")
        }

    }

}
