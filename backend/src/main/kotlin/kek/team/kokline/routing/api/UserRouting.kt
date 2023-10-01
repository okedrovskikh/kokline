package kek.team.kokline.routing.api

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import kek.team.kokline.mappers.UserMapper
import kek.team.kokline.models.UserCreateRequest
import kek.team.kokline.models.UserEditRequest
import kek.team.kokline.persistence.repositories.UserRepository

private val mapper = UserMapper()
private val userRepository = UserRepository(mapper)

fun Route.userRouting() {
    route("/users") {
        post("") {
            val createRequest = call.receive<UserCreateRequest>()
            val user = userRepository.create(createRequest)
            call.respond(HttpStatusCode.Created, user)
        }
        get("") {
            call.respond(userRepository.findAll())
        }
        get("{id?}") {
            val id = call.parameters["id"]?.toLongOrNull() ?: return@get call.respondText(
                text = "Missing or invalid id",
                status = HttpStatusCode.BadRequest
            )
            val userWithChats = userRepository.findByIdWithChats(id) ?: return@get call.respondText(
                text = "No user with id $id",
                status = HttpStatusCode.NotFound
            )
            call.respond(userWithChats)
        }
        put("") {
            val editRequest = call.receive<UserEditRequest>()
            val updated = userRepository.edit(editRequest)
            call.respond(if (updated) HttpStatusCode.Accepted else HttpStatusCode.NotFound)
        }
        delete("{id?}") {
            val id = call.parameters["id"]?.toLongOrNull() ?: return@delete call.respondText(
                text = "Missing or invalid id",
                status = HttpStatusCode.BadRequest
            )
            val deleted = userRepository.deleteById(id)
            call.respond(if (deleted) HttpStatusCode.Accepted else HttpStatusCode.NotFound)
        }
    }
}
