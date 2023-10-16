package kek.team.kokline.service.chat

import kek.team.kokline.exceptions.NotFoundException
import kek.team.kokline.factories.dbQuery
import kek.team.kokline.mappers.ChatMapper
import kek.team.kokline.models.Chat
import kek.team.kokline.models.ChatCreateRequest
import kek.team.kokline.models.ChatEditRequest
import kek.team.kokline.models.PreferenceDescription
import kek.team.kokline.persistence.entities.UserEntity
import kek.team.kokline.persistence.repositories.ChatRepository
import kek.team.kokline.redis.publisher.MessagePublisher
import kek.team.kokline.service.security.PreferencesService
import kek.team.kokline.support.utils.toSizedCollection

class ChatService(
    private val chatRepository: ChatRepository,
    // private val userRepository: UserRepository,
    private val mapper: ChatMapper,
    private val preferencesService: PreferencesService,
) {

    suspend fun create(userId: Long, request: ChatCreateRequest): Chat = dbQuery {
        // val user = userRepository.findByIdWithChats(userId) ?: error("Not found user by id after auth")
        val participants = (request.users + userId).toSet()
        val chat = chatRepository.create(request.name, participants)
        mapper.mapToModel(chat).also {
            val preferences = listOf(
                PreferenceDescription("chat:read", participants, listOf(requireNotNull(it.id))),
                PreferenceDescription("chat:edit", listOf(userId), listOf(requireNotNull(it.id))),
                PreferenceDescription("chat:delete", listOf(userId), listOf(requireNotNull(it.id)))
            )
            preferencesService.createAll(preferences)
            MessagePublisher.publish(requireNotNull(chat.id).toString(), "events:chat:create")
        }
    }

    suspend fun getById(id: Long): Chat = dbQuery { chatRepository.findById(id)?.let(mapper::mapToModel) }
        ?: throw NotFoundException("Not found chat by id: $id")

    suspend fun edit(ownerId: Long, request: ChatEditRequest): Unit = dbQuery {
        val chat = chatRepository.findById(request.id) ?: throw NotFoundException("Not found chat by id: ${request.id}")

        val chatUsersSet = chat.users.map { it.id.value }.toSet()
        val requestUsersSet = (request.users + ownerId).toSet()

        val removedUsers = chatUsersSet - requestUsersSet
        val addedUsers = requestUsersSet - chatUsersSet

        chat.name = request.name
        chat.users = requestUsersSet.map { UserEntity[it] }.toSizedCollection()

        preferencesService.create(PreferenceDescription("chat:read", addedUsers, listOf(chat.id.value)))
        preferencesService.deleteUserPreference(PreferenceDescription("chat:read", removedUsers, listOf(chat.id.value)))
        MessagePublisher.publish(chat.id.value.toString(), "events:chat:edit")
    }

    suspend fun deleteById(id: Long): Unit = dbQuery {
        val chat = chatRepository.findById(id) ?: throw NotFoundException("Not found chat by id: $id")
        preferencesService.deleteUserPreference(PreferenceDescription("chat:read", chat.users.map { it.id.value }, listOf(chat.id.value)))
        chat.delete()
        MessagePublisher.publish(id.toString(), "events:chat:delete")
    }
}
