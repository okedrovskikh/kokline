package kek.team.kokline.persistence.repositories

import kek.team.kokline.persistence.entities.IncomingMessageEntity
import kek.team.kokline.persistence.entities.MessageEntity
import kek.team.kokline.factories.dbQuery
import org.jetbrains.exposed.sql.transactions.transaction

class IncomingMessageRepository {

    suspend fun create(entity: MessageEntity): IncomingMessageEntity = dbQuery { IncomingMessageEntity.new { message = entity } }

    fun findById(id: Long): IncomingMessageEntity? = transaction { IncomingMessageEntity.findById(id) }
}
