package kek.team.kokline.routing.api

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
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
import kek.team.kokline.exceptions.ForbiddenException
import kek.team.kokline.models.ChatCreateRequest
import kek.team.kokline.models.ChatEditRequest
import kek.team.kokline.service.chat.ChatService
import kek.team.kokline.session.UserSession
import kek.team.kokline.session.userSession
import org.koin.ktor.ext.inject

fun Route.chatRouting() {

    val service: ChatService by inject<ChatService>()

    route("/chats") {
        post("") {
            val session = call.principal<UserSession>() ?: error("Not found session by id after auth")
            val chatCreateRequest = call.receive<ChatCreateRequest>()
            val chat = service.create(session.id, chatCreateRequest)
            call.respond(HttpStatusCode.Created, chat)
        }
        get("{id?}") {
            val id = call.parameters["id"]?.toLongOrNull() ?: throw BadRequestException("Missing or invalid id")
            val chat = service.getById(id)
            call.respond(chat)
        }
        put("") {
            val chat = call.receive<ChatEditRequest>()
            val updated = service.edit(chat)
            call.respond(if (updated) HttpStatusCode.Accepted else HttpStatusCode.NotFound)
        }
        delete("{id?}") {
            val id = call.parameters["id"]?.toLongOrNull() ?: throw BadRequestException("Missing or invalid id")
            val deleted = service.deleteById(id)
            call.respond(if (deleted) HttpStatusCode.Accepted else HttpStatusCode.NotFound)
        }
    }
}
