package kek.team.kokline.routing.api

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import kek.team.kokline.models.MessageEditRequest
import kek.team.kokline.security.sessions.basicSession
import kek.team.kokline.security.sessions.chatReadSession
import kek.team.kokline.security.sessions.messageDeleteSession
import kek.team.kokline.security.sessions.messageEditSession
import kek.team.kokline.service.message.MessageService
import kek.team.kokline.support.utils.authAndCallMethod
import kek.team.kokline.support.utils.getId
import kek.team.kokline.support.utils.mapOrThrowBadRequest
import org.koin.ktor.ext.inject

fun Route.messageRouting() {

    val service: MessageService by inject<MessageService>()

    route("/messages") {
        authenticate(basicSession) {
            authAndCallMethod(::get, "/{id?}") {
                val id = call.getId()
                val message = service.getById(id)
                call.respond(message)
            }
        }
        authenticate(chatReadSession) {
            authAndCallMethod(::get, "/fromChat/{id?}/all") {
                val id = call.getId()
                val messages = service.findAllByChatId(id)
                call.respond(messages)
            }
            authAndCallMethod(::get, "/fromChat/{id?}") {
                val id = call.getId()
                val pageNumber = call.request.rawQueryParameters["page"].mapOrThrowBadRequest { requireNotNull(it).toLong() }
                val pageSize = call.request.rawQueryParameters["pageSize"].mapOrThrowBadRequest { requireNotNull(it).toInt() }
                val messages = service.findPage(id, pageNumber, pageSize)
                call.respond(messages)
            }
        }
        authenticate(messageEditSession) {
            authAndCallMethod(::put, "") {
                val request = call.receive<MessageEditRequest>()
                service.edit(request)
                call.respond(HttpStatusCode.OK)
            }
        }
        authenticate(messageDeleteSession) {
            authAndCallMethod(::delete, "/{id?}") {
                val id = call.getId()
                service.deleteById(id)
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}
