package kek.team.kokline.routing.websocket

import io.ktor.server.routing.Route
import io.ktor.server.routing.route

fun Route.webSocketRouting() {
    route("/chat") {
        chatRouting()
    }
}
