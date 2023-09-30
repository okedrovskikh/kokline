package kek.team.kokline.repositories

import kek.team.kokline.entities.ChatEntity
import kek.team.kokline.entities.ChatTable
import kek.team.kokline.factories.dbQuery
import kek.team.kokline.mappers.ChatMapper
import kek.team.kokline.models.Chat
import kek.team.kokline.models.ChatCreateRequest
import kek.team.kokline.models.ChatEditRequest
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.update

class ChatRepository(private val mapper: ChatMapper) {
    suspend fun create(chat: ChatCreateRequest): Chat = dbQuery { ChatEntity.new { name = chat.name } }.let(mapper::mapToModel)

    suspend fun findById(id: Long): Chat? = dbQuery { ChatEntity.findById(id) }?.let(mapper::mapToModel)

    suspend fun edit(chat: ChatEditRequest): Boolean = dbQuery { ChatTable.update { name eq chat.name } } > 0

    suspend fun deleteById(id: Long): Boolean = dbQuery { ChatTable.deleteWhere { ChatTable.id eq id } } > 0
}
