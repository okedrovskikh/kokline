package kek.team.kokline.routing.websocket

import io.ktor.server.auth.authenticate
import io.ktor.server.routing.Route
import io.ktor.server.routing.route

fun Route.webSocketRouting() {
    authenticate("auth-session") {
        route("/chat") {
            chatRouting()
        }
        route("/notifier") {
            notifierRouting()
        }
    }
}
