package kek.team.kokline.redis.consumer

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.server.websocket.sendSerialized
import kek.team.kokline.coroutines.coroutinePool
import kek.team.kokline.models.Message
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import redis.clients.jedis.JedisPubSub

class MessageConsumer(private val channel: String) {
    suspend fun subscribe() = coroutineScope {

    }
}
