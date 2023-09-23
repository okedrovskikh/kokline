package kek.team.kokline.entities

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object UserEntity : Table("kokline_user") {
    val id: Column<Long> = long("id").autoIncrement()
    val nickname: Column<String> = text("nickname")

    override val primaryKey: PrimaryKey = PrimaryKey(id)
}