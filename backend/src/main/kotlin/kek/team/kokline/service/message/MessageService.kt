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
    private val repository: MessageRepository,
    private val chatRepository: ChatRepository,
    private val mapper: MessageMapper,
    private val preferencesService: PreferencesService
) {
    suspend fun create(request: WebSocketMessageCreateRequest, chatId: Long, userId: Long): Message = dbQuery {
        val chat = chatRepository.findById(chatId) ?: throw NotFoundException("Not found chat by id: $chatId")
        val message = repository.create(request.payload.text, chat)
        mapper.mapToModel(message).also {
            val preferences = listOf(
                PreferenceDescription("message:edit", listOf(userId), listOf(requireNotNull(it.id))),
                PreferenceDescription("message:delete", listOf(userId), listOf(requireNotNull(it.id)))
            )
            preferencesService.createAll(preferences)
            MessagePublisher.publish("${chat.id}:${message.id}", "events:chat:message:create")
        }
    }

    suspend fun findAllByChatId(id: Long): List<Message> = dbQuery { repository.findAllByChatId(id).map(mapper::mapToModel) }

    suspend fun getById(id: Long): Message = dbQuery {
        repository.findById(id)?.let(mapper::mapToModel)
    } ?: throw NotFoundException("Not found message by id: $id")

    suspend fun edit(request: MessageEditRequest): Unit = dbQuery {
        val message = repository.findById(request.id) ?: throw NotFoundException("Not found message by id: ${request.id}")
        message.payload = request.payload
        MessagePublisher.publish(message.id.value.toString(), "events:message:edit")
    }

    suspend fun deleteById(id: Long): Unit = dbQuery {
        repository.deleteById(id)
        preferencesService.deleteAllPreferencesByResource(id, "message:")
        MessagePublisher.publish(id.toString(), "events:message:delete")
    }
}
