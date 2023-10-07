package kek.team.kokline.factories

import io.ktor.server.config.ApplicationConfig
import redis.clients.jedis.JedisPooled

object RedisFactory {
    lateinit var jedisPool: JedisPooled
        private set

    fun init(config: ApplicationConfig) {
        if (::jedisPool.isInitialized) error("Jedis pool initialized")

        val url = config.property("redis.url").getString()
        val port = config.property("redis.port").getString().toInt()
        jedisPool = JedisPooled(url, port)
    }

    fun close() {
        jedisPool.close()
    }
}
