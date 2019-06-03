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

package petclinic.api

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.server.ResponseStatusException

@ControllerAdvice
class ExceptionControllerAdvice {

    @ExceptionHandler(Exception::class)
    fun exception(e: Exception): ResponseEntity<String> {
        return ResponseEntity.badRequest().body(
            try {
                jacksonObjectMapper().writeValueAsString(ErrorInfo(e))
            } catch (e1: JsonProcessingException) {
                e1.printStackTrace()
                "{}"
            }
        )
    }

    private data class ErrorInfo(val className: String, val exMessage: String?) {
        constructor(ex: Exception) : this(ex.javaClass.name, ex.localizedMessage)
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException) =
        ex.bindingResult.allErrors.map {
            (it as FieldError).field to it.defaultMessage
        }.toMap()

    @ExceptionHandler(ResponseStatusException::class)
    fun handleResponseStatusException(ex: ResponseStatusException) =
        ResponseEntity(ex.reason, ex.status)
}
