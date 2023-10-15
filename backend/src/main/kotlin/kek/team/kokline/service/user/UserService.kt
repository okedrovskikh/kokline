package kek.team.kokline.service.user

import kek.team.kokline.exceptions.NotFoundException
import kek.team.kokline.factories.dbQuery
import kek.team.kokline.mappers.UserMapper
import kek.team.kokline.models.PreferenceDescription
import kek.team.kokline.models.User
import kek.team.kokline.models.UserCreateRequest
import kek.team.kokline.models.UserEditRequest
import kek.team.kokline.persistence.repositories.UserRepository
import kek.team.kokline.redis.publisher.MessagePublisher
import kek.team.kokline.service.security.PreferencesService

class UserService(
    private val repository: UserRepository,
    private val mapper: UserMapper,
    private val preferencesService: PreferencesService,
) {

    suspend fun create(request: UserCreateRequest): User = dbQuery {
        repository.create(request.nickname, request.credits.encodeToByteArray()).let(mapper::mapToUser).also {
            preferencesService.create(PreferenceDescription("user:edit", listOf(it.id), listOf(it.id)))
            preferencesService.create(PreferenceDescription("user:delete", listOf(it.id), listOf(it.id)))
        }
    }

    suspend fun getById(id: Long): User = dbQuery { repository.findByIdWithChats(id)?.let(mapper::mapToUserWithChats) }
        ?: throw NotFoundException("Not found user with id: $id")

    suspend fun edit(id: Long, request: UserEditRequest): Boolean = repository.edit(id, request.nickname).also {
        MessagePublisher.publish(id.toString(), "events:user:edit")
    }

    suspend fun deleteById(id: Long): Boolean = repository.deleteById(id).also {
        MessagePublisher.publish(id.toString(), "events:user:delete")
        preferencesService.deleteUserPreference(id, id, "user:edit")
        preferencesService.deleteUserPreference(id, id, "user:delete")
    }
}
