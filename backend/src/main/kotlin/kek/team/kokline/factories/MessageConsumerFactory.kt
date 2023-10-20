package kek.team.kokline.factories

import io.github.oshai.kotlinlogging.KotlinLogging
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

    fun createConsumer(channel: String): MessageConsumer<String> {
        val listenChannel = Channel<String> { }

        val job = CoroutineScope(consumersCoroutinePool).launch {
            val pubSub = object : JedisPubSub() {
                override fun onMessage(channel1: String?, event: String?) {
                    launch {
                        supervisorScope {
                            launch(loggingHandler) {
                                if (channel1 == null || event == null) {
                                    // скорее всего этого не будет, но на всякий случай сделано
                                    logger.warn { "Channel or message eq null" }
                                    return@launch
                                }

                                if (channel1 == channel) {
                                    listenChannel.send(event)
                                }
                            }
                        }
                    }
                }
            }

            launch(loggingHandler) {
                RedisFactory.jedisPool.subscribe(pubSub, channel)
            }.invokeOnCompletion { pubSub.unsubscribe(); logger.debug { "Pubsub unsubscribed" } }
        }

        // максимально не уверен, что это работает правильно
        job.invokeOnCompletion { listenChannel.close(it); logger.debug { "Channel: $channel closed" } }
        listenChannel.invokeOnClose { job.cancel() }

        return ChannelMessageConsumer(channel, listenChannel)
    }
}