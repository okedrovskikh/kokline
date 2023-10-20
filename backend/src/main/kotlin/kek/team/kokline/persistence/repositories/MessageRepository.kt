package kek.team.kokline.persistence.repositories

import kek.team.kokline.persistence.entities.ChatEntity
import kek.team.kokline.persistence.entities.MessageEntity
import kek.team.kokline.persistence.entities.MessageTable
import kek.team.kokline.factories.dbQuery
import kek.team.kokline.factories.newOrSupportedTransaction
import kek.team.kokline.models.MessagePayload
import org.jetbrains.exposed.sql.SizedIterable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.update

class MessageRepository {

    suspend fun create(payload: MessagePayload, chat: ChatEntity): MessageEntity = newOrSupportedTransaction {
        MessageEntity.new {
            this.payload = payload
            this.chat = chat
        }
    }

    suspend fun findAllByChatId(id: Long): SizedIterable<MessageEntity> = newOrSupportedTransaction {
        MessageEntity.find { MessageTable.chatId eq id }
    }

    suspend fun findById(id: Long): MessageEntity? = newOrSupportedTransaction { MessageEntity.findById(id) }

    suspend fun edit(id: Long, payload: MessagePayload): Boolean = newOrSupportedTransaction {
        val updatedRows = MessageTable.update({ MessageTable.id eq id }) { it[MessageTable.payload] = payload }

        if (updatedRows > 1) error("Updated more than 1 row by id: $id")

        updatedRows > 0
    }

    suspend fun deleteById(id: Long): Boolean = newOrSupportedTransaction { MessageTable.deleteWhere { MessageTable.id eq id } } > 0
}
