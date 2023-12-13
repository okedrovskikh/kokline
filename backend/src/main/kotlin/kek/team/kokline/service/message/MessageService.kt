package kek.team.kokline.service.message

import com.google.gson.internal.bind.util.ISO8601Utils
import kek.team.kokline.exceptions.NotFoundException
import kek.team.kokline.factories.dbQuery
import kek.team.kokline.mappers.MessageMapper
import kek.team.kokline.models.Message
import kek.team.kokline.models.MessageEditRequest
import kek.team.kokline.models.PreferenceDescription
import kek.team.kokline.models.WebSocketMessageCreateRequest
import kek.team.kokline.persistence.repositories.ChatRepository
import kek.team.kokline.persistence.repositories.MessageRepository
import kek.team.kokline.redis.events.Events
import kek.team.kokline.redis.publisher.MessagePublisher
import kek.team.kokline.security.actions.ActionPrefixes.MESSAGE
import kek.team.kokline.security.actions.Actions.MESSAGE_DELETE
import kek.team.kokline.security.actions.Actions.MESSAGE_EDIT
import kek.team.kokline.service.security.PreferencesService
import java.time.Instant
import java.util.*

class MessageService(
    private val repository: MessageRepository,
    private val chatRepository: ChatRepository,
    private val mapper: MessageMapper,
    private val preferencesService: PreferencesService
) {
    suspend fun create(request: WebSocketMessageCreateRequest, chatId: Long, userId: Long): Message = dbQuery {
        val chat = chatRepository.findById(chatId) ?: throw NotFoundException("Not found chat by id: $chatId")
        val message = repository.create(request.payload, chat, ISO8601Utils.format(Date.from(Instant.now())))
        mapper.mapToModel(message).also {
            val preferences = listOf(
                PreferenceDescription(MESSAGE_EDIT.actionName, listOf(userId), listOf(requireNotNull(it.id))),
                PreferenceDescription(MESSAGE_DELETE.actionName, listOf(userId), listOf(requireNotNull(it.id)))
            )
            preferencesService.createAll(preferences)
            MessagePublisher.publish("${chat.id}:${message.id}", Events.CHAT_MESSAGE_CREATE.eventName)
        }
    }

    suspend fun findAllByChatId(id: Long): List<Message> =
        dbQuery { repository.findAllByChatId(id).map(mapper::mapToModel) }

    suspend fun findPage(chatId: Long, currentPage: Long, pageSize: Int): List<Message> = dbQuery {
        repository.findPageByChatId(chatId, currentPage, pageSize).map(mapper::mapToModel)
    }

    suspend fun getById(id: Long): Message = dbQuery {
        repository.findById(id)?.let(mapper::mapToModel)
    } ?: throw NotFoundException("Not found message by id: $id")

    suspend fun edit(request: MessageEditRequest): Unit = dbQuery {
        val message =
            repository.findById(request.id) ?: throw NotFoundException("Not found message by id: ${request.id}")
        message.payload = request.payload
        MessagePublisher.publish(message.id.value.toString(), Events.MESSAGE_EDIT.eventName)
    }

    suspend fun deleteById(id: Long): Unit = dbQuery {
        repository.deleteById(id)
        preferencesService.deleteAllPreferencesByResource(id, MESSAGE.actionPrefix)
        MessagePublisher.publish(id.toString(), Events.MESSAGE_DELETE.eventName)
    }
}
