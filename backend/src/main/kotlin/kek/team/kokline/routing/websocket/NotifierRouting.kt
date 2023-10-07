package kek.team.kokline.routing.websocket

import io.ktor.server.routing.Route
import io.ktor.server.websocket.webSocket

fun Route.notifierRouting() {
    webSocket("") {
        // контракт
        //----------
        // сущность
        // id
        // операция
    }
}
