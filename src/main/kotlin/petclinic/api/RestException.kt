package petclinic.api

open class RestException(message: String) : Throwable(message)


open class RestNotFoundException(type: String, id: Int) : RestException("$type $id not found.")
