package kek.team.kokline.routing.websocket

import io.ktor.server.routing.Route
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.readText

fun Route.chatRouting() {
    webSocket("/join/{id?}") {
        send(Frame.Text("You are connected!"))
        for (frame in incoming) {
            frame as? Frame.Text ?: continue
            val receivedText = frame.readText()
            send(Frame.Text("You said: $receivedText"))
        }
    }
}
