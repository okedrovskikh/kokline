package kek.team.kokline.persistence.repositories

import kek.team.kokline.persistence.entities.ChatEntity
import kek.team.kokline.persistence.entities.ChatTable
import kek.team.kokline.persistence.entities.MessageEntity
import kek.team.kokline.persistence.entities.MessageTable
import kek.team.kokline.factories.dbQuery
import kek.team.kokline.models.MessageCreateRequest
import kek.team.kokline.models.MessageEditRequest
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.load
import org.jetbrains.exposed.sql.SizedIterable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.update

class MessageRepository {

    suspend fun create(message: MessageCreateRequest): MessageEntity = dbQuery {
        MessageEntity.new {
            payload = message.payload
            chat = ChatEntity[message.chatId]
        }
    }

    suspend fun findAllByChatId(id: Long): SizedIterable<MessageEntity> = dbQuery { MessageEntity.find { MessageTable.chat eq id } }

    suspend fun findById(id: Long): MessageEntity? = dbQuery { MessageEntity.findById(id) }

    suspend fun edit(message: MessageEditRequest): Boolean = dbQuery {
        MessageTable.update({ MessageTable.id eq message.id }) { it[payload] = message.payload }
    } > 0

    suspend fun deleteById(id: Long): Boolean = dbQuery { MessageTable.deleteWhere { MessageTable.id eq id } } > 0
}
