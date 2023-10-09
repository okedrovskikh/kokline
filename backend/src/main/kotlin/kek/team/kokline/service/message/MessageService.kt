package kek.team.kokline.service.message

import kek.team.kokline.exceptions.NotFoundException
import kek.team.kokline.mappers.MessageMapper
import kek.team.kokline.models.Message
import kek.team.kokline.models.MessageEditRequest
import kek.team.kokline.models.WebSocketMessageCreateRequest
import kek.team.kokline.persistence.repositories.ChatRepository
import kek.team.kokline.persistence.repositories.IncomingMessageRepository
import kek.team.kokline.persistence.repositories.MessageRepository
import kek.team.kokline.redis.publisher.MessagePublisher
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class MessageService(
    private val messageRepository: MessageRepository,
    private val chatRepository: ChatRepository,
    private val incomingMessageRepository: IncomingMessageRepository,
    private val mapper: MessageMapper,
) {
    // TODO протестировать как работают транзакции (если будет ошибка при publish откатим ли сообщение?)
    suspend fun create(request: WebSocketMessageCreateRequest, chatId: Long): Message = newSuspendedTransaction {
        val chat = chatRepository.findById(chatId, true) ?: throw NotFoundException("Not found chat by id: ${chatId}")
        val message = messageRepository.create(request.payload.text, chat)
        chat.users.forEach { incomingMessageRepository.create(message, it) }
        MessagePublisher.publish("${chat.id}:${message.id}", "events:chat:message:create")
        mapper.mapToModel(message)
    }

    suspend fun findAllByChatId(id: Long): List<Message> = messageRepository.findAllByChatId(id).map(mapper::mapToModel)

    suspend fun getById(id: Long): Message = newSuspendedTransaction {
        messageRepository.findById(id)?.let(mapper::mapToModel)
    } ?: throw NotFoundException("Not found message by id: $id")

    suspend fun edit(request: MessageEditRequest): Message? {
        val message = messageRepository.findById(request.id)
        return message?.apply { payload = request.payload }?.let(mapper::mapToModel)
    }

    suspend fun deleteById(id: Long): Boolean = messageRepository.deleteById(id)
}
