package kek.team.kokline.persistence.entities

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.LazySizedCollection
import org.jetbrains.exposed.sql.SizedIterable

object UserTable : LongIdTable("kokline_user") {
    val nickname: Column<String> = text("nickname")
    val credits: Column<ByteArray> = binary("credits")
    val name: Column<String> = text("name")
    val avatarUrl: Column<String?> = text("avatar_url").nullable()

    init {
        uniqueIndex(nickname, credits)
    }
}

class UserEntity(id: EntityID<Long>) : LongEntity(id) {
    var nickname: String by UserTable.nickname
    var credits: ByteArray by UserTable.credits
    val chats: SizedIterable<ChatEntity> by ChatEntity via ChatUsersTable
    var name: String by UserTable.name
    var avatarUrl: String? by UserTable.avatarUrl

    companion object : LongEntityClass<UserEntity>(UserTable)
}
