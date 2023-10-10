package kek.team.kokline.routing.api

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import io.ktor.server.sessions.sessions
import kek.team.kokline.exceptions.BadRequestException
import kek.team.kokline.models.UserCreateRequest
import kek.team.kokline.models.UserEditRequest
import kek.team.kokline.service.user.UserService
import kek.team.kokline.session.UserSession
import kek.team.kokline.session.userSession
import org.koin.ktor.ext.inject

fun Route.userRouting() {

    val service: UserService by inject<UserService>()

    route("/users") {
        post("") {
            val createRequest = call.receive<UserCreateRequest>()
            val user = service.create(createRequest)
            call.respond(HttpStatusCode.Created, user)
        }
        authenticate("auth-session") {
            get("{id?}") {
                val id = call.parameters["id"]?.toLongOrNull() ?: throw BadRequestException("Missing or invalid id")
                call.respond(service.getById(id))
            }
            put("") {
                val session = call.principal<UserSession>() ?: error("Not found session by id after auth")
                val editRequest = call.receive<UserEditRequest>()
                val updated = service.edit(session.id, editRequest)
                call.respond(if (updated) HttpStatusCode.Accepted else HttpStatusCode.NotFound)
            }
            delete("") {
                val session = call.principal<UserSession>() ?: error("Not found session by id after auth")
                val deleted = service.deleteById(session.id)
                call.sessions.clear(userSession)
                call.respond(if (deleted) HttpStatusCode.Accepted else HttpStatusCode.NotFound)
            }
        }
    }
}
