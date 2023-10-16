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
import kek.team.kokline.exceptions.BadRequestException
import kek.team.kokline.models.ChatCreateRequest
import kek.team.kokline.models.ChatEditRequest
import kek.team.kokline.service.chat.ChatService
import kek.team.kokline.security.sessions.BasicUserSession
import kek.team.kokline.security.sessions.basicSession
import kek.team.kokline.security.sessions.chatDeleteSession
import kek.team.kokline.security.sessions.chatEditSession
import org.koin.ktor.ext.inject

fun Route.chatRouting() {

    val service: ChatService by inject<ChatService>()

    route("/chats") {
        authenticate(basicSession) {
            post("") {
                val session = call.principal<BasicUserSession>() ?: error("Not found session by id after auth")
                val chatCreateRequest = call.receive<ChatCreateRequest>()
                val chat = service.create(session.id, chatCreateRequest)
                call.respond(HttpStatusCode.Created, chat)
            }
            get("{id?}") {
                val id = call.parameters["id"]?.toLongOrNull() ?: throw BadRequestException("Missing or invalid id")
                val chat = service.getById(id)
                call.respond(chat)
            }
        }
        authenticate(chatEditSession) {
            put("") {
                val session = call.principal<BasicUserSession>() ?: error("Not found session by id after auth")
                val chat = call.receive<ChatEditRequest>()
                service.edit(session.id, chat)
                call.respond(HttpStatusCode.OK)
            }
        }
        authenticate(chatDeleteSession) {
            delete("{id?}") {
                val id = call.parameters["id"]?.toLongOrNull() ?: throw BadRequestException("Missing or invalid id")
                service.deleteById(id)
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}
