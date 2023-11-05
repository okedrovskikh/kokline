package kek.team.kokline.routing.websocket

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.server.routing.Route
import io.ktor.server.routing.route
import io.ktor.server.websocket.webSocket
import io.ktor.utils.io.CancellationException
import io.ktor.websocket.CloseReason
import io.ktor.websocket.close
import kek.team.kokline.factories.coroutinePool
import kek.team.kokline.factories.RedisFactory
import kek.team.kokline.service.events.EventsService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.ktor.ext.inject
import redis.clients.jedis.JedisPubSub

private val logger = KotlinLogging.logger { }
private val pubSubChannels = arrayOf("events")

// контракт
//----------
// сущность
// id
// операция
fun Route.notifierRouting() {

    val service: EventsService by inject<EventsService>()

    route("/notifier") {
        webSocket("/listen") {
            val userId = call.request.headers["x-user-id"]?.toLongOrNull() ?: return@webSocket close(
                CloseReason(
                    code = CloseReason.Codes.CANNOT_ACCEPT,
                    message = "No header x-user-id"
                )
            )

            val pubSub = object : JedisPubSub() {
                override fun onMessage(channel: String?, event: String?) {
                    try {
                        if (channel == null || event == null) {
                            // скорее всего этого не будет, но на всякий случай сделано
                            logger.warn { "Channel or message eq null" }
                            return
                        }

                        val ids = event.split(":")


                    } catch (th: Throwable) {
                        logger.error(th) {}
                    }
                }
            }

            val subscriberJob = CoroutineScope(coroutinePool).launch {
                RedisFactory.jedisPool.subscribe(pubSub, *pubSubChannels)
            }

            closeReason.invokeOnCompletion {
                subscriberJob.cancel(CancellationException("Connection closed"))
                pubSub.unsubscribe()
            }

            closeReason.await()
            subscriberJob.join()
        }
    }
}
