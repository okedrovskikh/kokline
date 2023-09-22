package kek.team.kokline.routing

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import kek.team.kokline.models.User
import kek.team.kokline.repositories.UserRepository

private val repository = UserRepository()

fun Route.userRouting() {
    route("/user") {
        get("") {
            call.respond(repository.findAll())
        }
        get("{id?}") {
            val id = call.parameters["id"]?.toLongOrNull() ?: return@get call.respondText(
                text = "Missing or invalid id",
                status = HttpStatusCode.BadRequest
            )
            val user = repository.findUser(id) ?: return@get call.respondText(
                text = "No user with id $id",
                status = HttpStatusCode.NotFound
            )
            call.respond(user)
        }
        post("") {
            val user = call.receive<User>()
            repository.createUser(user)
            call.respond(HttpStatusCode.Created)
        }
        delete("{id?}") {
            val id = call.parameters["id"]?.toLongOrNull() ?: return@delete call.respondText(
                text = "Missing or invalid id",
                status = HttpStatusCode.BadRequest
            )
            val deleted = repository.delete(id)
            call.respond(if (deleted) HttpStatusCode.NotFound else HttpStatusCode.Accepted)
        }
    }
}