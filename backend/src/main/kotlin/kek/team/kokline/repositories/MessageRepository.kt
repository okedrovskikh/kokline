package kek.team.kokline.repositories

import kek.team.kokline.entities.ChatEntity
import kek.team.kokline.entities.ChatTable
import kek.team.kokline.entities.MessageEntity
import kek.team.kokline.entities.MessageTable
import kek.team.kokline.factories.dbQuery
import kek.team.kokline.mappers.MessageMapper
import kek.team.kokline.models.Message
import kek.team.kokline.models.MessageCreateRequest
import kek.team.kokline.models.MessageEditRequest
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.update

class MessageRepository(private val mapper: MessageMapper) {

    suspend fun create(message: MessageCreateRequest): Message = dbQuery {
        MessageEntity.new {
            payload = message.payload
            chat = ChatEntity(EntityID(message.chatId, ChatTable))
        }
    }.let(mapper::mapToModel)

    suspend fun findAllByChatId(id: Long): List<Message> = dbQuery {
        MessageEntity.find { MessageTable.chat eq id }
    }.map(mapper::mapToModel)

    suspend fun findById(id: Long): Message? = dbQuery { MessageEntity.findById(id) }?.let(mapper::mapToModel)

    suspend fun edit(message: MessageEditRequest): Boolean = dbQuery {
        MessageTable.update({ MessageTable.id eq message.id }) { it[payload] = message.payload }
    } > 0

    suspend fun deleteById(id: Long): Boolean = dbQuery { MessageTable.deleteWhere { MessageTable.id eq id } } > 0
}