package kek.team.kokline.service

import com.fasterxml.jackson.databind.ObjectMapper
import kek.team.kokline.coroutines.ChatSessionContext
import kek.team.kokline.models.MessageCreateRequest
import kek.team.kokline.models.WebSocketMessageRequest
import kek.team.kokline.redis.publisher.MessagePublisher
import kek.team.kokline.support.utils.RedisChannels
import kotlinx.coroutines.coroutineScope
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class LiveChatService(
    private val messageService: MessageService,
    private val objectMapper: ObjectMapper,
) {

    /**
     * Для работы сервиса необходимо явно инициализировать ChatSessionContext (в дальнейшем его инициализация будет вынесена на этап авторизации)
     */
    suspend fun processMessage(request: WebSocketMessageRequest): Unit = coroutineScope {
        // TODO подумать как можно упростить использование сессий
        val session = requireNotNull(coroutineContext[ChatSessionContext.Key]).session
        println(session)
        // TODO протестировать как работают транзакции (если будет ошибка при publish откатим ли сообщение?)
        newSuspendedTransaction {
            val message = messageService.create(MessageCreateRequest(request.payload.text, session.chatId))
            MessagePublisher.publish(objectMapper.writeValueAsString(message), RedisChannels.MESSAGES.channelName)
        }
    }
}
