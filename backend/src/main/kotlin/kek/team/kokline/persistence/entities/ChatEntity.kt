package kek.team.kokline.persistence.entities

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.SizedIterable

object ChatTable : LongIdTable("chat") {
    val name: Column<String> = text("name")
}

class ChatEntity(id: EntityID<Long>) : LongEntity(id) {
    var name: String by ChatTable.name
    val messages: SizedIterable<MessageEntity> by MessageEntity referrersOn MessageTable.chat
    val users: SizedIterable<UserEntity> by UserEntity via ChatUsersTable

    companion object : LongEntityClass<ChatEntity>(ChatTable)
}
