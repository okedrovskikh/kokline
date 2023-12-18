package kek.team.kokline.factories

import io.github.oshai.kotlinlogging.KotlinLogging
import kek.team.kokline.models.EventModel
import kek.team.kokline.redis.consumer.ChannelMessageConsumer
import kek.team.kokline.redis.consumer.MessageConsumer
import kek.team.kokline.support.utils.loggingCoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import redis.clients.jedis.JedisPubSub
import java.util.concurrent.Executors

object MessageConsumerFactory {
    private val logger = KotlinLogging.logger { }
    private val loggingHandler = loggingCoroutineExceptionHandler(logger)
    private val consumersCoroutinePool = Executors.newFixedThreadPool(5).asCoroutineDispatcher()

    fun createConsumer(vararg channels: String): MessageConsumer<EventModel> {
        val listenChannel = Channel<EventModel> { }

        val job = CoroutineScope(consumersCoroutinePool).launch {
            val pubSub = object : JedisPubSub() {
                override fun onMessage(channel: String?, event: String?) {
                    launch {
                        supervisorScope {
                            launch(loggingHandler) {
                                if (channel == null || event == null) {
                                    // скорее всего этого не будет, но на всякий случай сделано
                                    logger.warn { "Channel or message eq null" }
                                    return@launch
                                }

                                if (channel in channels) {
                                    listenChannel.send(EventModel("$channel|$event"))
                                }
                            }
                        }
                    }
                }
            }

            launch(loggingHandler) {
                RedisFactory.jedisPool.subscribe(pubSub, *channels)
            }.invokeOnCompletion { pubSub.unsubscribe(); logger.debug { "Pubsub unsubscribed" } }
        }

        // максимально не уверен, что это работает правильно
        job.invokeOnCompletion { listenChannel.close(it); logger.debug { "Channel: $channels closed" } }
        listenChannel.invokeOnClose { job.cancel() }

        return ChannelMessageConsumer(channels, listenChannel)
    }
}