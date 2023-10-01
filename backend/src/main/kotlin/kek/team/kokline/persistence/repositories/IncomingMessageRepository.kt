package kek.team.kokline.persistence.repositories

import kek.team.kokline.persistence.entities.IncomingMessageEntity
import kek.team.kokline.persistence.entities.MessageEntity
import kek.team.kokline.factories.dbQuery

class IncomingMessageRepository {

    suspend fun create(entity: MessageEntity): IncomingMessageEntity = dbQuery { IncomingMessageEntity.new { message = entity } }
}
