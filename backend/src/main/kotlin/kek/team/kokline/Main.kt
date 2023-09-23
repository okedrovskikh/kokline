package kek.team.kokline

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kek.team.kokline.configurations.configureRouting
import kek.team.kokline.configurations.configureSerialization
import kek.team.kokline.configurations.configureSockets
import kek.team.kokline.plugins.hikariPoolPlugin


fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    install(hikariPoolPlugin)
    configureSerialization()
    configureSockets()
    configureRouting()
}
