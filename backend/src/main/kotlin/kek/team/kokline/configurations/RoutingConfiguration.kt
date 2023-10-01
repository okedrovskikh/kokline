package kek.team.kokline.configurations

import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.plugins.openapi.openAPI
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kek.team.kokline.routing.api.apiRouting
import kek.team.kokline.routing.websocket.webSocketRouting

fun Application.configureRouting() {
    routing {
        get("/health") {
            call.respond(mapOf("Status" to "Ok"))
        }
        apiRouting()
        webSocketRouting()
        //openAPI(path="openapi", swaggerFile = "openapi/documentation.yaml")
    }
}
