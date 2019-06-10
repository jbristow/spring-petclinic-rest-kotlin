package petclinic.util

import org.springframework.web.util.UriComponentsBuilder
import petclinic.model.BaseEntity
import java.net.URI

fun UriComponentsBuilder.buildEntityUri(entity: BaseEntity, path: String): URI {
    return path("/api/$path/{id}").buildAndExpand(entity.id).toUri()
}
