package kek.team.kokline.security.auth

import io.ktor.server.application.Application
import io.ktor.server.auth.UnauthorizedResponse
import io.ktor.server.auth.authentication
import io.ktor.server.auth.session
import io.ktor.server.response.respond
import kek.team.kokline.models.Preference
import kek.team.kokline.security.sessions.BasicUserSession
import kek.team.kokline.security.sessions.SessionNames.userDeleteSession
import kek.team.kokline.security.sessions.SessionNames.userEditSession
import kek.team.kokline.service.security.SecurityService
import org.koin.ktor.ext.inject

fun Application.configureUserApiAuth() {

    val securityService: SecurityService by inject<SecurityService>()

    authentication {
        session<BasicUserSession>(userEditSession) {
            validate { session ->
                if (securityService.validate(session, Preference(session.id, "user:edit"))) session else null
            }
            challenge {
                call.respond(UnauthorizedResponse())
            }
        }
        session<BasicUserSession>(userDeleteSession) {
            validate { session ->
                if (securityService.validate(session, Preference(session.id, "user:delete"))) session else null
            }
            challenge {
                call.respond(UnauthorizedResponse())
            }
        }
    }
}
