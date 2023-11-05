package kek.team.kokline.redis.consumer

import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.channels.Channel

interface MessageConsumer<T> {
    val channel: String

    suspend fun isActive(): Boolean

    suspend fun getMessage(): T

    suspend fun sendMessage(message: T)
}

class ChannelMessageConsumer<T>(override val channel: String, private val listenChannel: Channel<T>) : MessageConsumer<T>, AutoCloseable {
    private val typeRef = object : TypeToken<T>() {}.type

    @OptIn(DelicateCoroutinesApi::class)
    override suspend fun isActive(): Boolean = !listenChannel.isClosedForReceive

    override suspend fun getMessage(): T = listenChannel.receive()

    override suspend fun sendMessage(message: T) = listenChannel.send(message)

    override fun close() {
        listenChannel.close()
    }

    override fun hashCode(): Int = channel.hashCode()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ChannelMessageConsumer<*>) return false

        return channel == other.channel && typeRef == other.typeRef
    }

}
