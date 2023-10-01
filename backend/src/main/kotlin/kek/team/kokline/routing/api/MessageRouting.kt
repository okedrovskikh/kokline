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
import kek.team.kokline.mappers.MessageMapper
import kek.team.kokline.models.MessageCreateRequest
import kek.team.kokline.models.MessageEditRequest
import kek.team.kokline.repositories.IncomingMessageRepository
import kek.team.kokline.repositories.MessageRepository
import kek.team.kokline.service.IncomingMessageProducer
import kek.team.kokline.service.MessageService

private val mapper = MessageMapper()
private val messageRepository = MessageRepository()
private val incomingMessageRepository = IncomingMessageRepository()
private val producer = IncomingMessageProducer()
private val service = MessageService(mapper, messageRepository, incomingMessageRepository, producer)

fun Route.messageRouting() {
    route("/messages") {
        post("") {
            val request = call.receive<MessageCreateRequest>()
            val message = service.create(request)
            call.respond(HttpStatusCode.Created, message)
        }
        get("/fromChat/{id?}") {
            val id = call.parameters["id"]?.toLongOrNull() ?: return@get call.respondText(
                text = "Missing or invalid id",
                status = HttpStatusCode.BadRequest
            )
            val messages = service.findAllByChatId(id)
            call.respond(messages)
        }
        get("{id?}") {
            val id = call.parameters["id"]?.toLongOrNull() ?: return@get call.respondText(
                text = "Missing or invalid id",
                status = HttpStatusCode.BadRequest
            )
            val message = service.findById(id) ?: return@get call.respondText(
                text = "No message with id $id",
                status = HttpStatusCode.NotFound
            )
            call.respond(message)
        }
        put("") {
            val request = call.receive<MessageEditRequest>()
            val message = service.edit(request)

            if (message == null) {
                call.respond(HttpStatusCode.NotFound, "No message with id ${request.id}")
            } else {
                call.respond(HttpStatusCode.Accepted, message)
            }
        }
        delete("{id?}") {
            val id = call.parameters["id"]?.toLongOrNull() ?: return@delete call.respondText(
                text = "Missing or invalid id",
                status = HttpStatusCode.BadRequest
            )
            val deleted = service.deleteById(id)
            call.respond(if (deleted) HttpStatusCode.Accepted else HttpStatusCode.NotFound)
        }
    }
}
