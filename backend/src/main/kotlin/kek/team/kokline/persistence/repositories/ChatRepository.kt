package kek.team.kokline.persistence.repositories

import kek.team.kokline.persistence.entities.ChatEntity
import kek.team.kokline.persistence.entities.ChatTable
import kek.team.kokline.factories.dbQuery
import kek.team.kokline.mappers.ChatMapper
import kek.team.kokline.models.Chat
import kek.team.kokline.models.ChatCreateRequest
import kek.team.kokline.models.ChatEditRequest
import kek.team.kokline.persistence.entities.UserEntity
import org.jetbrains.exposed.dao.load
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.update

class ChatRepository {

    suspend fun create(name: String): ChatEntity = dbQuery {
        ChatEntity.new {
            this.name = name
        }
    }

    suspend fun findById(id: Long, loadUser: Boolean = false): ChatEntity? = dbQuery {
        val entity = ChatEntity.findById(id)

        if (loadUser) entity?.load(ChatEntity::users)

        entity
    }

    suspend fun edit(id: Long, name: String): Boolean = dbQuery {
        val updatedRows = ChatTable.update( { ChatTable.id eq id } ) { ChatTable.name eq name }

        if (updatedRows > 1) error("update more than 1 row by id: ${id}")

        updatedRows > 0
    }

    suspend fun deleteById(id: Long): Boolean = dbQuery {
        val deletedRows = ChatTable.deleteWhere { ChatTable.id eq id }

        if (deletedRows > 1) error("Delete more than 1 row by id: $id")

        deletedRows > 0
    }
}
