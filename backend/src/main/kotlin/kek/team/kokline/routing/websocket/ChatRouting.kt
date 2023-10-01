package kek.team.kokline.routing.websocket

import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.websocket.webSocket
import kek.team.kokline.factories.ChatProcessorFactory
import kek.team.kokline.service.IncomingMessageConsumer

private val consumer = IncomingMessageConsumer()

fun Route.chatRouting() {
    webSocket("/joinChat") {
        val id = call.request.headers["x-id"] ?: call.respond(HttpStatusCode.BadRequest)
        val processor = ChatProcessorFactory.createProcessor({ consumer.receiveEvent() }, { println(if (it == id) it else "Not yours") })
        while (true) {
            processor.process()
        }
    }
}
