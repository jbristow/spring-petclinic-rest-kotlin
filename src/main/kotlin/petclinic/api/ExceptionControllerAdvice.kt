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

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ExceptionControllerAdvice {

    val log: Logger = LoggerFactory.getLogger(this::class.java)

    @ExceptionHandler(RestNotFoundException::class)
    fun handleNotFound(ex: RestNotFoundException): ResponseEntity<String> {
        log.error("handling a not found... $ex")
        return ResponseEntity.notFound().build()
    }

    data class ValidationMessage(val field: String, val message: String)

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException): ResponseEntity<List<ValidationMessage>> =
        ResponseEntity.badRequest()
            .body(
                ex.bindingResult.allErrors.map {
                    ValidationMessage((it as FieldError).field, it.defaultMessage ?: "")
                }
            )
}
