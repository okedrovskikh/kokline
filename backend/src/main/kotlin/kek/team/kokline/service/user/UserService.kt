package kek.team.kokline.service.user

import kek.team.kokline.exceptions.NotFoundException
import kek.team.kokline.factories.dbQuery
import kek.team.kokline.mappers.ChatMapper
import kek.team.kokline.mappers.UserMapper
import kek.team.kokline.models.*
import kek.team.kokline.persistence.repositories.ChatRepository
import kek.team.kokline.persistence.repositories.UserRepository
import kek.team.kokline.redis.events.Events
import kek.team.kokline.redis.publisher.MessagePublisher
import kek.team.kokline.security.actions.ActionPrefixes
import kek.team.kokline.security.actions.Actions.USER_DELETE
import kek.team.kokline.security.actions.Actions.USER_EDIT
import kek.team.kokline.service.security.PreferencesService

class UserService(
    private val repository: UserRepository,
    private val chatRepository: ChatRepository,
    private val mapper: UserMapper,
    private val chatMapper: ChatMapper,
    private val preferencesService: PreferencesService,
) {

    suspend fun create(request: UserCreateRequest): User = dbQuery {
        repository.create(request.nickname, request.credits.encodeToByteArray(), request.name, request.avatarUrl)
            .let(mapper::mapToUser).also {
                val preferences = listOf(
                    PreferenceDescription(USER_EDIT.actionName, listOf(it.id), listOf(it.id)),
                    PreferenceDescription(USER_DELETE.actionName, listOf(it.id), listOf(it.id))
                )
                preferencesService.createAll(preferences)
            }
    }

    suspend fun getAll(): List<User> = dbQuery { repository.findAll().map(mapper::mapToUserWithChats) }

    suspend fun search(search: String): List<User> =
        dbQuery { repository.search(search).map(mapper::mapToUserWithChats) }

    suspend fun getById(id: Long): User = dbQuery { repository.findById(id)?.let(mapper::mapToUserWithChats) }
        ?: throw NotFoundException("Not found user with id: $id")

    suspend fun getChatsById(id: Long): List<Chat?> = dbQuery {
        repository.findById(id)?.let { user ->
            mapper.mapToUserWithChats(user).chats?.map { chatId ->
                val userChatPreferences = preferencesService.findAllUserPreferenceByResource(id, chatId, ActionPrefixes.CHAT.actionPrefix)
                chatRepository.findById(chatId)?.let { chatMapper.mapToModel(it, userChatPreferences) }
            } ?: throw NotFoundException("Not found chats for user with id: $id")
        }
    } ?: throw NotFoundException("Not found user with id: $id")

    suspend fun edit(id: Long, request: UserEditRequest): Unit = dbQuery {
        repository.edit(id, request.nickname, request.name, request.avatarUrl).also {
            MessagePublisher.publish(id.toString(), Events.USER_EDIT.eventName)
        }
    }

    suspend fun deleteById(id: Long): Unit = dbQuery {
        repository.deleteById(id).also {
            preferencesService.deleteAllUserPreferences(id)
            MessagePublisher.publish(id.toString(), Events.USER_DELETE.eventName)
        }
    }
}
