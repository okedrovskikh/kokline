package kek.team.kokline.mappers

import kek.team.kokline.persistence.entities.UserEntity
import kek.team.kokline.models.User
import kek.team.kokline.models.UserWithChats

class UserMapper {

    fun mapToUser(entity: UserEntity): User = User(entity.id.value, entity.nickname)

    fun mapToUserWithChats(entity: UserEntity): UserWithChats = UserWithChats(
        id = entity.id.value,
        nickname = entity.nickname,
        chats = entity.chats.map { it.id.value }
    )
}
