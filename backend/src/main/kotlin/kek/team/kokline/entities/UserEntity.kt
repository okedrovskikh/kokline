package kek.team.kokline.entities

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Column

object UserTable : LongIdTable("kokline_user") {
    val nickname: Column<String> = text("nickname")
}

class UserEntity(id: EntityID<Long>): LongEntity(id) {
    var nickname: String by UserTable.nickname

    companion object : LongEntityClass<UserEntity>(UserTable)
}
