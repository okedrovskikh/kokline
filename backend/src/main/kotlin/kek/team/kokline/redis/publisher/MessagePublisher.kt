package kek.team.kokline.redis.publisher

import kek.team.kokline.factories.RedisFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object MessagePublisher {

    suspend fun publish(event: String, channel: String) = withContext(Dispatchers.IO) {
        RedisFactory.jedisPool.publish(channel, event)
    }
}
