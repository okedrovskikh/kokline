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
import kek.team.kokline.repositories.MessageRepository

private val mapper = MessageMapper()
private val messageRepository = MessageRepository(mapper)

fun Route.messageRouting() {
    route("/messages") {
        post("") {
            val messageCreateRequest = call.receive<MessageCreateRequest>()
            val message = messageRepository.create(messageCreateRequest)
            call.respond(HttpStatusCode.Created, message)
        }
        get("/fromChat/{id?}") {
            val id = call.parameters["id"]?.toLongOrNull() ?: return@get call.respondText(
                text = "Missing or invalid id",
                status = HttpStatusCode.BadRequest
            )
            val messages = messageRepository.findAllByChatId(id)
            call.respond(messages)
        }
        get("{id?}") {
            val id = call.parameters["id"]?.toLongOrNull() ?: return@get call.respondText(
                text = "Missing or invalid id",
                status = HttpStatusCode.BadRequest
            )
            val message = messageRepository.findById(id) ?: return@get call.respondText(
                text = "No message with id $id",
                status = HttpStatusCode.NotFound
            )
            call.respond(message)
        }
        put("") {
            val editRequest = call.receive<MessageEditRequest>()
            val updated = messageRepository.edit(editRequest)
            call.respond(if (updated) HttpStatusCode.Accepted else HttpStatusCode.NotFound)
        }
        delete("{id?}") {
            val id = call.parameters["id"]?.toLongOrNull() ?: return@delete call.respondText(
                text = "Missing or invalid id",
                status = HttpStatusCode.BadRequest
            )
            val deleted = messageRepository.deleteById(id)
            call.respond(if (deleted) HttpStatusCode.Accepted else HttpStatusCode.NotFound)
        }
    }
}
