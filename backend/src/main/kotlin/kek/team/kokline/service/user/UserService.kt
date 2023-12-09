package kek.team.kokline.service.user

import kek.team.kokline.exceptions.NotFoundException
import kek.team.kokline.factories.dbQuery
import kek.team.kokline.mappers.UserMapper
import kek.team.kokline.models.PreferenceDescription
import kek.team.kokline.models.User
import kek.team.kokline.models.UserCreateRequest
import kek.team.kokline.models.UserEditRequest
import kek.team.kokline.persistence.repositories.UserRepository
import kek.team.kokline.redis.events.Events
import kek.team.kokline.redis.publisher.MessagePublisher
import kek.team.kokline.security.actions.Actions.USER_EDIT
import kek.team.kokline.security.actions.Actions.USER_DELETE
import kek.team.kokline.service.security.PreferencesService

class UserService(
    private val repository: UserRepository,
    private val mapper: UserMapper,
    private val preferencesService: PreferencesService,
) {

    suspend fun create(request: UserCreateRequest): User = dbQuery {
        repository.create(request.nickname, request.credits.encodeToByteArray(), request.name, request.avatarUrl).let(mapper::mapToUser).also {
            val preferences = listOf(
                PreferenceDescription(USER_EDIT.actionName, listOf(it.id), listOf(it.id)),
                PreferenceDescription(USER_DELETE.actionName, listOf(it.id), listOf(it.id))
            )
            preferencesService.createAll(preferences)
        }
    }

    suspend fun getById(id: Long): User = dbQuery { repository.findById(id)?.let(mapper::mapToUserWithChats) }
        ?: throw NotFoundException("Not found user with id: $id")

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
