package kek.team.kokline.mappers

import kek.team.kokline.persistence.entities.UserEntity
import kek.team.kokline.models.User

class UserMapper {

    fun mapToUser(entity: UserEntity): User = User(entity.id.value, entity.nickname)

    fun mapToUserWithChats(entity: UserEntity): User = User(
        id = entity.id.value,
        nickname = entity.nickname,
        chats = entity.chats.map { it.id.value }
    )
}
