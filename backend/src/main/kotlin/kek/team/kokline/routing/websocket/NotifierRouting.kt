package kek.team.kokline.routing.websocket

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.server.routing.Route
import io.ktor.server.websocket.webSocket
import io.ktor.utils.io.CancellationException
import io.ktor.websocket.CloseReason
import io.ktor.websocket.close
import kek.team.kokline.coroutines.coroutinePool
import kek.team.kokline.factories.RedisFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import redis.clients.jedis.JedisPubSub

private val logger = KotlinLogging.logger { }
private val pubSubChannels = arrayOf("events")

// контракт
//----------
// сущность
// id
// операция
fun Route.notifierRouting() {
    webSocket("/listen") {
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
                        logger.warn {"Channel or message eq null" }
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
