package kek.team.kokline.routing.websocket

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.server.routing.Route
import io.ktor.server.websocket.sendSerialized
import io.ktor.server.websocket.webSocket
import io.ktor.utils.io.CancellationException
import io.ktor.websocket.CloseReason
import io.ktor.websocket.close
import kek.team.kokline.factories.RedisFactory
import kek.team.kokline.factories.coroutinePool
import kek.team.kokline.models.Message
import kek.team.kokline.models.MessageCreateRequest
import kek.team.kokline.models.MessagePayload
import kek.team.kokline.service.MessageService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import org.koin.ktor.ext.inject
import redis.clients.jedis.JedisPubSub

private const val MESSAGE_CHANNEL = "message"

fun Route.chatRouting() {

    val messageService: MessageService by inject<MessageService>()
    val objectMapper: ObjectMapper by inject<ObjectMapper>()

    webSocket("/joinChat") {
        val id = call.request.queryParameters["id"]?.toLongOrNull() ?: return@webSocket close(
            CloseReason(
                code = CloseReason.Codes.CANNOT_ACCEPT,
                message = "Parameter chatId empty ot null"
            )
        )

        val pubSub = object : JedisPubSub() {
            override fun onMessage(channel: String?, message: String?) {
                if (channel == null || message == null) {
                    this@chatRouting.environment?.log?.error("Channel or message eq null")
                    return
                }

                val payload = objectMapper.readValue<Message>(message)

                if (payload.chatId == id) {
                    launch(coroutinePool) { sendSerialized(payload) }
                }
            }
        }

        val publisherJob = CoroutineScope(coroutinePool).launch {
            for (frame in incoming) {
                val payload = objectMapper.readValue<MessagePayload>(frame.data)
                val message = messageService.create(MessageCreateRequest(payload.text, id))
                RedisFactory.jedisPool.publish(MESSAGE_CHANNEL, objectMapper.writeValueAsString(message))
            }
        }

        val subscriberJob = CoroutineScope(coroutinePool).launch {
            RedisFactory.jedisPool.subscribe(pubSub, MESSAGE_CHANNEL)
        }

        closeReason.invokeOnCompletion {
            publisherJob.cancel(CancellationException("Connection closed"))
            subscriberJob.cancel(CancellationException("Connection closed"))
        }

        closeReason.await()
        pubSub.unsubscribe()
        listOf(publisherJob, subscriberJob).joinAll()
    }
}
