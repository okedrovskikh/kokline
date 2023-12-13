package kek.team.kokline.persistence.entities

import kek.team.kokline.models.MessagePayload
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.json.json

object MessageTable : LongIdTable("message") {
    val chatId: Column<EntityID<Long>> = reference("chat_id", ChatTable)
    val payload: Column<MessagePayload> = json<MessagePayload>("payload", Json, serializer())
    var timestamp: Column<String> = text("timestamp")
}

class MessageEntity(id: EntityID<Long>) : LongEntity(id) {
    var chat: ChatEntity by ChatEntity referencedOn MessageTable.chatId
    var payload: MessagePayload by MessageTable.payload
    var timestamp: String by MessageTable.timestamp

    companion object : LongEntityClass<MessageEntity>(MessageTable)
}
