package kek.team.kokline.routing.api

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import kek.team.kokline.exceptions.BadRequestException
import kek.team.kokline.models.UserCreateRequest
import kek.team.kokline.models.UserEditRequest
import kek.team.kokline.service.user.UserService
import org.koin.ktor.ext.inject

fun Route.userRouting() {

    val service: UserService by inject<UserService>()

    route("/users") {
        post("") {
            val createRequest = call.receive<UserCreateRequest>()
            val user = service.create(createRequest)
            call.respond(HttpStatusCode.Created, user)
        }
        get("{id?}") {
            val id = call.parameters["id"]?.toLongOrNull() ?: throw BadRequestException("Missing or invalid id")
            call.respond(service.getById(id))
        }
        put("") {
            val editRequest = call.receive<UserEditRequest>()
            val updated = service.edit(editRequest)
            call.respond(if (updated) HttpStatusCode.Accepted else HttpStatusCode.NotFound)
        }
        delete("{id?}") {
            val id = call.parameters["id"]?.toLongOrNull() ?: throw BadRequestException("Missing or invalid id")
            val deleted = service.deleteById(id)
            call.respond(if (deleted) HttpStatusCode.Accepted else HttpStatusCode.NotFound)
        }
    }
}
