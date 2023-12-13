package kek.team.kokline.mappers

import kek.team.kokline.persistence.entities.MessageEntity
import kek.team.kokline.models.Message
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class MessageMapper {

    fun mapToModel(entity: MessageEntity): Message = Message(
        id = entity.id.value,
        payload = entity.payload,
        chatId = entity.chat.id.value,
        timestamp = entity.timestamp.truncatedTo(ChronoUnit.SECONDS).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    )
}
