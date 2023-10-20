package kek.team.kokline.redis.consumer

import com.google.gson.reflect.TypeToken
import io.github.oshai.kotlinlogging.KotlinLogging
import kek.team.kokline.factories.RedisFactory
import kek.team.kokline.support.utils.loggingCoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import redis.clients.jedis.JedisPubSub
import java.util.concurrent.ConcurrentSkipListMap
import java.util.concurrent.Executors

interface MessageConsumer<T> {
    val channel: String

    suspend fun isActive(): Boolean

    suspend fun getMessage(): T

    suspend fun sendMessage(message: T)
}

interface MessageDispatcher<T> {

    fun register(consumer: MessageConsumer<T>)

    fun unregister(consumer: MessageConsumer<T>)
}

// TODO доделать диспатчер. Нужен чтоб убрать нагрузку с фабрики, по настройке закрытия джобов и каналов
class RedisMessageDispatcher<T> : MessageDispatcher<T> {

    init {
        CoroutineScope(pool).launch {
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

                                map[channel1]?.forEach {
                                    it.sendMessage(event as T) // TODO исправить
                                }
                            }
                        }
                    }
                }
            }

            launch {
                RedisFactory.jedisPool.subscribe(pubSub)
            }.invokeOnCompletion { pubSub.unsubscribe() }
        }.invokeOnCompletion { map.flatMap { it.value }.forEach { (it as? AutoCloseable)?.close() }; logger.debug { "Closed" } }
    }

    private val map = ConcurrentSkipListMap<String, MutableList<MessageConsumer<T>>>()

    override fun register(consumer: MessageConsumer<T>) {
        map[consumer.channel]?.add(consumer)
    }

    override fun unregister(consumer: MessageConsumer<T>) {
        map[consumer.channel]?.remove(consumer)
    }

    companion object {
        private val pool = Executors.newFixedThreadPool(5).asCoroutineDispatcher()
        private val logger = KotlinLogging.logger { }
        private val loggingHandler = loggingCoroutineExceptionHandler(logger)
    }
}

class ChannelMessageConsumer<T>(override val channel: String, private val listenChannel: Channel<T>) : MessageConsumer<T>, AutoCloseable {
    private val typeRef = object : TypeToken<T>() {}.type

    @OptIn(DelicateCoroutinesApi::class)
    override suspend fun isActive(): Boolean = !listenChannel.isClosedForReceive

    override suspend fun getMessage(): T = listenChannel.receive()

    override suspend fun sendMessage(message: T) = listenChannel.send(message)

    override fun hashCode(): Int = channel.hashCode()

    override fun close() {
        listenChannel.close()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ChannelMessageConsumer<*>) return false

        return channel == other.channel && typeRef == other.typeRef
    }

}
