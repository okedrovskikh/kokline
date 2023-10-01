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
import kek.team.kokline.models.ChatCreateRequest
import kek.team.kokline.models.ChatEditRequest
import kek.team.kokline.persistence.repositories.ChatRepository
import org.koin.ktor.ext.inject

fun Route.chatRouting() {

    val repository: ChatRepository by inject<ChatRepository>()

    route("/chats") {
        post("") {
            val chatCreateRequest = call.receive<ChatCreateRequest>()
            val chat = repository.create(chatCreateRequest)
            call.respond(HttpStatusCode.Created, chat)
        }
        get("{id?}") {
            val id = call.parameters["id"]?.toLongOrNull() ?: return@get call.respondText(
                text = "Missing or invalid id",
                status = HttpStatusCode.BadRequest
            )
            val chat = repository.findById(id) ?: return@get call.respondText(
                text = "No chat with id $id",
                status = HttpStatusCode.NotFound
            )
            call.respond(chat)
        }
        put("") {
            val chat = call.receive<ChatEditRequest>()
            val updated = repository.edit(chat)
            call.respond(if (updated) HttpStatusCode.Accepted else HttpStatusCode.NotFound)
        }
        delete("{id?}") {
            val id = call.parameters["id"]?.toLongOrNull() ?: return@delete call.respondText(
                text = "Missing or invalid id",
                status = HttpStatusCode.BadRequest
            )
            val deleted = repository.deleteById(id)
            call.respond(if (deleted) HttpStatusCode.Accepted else HttpStatusCode.NotFound)
        }
    }
}
