package kek.team.kokline.configurations

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.sessions.SessionStorageMemory
import io.ktor.server.sessions.Sessions
import io.ktor.server.sessions.cookie
import kek.team.kokline.session.UserSession
import kek.team.kokline.session.userSession

fun Application.configureSessions() {
    install(Sessions) {
        // TODO переделать хранение сессий с InMemory на redis
        cookie<UserSession>(userSession, SessionStorageMemory()) {
            cookie.path = "/"
            cookie.maxAgeInSeconds = 600
        }
    }
}
