package kek.team.kokline.service.message

import kek.team.kokline.exceptions.NotFoundException
import kek.team.kokline.factories.dbQuery
import kek.team.kokline.mappers.MessageMapper
import kek.team.kokline.models.Message
import kek.team.kokline.models.MessageEditRequest
import kek.team.kokline.models.PreferenceDescription
import kek.team.kokline.models.WebSocketMessageCreateRequest
import kek.team.kokline.persistence.repositories.ChatRepository
import kek.team.kokline.persistence.repositories.MessageRepository
import kek.team.kokline.redis.publisher.MessagePublisher
import kek.team.kokline.service.security.PreferencesService

class MessageService(
    private val messageRepository: MessageRepository,
    private val chatRepository: ChatRepository,
    private val mapper: MessageMapper,
    private val preferencesService: PreferencesService
) {
    suspend fun create(request: WebSocketMessageCreateRequest, chatId: Long, userId: Long): Message = dbQuery {
        val chat = chatRepository.findById(chatId) ?: throw NotFoundException("Not found chat by id: $chatId")
        val message = messageRepository.create(request.payload.text, chat)
        MessagePublisher.publish("${chat.id}:${message.id}", "events:chat:message:create")
        mapper.mapToModel(message).also {
            preferencesService.create(PreferenceDescription("message:edit", listOf(userId), listOf(requireNotNull(it.id))))
            preferencesService.create(PreferenceDescription("message:delete", listOf(userId), listOf(requireNotNull(it.id))))
        }
    }

    suspend fun findAllByChatId(id: Long): List<Message> = messageRepository.findAllByChatId(id).map(mapper::mapToModel)

    suspend fun getById(id: Long): Message = dbQuery {
        messageRepository.findById(id)?.let(mapper::mapToModel)
    } ?: throw NotFoundException("Not found message by id: $id")

    suspend fun edit(request: MessageEditRequest): Message? {
        val message = messageRepository.findById(request.id)
        return message?.apply { payload = request.payload }?.let(mapper::mapToModel)
    }

    suspend fun deleteById(id: Long): Boolean = messageRepository.deleteById(id)
}
