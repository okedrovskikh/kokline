package kek.team.kokline.persistence.entities

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.javatime.timestamp
import java.time.Instant

object IncomingMessageTable : LongIdTable("incoming") {
    val message: Column<EntityID<Long>> = reference("message_id", MessageTable)
    val readTime: Column<Instant?> = timestamp("read").nullable()
    val receiverId: Column<EntityID<Long>> = reference("receiver_id", UserTable)
}

class IncomingMessageEntity(id: EntityID<Long>) : LongEntity(id) {
    var message: MessageEntity by MessageEntity referencedOn IncomingMessageTable.message
    private var _readTime: Instant? by IncomingMessageTable.readTime
    var receiver: UserEntity by UserEntity referencedOn IncomingMessageTable.receiverId

    var readTime: Instant?
        set(value) = if (_readTime == null || value == null) _readTime = value else error("Cannot update property, cause it is not null")
        get() = _readTime

    companion object : LongEntityClass<IncomingMessageEntity>(IncomingMessageTable)
}
