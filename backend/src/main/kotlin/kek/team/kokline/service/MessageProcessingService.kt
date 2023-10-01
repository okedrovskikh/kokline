package kek.team.kokline.service

import com.fasterxml.jackson.databind.ObjectMapper
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import kek.team.kokline.models.Message
import kek.team.kokline.persistence.repositories.IncomingMessageRepository
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class MessageProcessingService(
    private val objectMapper: ObjectMapper,
    private val repository: IncomingMessageRepository,
    consumer: IncomingMessageConsumer,
) {

    private val consumer: Processor<Unit, String> = createProcessor { consumer.receiveEvent() }

    suspend fun startProcessing(id: Long, call: WebSocketSession) = coroutineScope {
        val sender: Processor<Message?, Unit> = createProcessor {
            if (it != null) this.launch { call.send(Frame.Text(objectMapper.writeValueAsString(call))) }
        }
        val mapper: Processor<String, Message?> = createProcessor {
            repository.findById(it.toLong())
                ?.let { Message(it.id.value, it.message.payload, it.message.chat.id.value) }
                ?.takeIf { it.chatId == id }
        }
        consumer.after(mapper).after(sender).after(consumer)
    }
}
