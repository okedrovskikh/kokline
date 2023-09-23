package kek.team.kokline.configurations

import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kek.team.kokline.routing.userRouting

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respond("Hello world")
        }
        userRouting()
        webSocket("/chat") {
            send(Frame.Text("You are connected!"))
            for(frame in incoming) {
                frame as? Frame.Text ?: continue
                val receivedText = frame.readText()
                send(Frame.Text("You said: $receivedText"))
            }
        }
    }
}