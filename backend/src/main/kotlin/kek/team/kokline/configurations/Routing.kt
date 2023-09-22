package kek.team.kokline.configurations

import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kek.team.kokline.routing.userRouting

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respond("Hello world")
        }
        userRouting()
    }
}