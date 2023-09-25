package kek.team.kokline.factories

import io.ktor.network.sockets.Connection

object ChatFactory {
    private val chatsConnections: MutableMap<Long, List<Connection>> = mutableMapOf()

    fun getChatConnection(chatId: Long, currentConnection: Connection): List<Connection> {
        TODO()
    }
}