package kek.team.kokline.routing.websocket

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.server.routing.Route
import io.ktor.server.routing.route
import io.ktor.server.websocket.sendSerialized
import io.ktor.server.websocket.webSocket
import io.ktor.utils.io.CancellationException
import io.ktor.websocket.CloseReason
import io.ktor.websocket.close
import io.ktor.websocket.closeExceptionally
import kek.team.kokline.exceptions.CriticalException
import kek.team.kokline.factories.CoroutinePoolFactory.coroutinePool
import kek.team.kokline.models.WebSocketMessageCreateRequest
import kek.team.kokline.factories.MessageConsumerFactory
import kek.team.kokline.mappers.ExceptionsMapper
import kek.team.kokline.redis.events.Events
import kek.team.kokline.service.message.MessageService
import kek.team.kokline.security.sessions.authSession
import kek.team.kokline.support.utils.authAndCallMethod
import kek.team.kokline.support.utils.loggingCoroutineExceptionHandler
import kek.team.kokline.support.utils.receiveDeserialized
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import org.koin.ktor.ext.inject

private val logger = KotlinLogging.logger { }
private val loggingHandler = loggingCoroutineExceptionHandler(logger)

fun Route.chatRouting() {

    val messageService: MessageService by inject<MessageService>()
    val exceptionMapper: ExceptionsMapper by inject<ExceptionsMapper>()

    route("/chat") {
        authAndCallMethod(::webSocket, "/joinChat/{id?}") {
            val chatId = call.request.queryParameters["id"]?.toLongOrNull() ?: return@authAndCallMethod close(
                CloseReason(
                    code = CloseReason.Codes.CANNOT_ACCEPT,
                    message = "Parameter chatId empty ot null"
                )
            )

            val publisherJob = launch(coroutinePool) {
                supervisorScope {
                    val sendingHandler = CoroutineExceptionHandler { _, th ->
                        logger.error(th) { "" }
                        val job = launch(loggingHandler) { sendSerialized(exceptionMapper.mapToWebSocketError(th)) }
                        job.invokeOnCompletion {
                            if (th is CriticalException) {
                                launch { this@authAndCallMethod.closeExceptionally(th) }
                            } else if (it != null) {
                                launch { this@authAndCallMethod.closeExceptionally(it) }
                            }
                        }
                    }
                    for (frame in incoming) {
                        launch(sendingHandler) {
                            val request = frame.receiveDeserialized<WebSocketMessageCreateRequest>()
                            messageService.create(request, chatId, authSession().id)
                        }
                    }
                }
            }

            val subJob = launch(coroutinePool) {
                val consumer = MessageConsumerFactory.createConsumer(Events.CHAT_MESSAGE_CREATE.eventName)

                supervisorScope {
                    while (consumer.isActive()) {
                        val message = consumer.getMessage()
                        launch(loggingHandler) {
                            val ids = message.split(":")

                            if (chatId == ids.first().toLong()) {
                                val message = messageService.getById(ids[1].toLong())
                                sendSerialized(message)
                            }
                        }
                    }
                }
            }

            closeReason.invokeOnCompletion {
                publisherJob.cancel(CancellationException("Connection closed"))
                subJob.cancel(CancellationException("Connection closed"))
            }

            closeReason.await()
            listOf(publisherJob, subJob).joinAll()
        }
    }
}
