package kek.team.kokline.routing.websocket

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.server.auth.principal
import io.ktor.server.routing.Route
import io.ktor.server.routing.route
import io.ktor.server.websocket.sendSerialized
import io.ktor.server.websocket.webSocket
import io.ktor.utils.io.CancellationException
import io.ktor.websocket.CloseReason
import io.ktor.websocket.close
import kek.team.kokline.factories.RedisFactory
import kek.team.kokline.coroutines.coroutinePool
import kek.team.kokline.models.WebSocketMessageCreateRequest
import kek.team.kokline.coroutines.ChatSession
import kek.team.kokline.coroutines.ChatSessionContext
import kek.team.kokline.mappers.ExceptionsMapper
import kek.team.kokline.service.message.MessageService
import kek.team.kokline.security.sessions.BasicUserSession
import kek.team.kokline.support.utils.loggingCoroutineExceptionHandler
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import org.koin.ktor.ext.inject
import redis.clients.jedis.JedisPubSub

private val logger = KotlinLogging.logger { }
private val loggingHandler = loggingCoroutineExceptionHandler(logger)

fun Route.chatRouting() {

    val objectMapper: ObjectMapper by inject<ObjectMapper>()
    val messageService: MessageService by inject<MessageService>()
    val exceptionMapper: ExceptionsMapper by inject<ExceptionsMapper>()

    route("/chat") {
        webSocket("/joinChat/{id?}") {
            val chatId = call.request.queryParameters["id"]?.toLongOrNull() ?: return@webSocket close(
                CloseReason(
                    code = CloseReason.Codes.CANNOT_ACCEPT,
                    message = "Parameter chatId empty ot null"
                )
            )
            val user = call.principal<BasicUserSession>() ?: return@webSocket close(
                CloseReason(
                    code = CloseReason.Codes.CANNOT_ACCEPT,
                    message = "No header x-user-id"
                )
            )
            val userId = user.id

            // TODO подумать как это можно вынести в отдельный класс с использованием сессии
            val pubSub = object : JedisPubSub() {

                override fun onMessage(channel: String?, event: String?) {
                    launch(coroutinePool) {
                        supervisorScope {
                            launch(loggingHandler) {
                                if (channel == null || event == null) {
                                    // скорее всего этого не будет, но на всякий случай сделано
                                    logger.warn { "Channel or message eq null" }
                                    return@launch
                                }

                                val ids = event.split(":")

                                if (chatId == ids.first().toLong()) {
                                    val message = messageService.getById(ids[1].toLong())
                                    sendSerialized(message)
                                }
                            }
                        }
                    }
                }

            }

            val publisherJob = CoroutineScope(coroutinePool + ChatSessionContext(ChatSession(chatId, userId))).launch {
                supervisorScope {
                    val sendingHandler = CoroutineExceptionHandler { _, th ->
                        launch(loggingHandler) { sendSerialized(exceptionMapper.mapToWebSocketError(th)) }
                    }
                    for (frame in incoming) {
                        launch(sendingHandler) {
                            val request = objectMapper.readValue<WebSocketMessageCreateRequest>(frame.data)
                            messageService.create(request, chatId, userId)
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
}
