package kek.team.kokline.routing.api

import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import kek.team.kokline.repositories.ChatRepository

private val repository = ChatRepository()

fun Route.chatRouting() {
    route("/chats") {
        post("") {
            // api для создания чатов
        }
        get("{id?}") {
            // api для получения данных по чату на фронте
        }
        put("{id?}") {
            // как будто лучше сделать отдельные ручки для изменения названия чача, удаления и добавления пользователей
        }
        delete("{id?}") {
            // api для удаления чата
        }
    }
}
