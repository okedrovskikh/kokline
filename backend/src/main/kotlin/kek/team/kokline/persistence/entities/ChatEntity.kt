package kek.team.kokline.persistence.entities

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.SizedIterable

object ChatTable : LongIdTable("chat") {
    val name: Column<String> = text("name")
    val previousChatId: Column<EntityID<Long>?> = reference("previous-chat-id", ChatTable).nullable()
    val avatarUrl: Column<String?> = text("avatarUrl").nullable()
}

class ChatEntity(id: EntityID<Long>) : LongEntity(id) {
    var name: String by ChatTable.name
    var avatarUrl: String? by ChatTable.avatarUrl
    val messages: SizedIterable<MessageEntity> by MessageEntity referrersOn MessageTable.chatId
    var users: SizedIterable<UserEntity> by UserEntity via ChatUsersTable
    var previousChat: ChatEntity? by ChatEntity optionalReferencedOn ChatTable.previousChatId

    companion object : LongEntityClass<ChatEntity>(ChatTable)
}
