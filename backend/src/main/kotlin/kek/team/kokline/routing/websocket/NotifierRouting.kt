package kek.team.kokline.routing.websocket

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.server.routing.Route
import io.ktor.server.routing.route
import io.ktor.server.websocket.sendSerialized
import io.ktor.server.websocket.webSocket
import io.ktor.utils.io.CancellationException
import kek.team.kokline.factories.CoroutinePoolFactory.coroutinePool
import kek.team.kokline.factories.MessageConsumerFactory
import kek.team.kokline.models.ErrorResponse
import kek.team.kokline.models.WebSocketErrorResponse
import kek.team.kokline.models.WebSocketErrorStatus
import kek.team.kokline.redis.events.Events
import kek.team.kokline.service.events.EventsServiceFacade
import kek.team.kokline.support.utils.authAndCallMethod
import kek.team.kokline.support.utils.loggingCoroutineExceptionHandler
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import org.koin.ktor.ext.inject

private val logger = KotlinLogging.logger { }
private val loggingHandler = loggingCoroutineExceptionHandler(logger)

fun Route.notifierRouting() {

    val service: EventsServiceFacade by inject<EventsServiceFacade>()

    route("/notifier") {
        authAndCallMethod(::webSocket, "/listen") {
            val publisherJob = launch(coroutinePool) {
                supervisorScope {
                    for (frame in incoming) {
                        launch(loggingHandler) {
                            sendSerialized(
                                WebSocketErrorResponse(
                                    error = ErrorResponse("Cannot send message to this channel", null),
                                    errorStatus = WebSocketErrorStatus.NON_CRITICAL
                                )
                            )
                        }
                    }
                }
            }

            val subJob = launch(coroutinePool) {
                val consumer = MessageConsumerFactory.createConsumer(*Events.entries.map { it.eventName }.toTypedArray())

                supervisorScope {
                    while (consumer.isActive()) {
                        val receivedMessage = consumer.getMessage()
                        launch(loggingHandler) {
                            val eventChannel = receivedMessage.eventChannel
                            val message = receivedMessage.message
                            val response = service.handle(message, Events.getFromString(eventChannel))
                            if (response == null) {
                                sendSerialized(
                                    WebSocketErrorResponse(
                                        error = ErrorResponse("Cannot handle message", null),
                                        errorStatus = WebSocketErrorStatus.NON_CRITICAL
                                    )
                                )
                            } else {
                                sendSerialized(response)
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
