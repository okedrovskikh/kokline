package kek.team.kokline.security.sessions

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.server.sessions.SessionStorage
import kek.team.kokline.factories.RedisFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RedisSessionStorage : SessionStorage {
    private val objectMapper = jacksonObjectMapper()

    override suspend fun invalidate(id: String): Unit = withContext(Dispatchers.IO) {
        RedisFactory.jedisPool.jsonDel(id)
    }

    override suspend fun read(id: String): String = withContext(Dispatchers.IO) {
        objectMapper.writeValueAsString(RedisFactory.jedisPool.jsonGet(id))
    }

    override suspend fun write(id: String, value: String): Unit = withContext(Dispatchers.IO) {
        RedisFactory.jedisPool.jsonSet(id, value)
    }

}
