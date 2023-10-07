package kek.team.kokline.configurations

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.authentication
import io.ktor.server.auth.session
import io.ktor.server.sessions.Sessions
import io.ktor.server.sessions.cookie
import kek.team.kokline.session.UserSession

fun Application.configureAuth() {
    authentication {
        session<UserSession> {

        }
    }
    install(Sessions) {
        cookie<UserSession>("user_session") {

        }
    }
}
