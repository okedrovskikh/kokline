package kek.team.kokline.persistence.entities

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Column

object PreferencesTable : LongIdTable("user_preferences") {
    val action: Column<String> = text("feature_name")
    val resourceId: Column<Long> = long("resource_id")
    val ownerId: Column<EntityID<Long>> = reference("owner_id", UserTable)
}

class PreferenceEntity(id: EntityID<Long>) : LongEntity(id) {
    val action: String by PreferencesTable.action
    val owner: UserEntity by UserEntity referencedOn PreferencesTable.ownerId
    val resourceId: Long by PreferencesTable.resourceId

    companion object : LongEntityClass<PreferenceEntity>(PreferencesTable)
}
