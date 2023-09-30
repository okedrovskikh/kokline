package kek.team.kokline.mappers

import kek.team.kokline.entities.MessageEntity
import kek.team.kokline.models.Message

class MessageMapper {
    fun mapToModel(entity: MessageEntity): Message = Message(
        id = entity.id.value,
        payload = entity.payload,
        chatId = entity.chat.id.value
    )
}