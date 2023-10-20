package kek.team.kokline.configurations

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.sessions.Sessions
import io.ktor.server.sessions.cookie
import io.ktor.server.sessions.serialization.KotlinxSessionSerializer
import kek.team.kokline.security.sessions.BasicUserSession
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
        cookie<BasicUserSession>("user-session", RedisSessionStorage()) {
            cookie.path = "/"
            cookie.maxAgeInSeconds = 600
            serializer = KotlinxSessionSerializer(serializer(), Json)
        }
    }
}
