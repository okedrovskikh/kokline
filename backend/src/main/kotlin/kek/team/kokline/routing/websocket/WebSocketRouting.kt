package kek.team.kokline.routing.websocket

import io.ktor.server.auth.authenticate
import io.ktor.server.routing.Route
import kek.team.kokline.security.sessions.basicSession

fun Route.webSocketRouting() {
    authenticate(basicSession) {
        chatRouting()
        notifierRouting()
    }
}
