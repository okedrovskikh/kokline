package kek.team.kokline.security.auth

import io.ktor.server.application.Application
import io.ktor.server.auth.UnauthorizedResponse
import io.ktor.server.auth.authentication
import io.ktor.server.auth.session
import io.ktor.server.response.respond
import kek.team.kokline.models.basicPreference
import kek.team.kokline.security.sessions.BasicUserSession
import kek.team.kokline.security.sessions.basicSession
import kek.team.kokline.service.security.SecurityService
import org.koin.ktor.ext.inject

fun Application.configureBasicAuth() {

    val securityService: SecurityService by inject<SecurityService>()

    authentication {
        session<BasicUserSession>(basicSession) {
            validate { session ->
                if (securityService.validate(session, basicPreference())) session else null
            }
            challenge {
                call.respond(UnauthorizedResponse())
            }
        }
    }
}
