package kek.team.kokline.service.chat

import kek.team.kokline.exceptions.NotFoundException
import kek.team.kokline.mappers.ChatMapper
import kek.team.kokline.models.Chat
import kek.team.kokline.models.ChatCreateRequest
import kek.team.kokline.models.ChatEditRequest
import kek.team.kokline.persistence.repositories.ChatRepository
import kek.team.kokline.redis.publisher.MessagePublisher

class ChatService(private val repository: ChatRepository, private val mapper: ChatMapper) {

    suspend fun create(request: ChatCreateRequest): Chat = repository.create(request.name).let(mapper::mapToModel).also {
        MessagePublisher.publish(requireNotNull(it.id).toString(), "events:chat:create")
    }

    suspend fun getById(id: Long): Chat = repository.findById(id)?.let(mapper::mapToModel)
        ?: throw NotFoundException("Not found chat by id: $id")

    suspend fun edit(request: ChatEditRequest): Boolean = repository.edit(request.id, request.name).also {
        MessagePublisher.publish(request.id.toString(), "events:chat:edit")
    }

    suspend fun deleteById(id: Long): Boolean = repository.deleteById(id).also {
        MessagePublisher.publish(id.toString(), "events:chat:delete")
    }
}
