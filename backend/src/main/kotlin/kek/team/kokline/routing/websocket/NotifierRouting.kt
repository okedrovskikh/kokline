package kek.team.kokline.routing.websocket

import io.ktor.server.routing.Route
import io.ktor.server.websocket.webSocket

fun Route.notifierRouting() {
    webSocket("") {
        // надо как нить пулить сообщения новые сообщения из базы и отправлять пользователям
    }
}
