package kek.team.kokline.redis.consumer

import io.ktor.utils.io.CancellationException
import kek.team.kokline.factories.RedisFactory
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import redis.clients.jedis.JedisPubSub
import java.lang.ref.Cleaner
import java.lang.ref.Cleaner.Cleanable

/**
 * Класс не thread safe. Хранит внутри себя свое состояние и сам же им управляет. Если хотим новую подписку -> создаем новый экземпляр
 */
class MessageConsumer(val channelName: String) : AutoCloseable {
    private lateinit var job: Job
    private lateinit var channel: Channel<String>

    suspend fun subscribe(): Channel<String> = coroutineScope {
        channel = Channel(10)

        val pubSub = object : JedisPubSub() {

            override fun onMessage(channel: String?, message: String?) {
                this@coroutineScope.launch {
                    supervisorScope {
                        launch {
                            if (channel == null || message == null) {
                                return@launch // хоть и ругается, но не важно откуда вернется
                            }

                            if (channel == channelName) {
                                this@MessageConsumer.channel.send(message)
                            }
                        }
                    }
                }
            }

        }

        job = launch {
            RedisFactory.jedisPool.subscribe(pubSub, channelName)
        }

        job.invokeOnCompletion {
            pubSub.unsubscribe()
            println("Here")
            if (it !is CancellationException) {
                channel.close(it)
            }
        }

        channel.invokeOnClose { if (it != null) job.cancel("Exception", it) else job.cancel(CancellationException("Subscription ended")) }

        channel
    }

    override fun close() {
        channel.close()
    }
}

class MessageConsumerDescription(private val consumer: MessageConsumer) : AutoCloseable {
    private val cleaner: Cleanable = Cleaner.create().register(consumer) { consumer.close() }

    suspend fun subscribe() = consumer.subscribe()

    override fun close() {
        consumer.close()
    }

}
