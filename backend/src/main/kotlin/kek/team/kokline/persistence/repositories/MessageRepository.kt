package kek.team.kokline.persistence.repositories

import kek.team.kokline.persistence.entities.ChatEntity
import kek.team.kokline.persistence.entities.MessageEntity
import kek.team.kokline.persistence.entities.MessageTable
import kek.team.kokline.factories.dbQuery
import org.jetbrains.exposed.dao.load
import org.jetbrains.exposed.sql.SizedIterable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.update

class MessageRepository {

    suspend fun create(payload: String, chat: ChatEntity): MessageEntity = dbQuery {
        MessageEntity.new {
            this.payload = payload
            this.chat = chat
        }
    }

    suspend fun findAllByChatId(id: Long): SizedIterable<MessageEntity> =
        dbQuery { MessageEntity.find { MessageTable.chat eq id } }

    suspend fun findById(id: Long): MessageEntity? = dbQuery {
        val entity = MessageEntity.findById(id)

        entity
    }

    suspend fun edit(id: Long, payload: String): Boolean = dbQuery {
        val updatedRows = MessageTable.update({ MessageTable.id eq id }) { it[MessageTable.payload] = payload }

        if (updatedRows > 1) error("Updated more than 1 row by id: $id")

        updatedRows > 0
    }

    suspend fun deleteById(id: Long): Boolean = dbQuery { MessageTable.deleteWhere { MessageTable.id eq id } } > 0
}
