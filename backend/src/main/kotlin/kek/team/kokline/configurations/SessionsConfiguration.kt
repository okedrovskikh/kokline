package kek.team.kokline.configurations

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.sessions.Sessions
import io.ktor.server.sessions.cookie
import io.ktor.server.sessions.serialization.KotlinxSessionSerializer
import kek.team.kokline.security.sessions.AuthSession
import kek.team.kokline.security.sessions.RedisSessionStorage
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer

/**
 * depends on KoinConfiguration
 * @see kek.team.kokline.configurations.configureKoin
 */
fun Application.configureSessions() {
    install(Sessions) {
        /**
         * cookie.maxAgeInSeconds should be synchronized with RedisSessionStorage expire time
         */
        val sessionLifeTime = requireNotNull(this@configureSessions.environment.config.property("session.lifetime").getString().toLong())

        cookie<AuthSession>("user-session", RedisSessionStorage(sessionLifeTime)) {
            cookie.path = "/"
            cookie.maxAgeInSeconds = sessionLifeTime
            serializer = KotlinxSessionSerializer(serializer(), Json)
        }
    }
}
