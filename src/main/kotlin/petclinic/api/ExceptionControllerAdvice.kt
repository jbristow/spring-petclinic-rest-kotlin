package petclinic.api

import com.fasterxml.jackson.databind.JsonMappingException
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ExceptionControllerAdvice {

    @ExceptionHandler(RestNotFoundException::class)
    fun handleNotFound(ex: RestNotFoundException): ResponseEntity<Unit> = ResponseEntity.notFound().build()

    data class ValidationMessage(val field: String, val message: String)

    @ExceptionHandler(JsonMappingException::class)
    fun handleBadJson(ex: JsonMappingException): ResponseEntity<String> {

        return ResponseEntity.badRequest().body(ex.message)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException): ResponseEntity<List<ValidationMessage>> =
        ResponseEntity.badRequest()
            .body(
                ex.bindingResult.allErrors.map {
                    ValidationMessage((it as FieldError).field, it.defaultMessage ?: "")
                }
            )
}
