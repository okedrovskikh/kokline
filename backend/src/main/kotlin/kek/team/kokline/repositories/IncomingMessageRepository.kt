package kek.team.kokline.repositories

import kek.team.kokline.entities.IncomingMessageEntity
import kek.team.kokline.entities.MessageEntity
import kek.team.kokline.factories.dbQuery

class IncomingMessageRepository {

    suspend fun create(entity: MessageEntity): IncomingMessageEntity = dbQuery { IncomingMessageEntity.new { message = entity } }
}
