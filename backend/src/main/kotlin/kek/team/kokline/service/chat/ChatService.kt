package kek.team.kokline.service.chat

import kek.team.kokline.exceptions.NotFoundException
import kek.team.kokline.mappers.ChatMapper
import kek.team.kokline.models.Chat
import kek.team.kokline.models.ChatCreateRequest
import kek.team.kokline.models.ChatEditRequest
import kek.team.kokline.persistence.repositories.ChatRepository
import kek.team.kokline.persistence.repositories.UserRepository
import kek.team.kokline.redis.publisher.MessagePublisher
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class ChatService(private val chatRepository: ChatRepository, private val userRepository: UserRepository, private val mapper: ChatMapper) {

    suspend fun create(userId: Long, request: ChatCreateRequest): Chat {
        val user = userRepository.findByIdWithChats(userId) ?: error("Not found user by id after auth")
        return chatRepository.create(request.name).let(mapper::mapToModel).also {
            MessagePublisher.publish(requireNotNull(it.id).toString(), "events:chat:create")
        }
    }

    suspend fun getById(id: Long): Chat = chatRepository.findById(id)?.let(mapper::mapToModel)
        ?: throw NotFoundException("Not found chat by id: $id")

    suspend fun edit(request: ChatEditRequest): Boolean = chatRepository.edit(request.id, request.name).also {
        if (it) MessagePublisher.publish(request.id.toString(), "events:chat:edit")
    }

    suspend fun deleteById(id: Long): Boolean = chatRepository.deleteById(id).also {
        if (it) MessagePublisher.publish(id.toString(), "events:chat:delete")
    }
}
