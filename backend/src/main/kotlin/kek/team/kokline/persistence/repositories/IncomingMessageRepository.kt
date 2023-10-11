package kek.team.kokline.persistence.repositories

import kek.team.kokline.persistence.entities.IncomingMessageEntity
import kek.team.kokline.persistence.entities.MessageEntity
import kek.team.kokline.factories.dbQuery
import kek.team.kokline.persistence.entities.UserEntity
import org.jetbrains.exposed.sql.transactions.transaction

class IncomingMessageRepository {

    suspend fun create(message: MessageEntity, user: UserEntity): IncomingMessageEntity = dbQuery {
        IncomingMessageEntity.new {
            this.message = message
            this.receiver = user
        }
    }

    fun findById(id: Long): IncomingMessageEntity? = transaction { IncomingMessageEntity.findById(id) }
}
