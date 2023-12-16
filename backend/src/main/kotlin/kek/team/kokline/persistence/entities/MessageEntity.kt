package kek.team.kokline.persistence.entities

import kek.team.kokline.models.MessagePayload
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.json.json
import java.time.LocalDateTime

object MessageTable : LongIdTable("message") {
    val chatId: Column<EntityID<Long>> = reference("chat_id", ChatTable)
    val payload: Column<MessagePayload> = json<MessagePayload>("payload", Json, serializer())
    var timestamp: Column<LocalDateTime> = datetime("timestamp")
    var senderId: Column<EntityID<Long>> = reference("user_id", UserTable)
}

class MessageEntity(id: EntityID<Long>) : LongEntity(id) {
    var chat: ChatEntity by ChatEntity referencedOn MessageTable.chatId
    var payload: MessagePayload by MessageTable.payload
    var timestamp: LocalDateTime by MessageTable.timestamp
    var sender: UserEntity by UserEntity referencedOn MessageTable.senderId

    companion object : LongEntityClass<MessageEntity>(MessageTable)
}
