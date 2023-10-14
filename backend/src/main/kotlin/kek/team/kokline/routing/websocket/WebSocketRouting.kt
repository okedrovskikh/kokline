package kek.team.kokline.routing.websocket

import io.ktor.server.auth.authenticate
import io.ktor.server.routing.Route
import io.ktor.server.routing.route
import kek.team.kokline.security.sessions.SessionNames.basicSession

fun Route.webSocketRouting() {
    authenticate(basicSession) {
        chatRouting()
        notifierRouting()
    }
}
