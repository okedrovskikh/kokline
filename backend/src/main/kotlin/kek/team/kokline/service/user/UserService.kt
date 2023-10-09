package kek.team.kokline.service.user

import kek.team.kokline.exceptions.NotFoundException
import kek.team.kokline.mappers.UserMapper
import kek.team.kokline.models.User
import kek.team.kokline.models.UserCreateRequest
import kek.team.kokline.models.UserEditRequest
import kek.team.kokline.persistence.repositories.UserRepository
import kek.team.kokline.redis.publisher.MessagePublisher

class UserService(private val repository: UserRepository, private val mapper: UserMapper) {

    suspend fun create(request: UserCreateRequest): User {
        return repository.create(request.nickname, request.credits.encodeToByteArray()).let(mapper::mapToUser)
    }

    suspend fun getById(id: Long): User = repository.findByIdWithChats(id)?.let(mapper::mapToUserWithChats)
        ?: throw NotFoundException("Not found user with id: $id")

    suspend fun edit(request: UserEditRequest): Boolean = repository.edit(request.id, request.nickname).also {
        MessagePublisher.publish(request.id.toString(), "events:user:edit")
    }

    suspend fun deleteById(id: Long): Boolean = repository.deleteById(id).also {
        MessagePublisher.publish(id.toString(), "events:user:delete")
    }
}
