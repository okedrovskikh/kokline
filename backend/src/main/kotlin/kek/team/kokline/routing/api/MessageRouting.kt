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
import kek.team.kokline.exceptions.BadRequestException
import kek.team.kokline.models.MessageEditRequest
import kek.team.kokline.security.sessions.SessionNames.basicSession
import kek.team.kokline.security.sessions.SessionNames.messageDeleteSession
import kek.team.kokline.security.sessions.SessionNames.messageEditSession
import kek.team.kokline.service.message.MessageService
import org.koin.ktor.ext.inject

fun Route.messageRouting() {

    val service: MessageService by inject<MessageService>()

    route("/messages") {
        authenticate(basicSession) {
            get("/fromChat/{id?}") {
                val id = call.parameters["id"]?.toLongOrNull() ?: throw BadRequestException("Missing or invalid id")
                val messages = service.findAllByChatId(id)
                call.respond(messages)
            }
            get("{id?}") {
                val id = call.parameters["id"]?.toLongOrNull() ?: throw BadRequestException("Missing or invalid id")
                val message = service.getById(id)
                call.respond(message)
            }
        }
        authenticate(messageEditSession) {
            put("") {
                val request = call.receive<MessageEditRequest>()
                val message = service.edit(request)

                if (message == null) {
                    call.respond(HttpStatusCode.NotFound, "No message with id ${request.id}")
                } else {
                    call.respond(HttpStatusCode.Accepted, message)
                }
            }
        }
        authenticate(messageDeleteSession) {
            delete("{id?}") {
                val id = call.parameters["id"]?.toLongOrNull() ?: throw BadRequestException("Missing or invalid id")
                val deleted = service.deleteById(id)
                call.respond(if (deleted) HttpStatusCode.Accepted else HttpStatusCode.NotFound)
            }
        }
    }
}
