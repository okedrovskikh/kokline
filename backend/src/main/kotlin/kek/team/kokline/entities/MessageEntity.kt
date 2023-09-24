package kek.team.kokline.entities

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Column

object MessageTable : LongIdTable("message") {
    val chat: Column<EntityID<Long>> = reference("chat_id", ChatTable)

    // TODO переделать на jsonb, чтоб можно было хранить любой содержимое сообщения
    val payload: Column<String> = text("payload")
}

class MessageEntity(id: EntityID<Long>) : LongEntity(id) {
    var chat by ChatEntity referencedOn MessageTable.chat
    var payload by MessageTable.payload

    companion object : LongEntityClass<MessageEntity>(MessageTable)
}
