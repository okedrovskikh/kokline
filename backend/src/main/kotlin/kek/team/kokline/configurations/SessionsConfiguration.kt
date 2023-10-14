package kek.team.kokline.configurations

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.sessions.SessionStorageMemory
import io.ktor.server.sessions.Sessions
import io.ktor.server.sessions.cookie
import io.ktor.server.sessions.serialization.KotlinxBackwardCompatibleSessionSerializer
import io.ktor.server.sessions.serialization.KotlinxSessionSerializer
import kek.team.kokline.security.sessions.BasicUserSession
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer

fun Application.configureSessions() {
    install(Sessions) {
        // TODO переделать хранение сессий с InMemory на redis
        cookie<BasicUserSession>("user-session", SessionStorageMemory()) {
            cookie.path = "/"
            cookie.maxAgeInSeconds = 600
            serializer = KotlinxSessionSerializer(serializer(), Json)
        }
    }
}
