package kek.team.kokline.routing.api

import io.ktor.server.auth.authenticate
import io.ktor.server.routing.Route
import io.ktor.server.routing.route

fun Route.apiRouting() {
    route("/api") {
        route("/v1") {
            authRouting()
            userRouting()
            chatRouting()
            messageRouting()
        }
    }
}
