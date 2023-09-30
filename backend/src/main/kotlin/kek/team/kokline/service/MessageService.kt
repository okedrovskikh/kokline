package kek.team.kokline.service

import kek.team.kokline.mappers.MessageMapper
import kek.team.kokline.models.Message
import kek.team.kokline.models.MessageCreateRequest
import kek.team.kokline.models.MessageEditRequest
import kek.team.kokline.repositories.IncomingMessageRepository
import kek.team.kokline.repositories.MessageRepository

class MessageService(
    private val mapper: MessageMapper,
    private val messageRepository: MessageRepository,
    private val incomingMessageRepository: IncomingMessageRepository,
) {
    suspend fun create(request: MessageCreateRequest): Message {
        val entity = messageRepository.create(request)
        incomingMessageRepository.create(entity)
        return mapper.mapToModel(entity)
    }

    suspend fun findAllByChatId(id: Long): List<Message> = messageRepository.findAllByChatId(id).map(mapper::mapToModel)

    suspend fun findById(id: Long): Message? = messageRepository.findById(id)?.let(mapper::mapToModel)

    suspend fun edit(request: MessageEditRequest): Message? {
        val message = messageRepository.findById(request.id)
        return message?.apply { payload = request.payload }?.let(mapper::mapToModel)
    }

    suspend fun deleteById(id: Long): Boolean = messageRepository.deleteById(id)
}