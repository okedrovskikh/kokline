package kek.team.kokline.persistence.entities

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object ChatUsersTable : Table("chat_users") {
    val chatId: Column<EntityID<Long>> = reference("chat_id", ChatTable, onDelete = ReferenceOption.CASCADE)
    val userId: Column<EntityID<Long>> = reference("user_id", UserTable, onDelete = ReferenceOption.CASCADE)
}
