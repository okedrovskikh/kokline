package kek.team.kokline.security.sessions

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.server.sessions.SessionStorage
import kek.team.kokline.factories.RedisFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RedisSessionStorage(private val cookieLifetime: Long) : SessionStorage {
    private val objectMapper = jacksonObjectMapper()

    override suspend fun invalidate(id: String): Unit = withContext(Dispatchers.IO) {
        RedisFactory.jedisPool.jsonDel(id)
    }

    override suspend fun read(id: String): String {
        val session: Any = withContext(Dispatchers.IO) {
            RedisFactory.jedisPool.jsonGet(id)
        } ?: throw NoSuchElementException("Not found session by id: $id")
        return objectMapper.writeValueAsString(session)
    }

    override suspend fun write(id: String, value: String): Unit = withContext(Dispatchers.IO) {
        RedisFactory.jedisPool.jsonSet(id, value)
        RedisFactory.jedisPool.expire(id, cookieLifetime)
    }

}
