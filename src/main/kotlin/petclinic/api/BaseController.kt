package petclinic.api

import org.springframework.data.repository.CrudRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.util.UriComponentsBuilder
import petclinic.api.owners.Owner
import petclinic.model.BaseEntity
import petclinic.util.buildEntityUri
import javax.validation.Valid

abstract class BaseController<T : BaseEntity, R : CrudRepository<T, Int>>(
    private val pathElement: String,
    val repository: R
) {

    abstract fun notFoundProvider(id: Int): RestNotFoundException
    abstract fun updateFn(a: T, b: T)

    @GetMapping("/{id}")
    fun get(@PathVariable("id") id: Int): T =
        repository.findById(id).orElseThrow { notFoundProvider(id) }

    @GetMapping
    fun getAll(): Iterable<T> = repository.findAll()

    @PostMapping
    fun add(
        @RequestBody @Valid t: T,
        uriComponentsBuilder: UriComponentsBuilder
    ): ResponseEntity<T> {
        println("*** ADD T: $t")
        val saved = repository.save(t)
        return ResponseEntity.created(uriComponentsBuilder.buildEntityUri(saved, pathElement)).body(saved)
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun update(
        @PathVariable("id") id: Int,
        @RequestBody @Valid t: T
    ): T =
        repository.findById(id)
            .orElseGet { throw Owner.NotFoundException(id) }
            .apply {
                updateFn(this, t)
                repository.save(this)
            }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    open fun delete(@PathVariable("id") id: Int) {
        val t = repository.findById(id)
            .orElseGet { throw Owner.NotFoundException(id) }
        repository.delete(t)
    }
}
