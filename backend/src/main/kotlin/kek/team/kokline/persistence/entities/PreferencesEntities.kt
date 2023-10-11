package kek.team.kokline.persistence.entities

import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Column

object PreferencesTable : LongIdTable("user_preferences") {
    val featureName: Column<String> = text("feature_name")
    val resourceId: Column<Long> = long("resource_id")
    val customerId: Column<EntityID<Long>> = reference("customer_id", UserTable)
}

sealed class BasePreferenceEntity<T : Entity<Long>>(id: EntityID<Long>) : LongEntity(id) {
    val featureName: String by PreferencesTable.featureName
    val customer: UserEntity by UserEntity referencedOn PreferencesTable.customerId
    abstract val resource: T

    companion object : LongEntityClass<BasePreferenceEntity<out Entity<Long>>>(PreferencesTable)
}

class ChatPreferencesEntity(id: EntityID<Long>) : BasePreferenceEntity<ChatEntity>(id) {
    override val resource: ChatEntity by ChatEntity referencedOn PreferencesTable.resourceId

    companion object : LongEntityClass<ChatPreferencesEntity>(PreferencesTable)
}

class MessagePreferencesEntity(id: EntityID<Long>) : BasePreferenceEntity<MessageEntity>(id) {
    override val resource: MessageEntity by MessageEntity referencedOn PreferencesTable.resourceId

    companion object : LongEntityClass<MessagePreferencesEntity>(PreferencesTable)
}
