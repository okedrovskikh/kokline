package kek.team.kokline.service.chat

import kek.team.kokline.exceptions.BadRequestException
import kek.team.kokline.exceptions.NotFoundException
import kek.team.kokline.factories.dbQuery
import kek.team.kokline.mappers.ChatMapper
import kek.team.kokline.models.Chat
import kek.team.kokline.models.ChatCreateRequest
import kek.team.kokline.models.ChatEditRequest
import kek.team.kokline.models.PreferenceDescription
import kek.team.kokline.persistence.entities.UserEntity
import kek.team.kokline.persistence.repositories.ChatRepository
import kek.team.kokline.redis.events.Events
import kek.team.kokline.redis.publisher.MessagePublisher
import kek.team.kokline.security.actions.ActionPrefixes
import kek.team.kokline.security.actions.ActionPrefixes.CHAT
import kek.team.kokline.security.actions.Actions.*
import kek.team.kokline.security.sessions.authSession
import kek.team.kokline.service.security.PreferencesService
import kek.team.kokline.support.utils.toSizedCollection

class ChatService(
    private val chatRepository: ChatRepository,
    private val mapper: ChatMapper,
    private val preferencesService: PreferencesService,
) {

    suspend fun create(userId: Long, request: ChatCreateRequest): Chat = dbQuery {
        val participants = (request.users + userId).toSet()
        val chat = chatRepository.create(request.name, participants, request.avatarUrl)
        val preferences = listOf(
            PreferenceDescription(CHAT_READ.actionName, participants, listOf(requireNotNull(chat.id.value))),
            PreferenceDescription(CHAT_EDIT.actionName, listOf(userId), listOf(requireNotNull(chat.id.value))),
            PreferenceDescription(CHAT_DELETE.actionName, listOf(userId), listOf(requireNotNull(chat.id.value)))
        )
        preferencesService.createAll(preferences)
        val userChatPreferences = preferencesService.findAllUserPreferenceByResource(userId, chat.id.value, CHAT.actionPrefix)
        mapper.mapToModel(chat, userChatPreferences).also {
            MessagePublisher.publish(requireNotNull(it.id).toString(), Events.CHAT_CREATE.eventName)
        }
    }

    suspend fun getById(id: Long): Chat = dbQuery {
        val userChatPreferences = preferencesService.findAllUserPreferenceByResource(authSession().id, id, CHAT.actionPrefix)
        chatRepository.findById(id)?.let { mapper.mapToModel(it, userChatPreferences) }
    } ?: throw NotFoundException("Not found chat by id: $id")

    suspend fun edit(ownerId: Long, chatId: Long, request: ChatEditRequest): Unit = dbQuery {
        val chat = chatRepository.findById(chatId) ?: throw NotFoundException("Not found chat by id: $chatId")

        val chatUsersSet = chat.users.map { it.id.value }.toSet()
        val requestUsersSet = (request.users + ownerId).toSet()

        val removedUsers = chatUsersSet - requestUsersSet
        val addedUsers = requestUsersSet - chatUsersSet

        chat.name = request.name
        chat.users = requestUsersSet.map { UserEntity[it] }.toSizedCollection()

        preferencesService.create(PreferenceDescription(CHAT_READ.actionName, addedUsers, listOf(chat.id.value)))
        preferencesService.deleteUserPreference(
            PreferenceDescription(
                CHAT_READ.actionName,
                removedUsers,
                listOf(chat.id.value)
            )
        )
        MessagePublisher.publish(chat.id.value.toString(), Events.CHAT_EDIT.eventName)
    }

    suspend fun leaveChat(userId: Long, chatId: Long): Unit = dbQuery {
        val chat = chatRepository.findById(chatId) ?: throw NotFoundException("Not found chat by id: $chatId")
        val newUsers = chat.users.toMutableList().apply { this.removeIf { it.id.value == userId } }.toSizedCollection()

        if (newUsers == chat.users) {
            throw BadRequestException("Not member of chat")
        } else {
            chat.users = newUsers
        }
    }

    suspend fun deleteById(id: Long): Unit = dbQuery {
        val chat = chatRepository.findById(id) ?: throw NotFoundException("Not found chat by id: $id")
        preferencesService.deleteAllPreferencesByResource(chat.id.value, CHAT.actionPrefix)
        chat.delete()
        MessagePublisher.publish(id.toString(), Events.CHAT_DELETE.eventName)
    }
}
