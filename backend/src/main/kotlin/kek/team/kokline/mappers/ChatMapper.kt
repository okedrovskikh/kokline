package kek.team.kokline.mappers

import kek.team.kokline.persistence.entities.ChatEntity
import kek.team.kokline.models.Chat
import kek.team.kokline.models.PreferenceDescription
import kek.team.kokline.security.actions.Actions

class ChatMapper {

    fun mapToModel(entity: ChatEntity, userPreferences: List<PreferenceDescription>): Chat =
        Chat(
            id = entity.id.value,
            name = entity.name,
            users = entity.users.map { userEntity -> userEntity.id.value },
            avatarUrl = entity.avatarUrl,
            userPreferences = userPreferences.map { Actions.getFromString(it.action) }
        )
}
