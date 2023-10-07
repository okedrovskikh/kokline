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
import kek.team.kokline.coroutines.coroutinePool
import kek.team.kokline.models.WebSocketMessageRequest
import kek.team.kokline.coroutines.ChatSession
import kek.team.kokline.coroutines.ChatSessionContext
import kek.team.kokline.service.message.MessageService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import org.koin.ktor.ext.inject
import redis.clients.jedis.JedisPubSub

fun Route.chatRouting() {

    val objectMapper: ObjectMapper by inject<ObjectMapper>()
    val messageService: MessageService by inject<MessageService>()

    webSocket("/joinChat") {
        val chatId = call.request.queryParameters["id"]?.toLongOrNull() ?: return@webSocket close(
            CloseReason(
                code = CloseReason.Codes.CANNOT_ACCEPT,
                message = "Parameter chatId empty ot null"
            )
        )
        val userId = call.request.headers["x-user-id"]?.toLongOrNull() ?: return@webSocket close(
            CloseReason(
                code = CloseReason.Codes.CANNOT_ACCEPT,
                message = "No header x-user-id"
            )
        )

        // TODO подумать как это можно вынести в отдельный класс с использованием сессии
        val pubSub = object : JedisPubSub() {
            override fun onMessage(channel: String?, event: String?) {
                try {
                    if (channel == null || event == null) {
                        // скорее всего этого не будет, но на всякий случай сделано
                        this@chatRouting.environment?.log?.error("Channel or message eq null")
                        return
                    }

                    val ids = event.split(":")

                    if (chatId == ids.first().toLong()) {
                        launch(coroutinePool) {
                            supervisorScope {
                                launch {
                                    val message = messageService.getById(ids[1].toLong())
                                    sendSerialized(message)
                                }
                            }
                        }
                    }
                } catch (_: Throwable) {}
            }
        }

        val publisherJob = CoroutineScope(coroutinePool + ChatSessionContext(ChatSession(chatId, userId))).launch {
            supervisorScope {
                for (frame in incoming) {
                    launch {
                        val request = objectMapper.readValue<WebSocketMessageRequest>(frame.data)
                        messageService.create(request, chatId)
                    }
                }
            }
        }

        val subscriberJob = CoroutineScope(coroutinePool + ChatSessionContext(ChatSession(chatId, userId))).launch {
            RedisFactory.jedisPool.subscribe(pubSub, "events:chat:message:create")
        }

        closeReason.invokeOnCompletion {
            publisherJob.cancel(CancellationException("Connection closed"))
            subscriberJob.cancel(CancellationException("Connection closed"))
            pubSub.unsubscribe()
        }

        closeReason.await()
        listOf(publisherJob, subscriberJob).joinAll()
    }
}
