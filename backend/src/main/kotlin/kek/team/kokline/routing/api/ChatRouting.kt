package kek.team.kokline.routing.api

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import kek.team.kokline.models.ChatCreateRequest
import kek.team.kokline.models.ChatEditRequest
import kek.team.kokline.service.chat.ChatService
import kek.team.kokline.security.sessions.basicSession
import kek.team.kokline.security.sessions.chatDeleteSession
import kek.team.kokline.security.sessions.chatEditSession
import kek.team.kokline.security.sessions.authSession
import kek.team.kokline.support.utils.authAndCallMethod
import kek.team.kokline.support.utils.getId
import org.koin.ktor.ext.inject

fun Route.chatRouting() {

    val service: ChatService by inject<ChatService>()

    route("/chats") {
        authenticate(basicSession) {
            authAndCallMethod(::post, "") {
                val chatCreateRequest = call.receive<ChatCreateRequest>()
                val chat = service.create(authSession().id, chatCreateRequest)
                call.respond(HttpStatusCode.Created, chat)
            }
            authAndCallMethod(::get, "/{id?}") {
                val id = call.getId()
                val chat = service.getById(id)
                call.respond(chat)
            }
        }
        authenticate(chatEditSession) {
            authAndCallMethod(::put, "") {
                val chat = call.receive<ChatEditRequest>()
                service.edit(authSession().id, chat)
                call.respond(HttpStatusCode.OK)
            }
        }
        authenticate(chatDeleteSession) {
            authAndCallMethod(::delete, "/{id?}") {
                val id = call.getId()
                service.deleteById(id)
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}
