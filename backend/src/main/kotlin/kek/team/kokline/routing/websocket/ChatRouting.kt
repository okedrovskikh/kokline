package kek.team.kokline.routing.websocket

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.readText

fun Route.chatRouting() {
    webSocket("/{id?}") {
        val id = call.parameters["id"]?.toLongOrNull() ?: return@webSocket call.respondText(
            text = "Missing or invalid id",
            status = HttpStatusCode.BadRequest
        )
        send(Frame.Text("You are connected!"))
        for (frame in incoming) {
            frame as? Frame.Text ?: continue
            val receivedText = frame.readText()
            send(Frame.Text("You said: $receivedText"))
        }
    }
}
