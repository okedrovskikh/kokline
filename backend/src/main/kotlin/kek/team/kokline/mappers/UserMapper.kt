package kek.team.kokline.mappers

import kek.team.kokline.entities.UserEntity
import kek.team.kokline.models.User

class UserMapper {
    fun mapToModel(entity: UserEntity): User = User(entity.id.value, entity.nickname)
}
