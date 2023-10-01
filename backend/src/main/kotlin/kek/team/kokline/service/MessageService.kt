package kek.team.kokline.service

import kek.team.kokline.mappers.MessageMapper
import kek.team.kokline.models.Message
import kek.team.kokline.models.MessageCreateRequest
import kek.team.kokline.models.MessageEditRequest
import kek.team.kokline.persistence.repositories.IncomingMessageRepository
import kek.team.kokline.persistence.repositories.MessageRepository
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

class MessageService(
    private val mapper: MessageMapper,
    private val messageRepository: MessageRepository,
    private val incomingMessageRepository: IncomingMessageRepository,
    private val producer: IncomingMessageProducer
) {
    suspend fun create(request: MessageCreateRequest): Message {
        val entity = newSuspendedTransaction {
            val entity = messageRepository.create(request)
            incomingMessageRepository.create(entity)
            producer.sendEvent(entity.chat.id.value.toString())
            entity
        }
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