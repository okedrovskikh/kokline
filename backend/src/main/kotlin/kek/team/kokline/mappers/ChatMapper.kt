package kek.team.kokline.mappers

import kek.team.kokline.persistence.entities.ChatEntity
import kek.team.kokline.models.Chat

class ChatMapper {

    fun mapToModel(entity: ChatEntity): Chat = Chat(entity.id.value, entity.name)
}
