package kek.team.kokline.entities

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object MessageEntity : Table("message") {
    val id: Column<Long> = long("id").autoIncrement()
    val chatId: Column<Long> = long("chat_id").references(ChatEntity.id)

    override val primaryKey: PrimaryKey = PrimaryKey(id)
}
