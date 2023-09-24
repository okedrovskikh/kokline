package kek.team.kokline.entities

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object ChatEntity : Table("chat") {
    val id: Column<Long> = long("id").autoIncrement()

    override val primaryKey: PrimaryKey = PrimaryKey(id)
}
