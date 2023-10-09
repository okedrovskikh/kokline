package kek.team.kokline.configurations

import io.ktor.server.application.Application
import io.ktor.server.auth.UnauthorizedResponse
import io.ktor.server.auth.authentication
import io.ktor.server.auth.session
import io.ktor.server.response.respond
import kek.team.kokline.service.login.LoginService
import kek.team.kokline.session.UserSession
import org.koin.ktor.ext.inject

/**
 * depends on KoinConfiguration
 * @see kek.team.kokline.configurations.configureKoin
 */
fun Application.configureAuth() {

    val service: LoginService by inject<LoginService>()

    authentication {
        session<UserSession>("auth-session") {
            validate { if (service.validate(it)) it else null }
            challenge { call.respond(UnauthorizedResponse()) }
        }
    }
}
