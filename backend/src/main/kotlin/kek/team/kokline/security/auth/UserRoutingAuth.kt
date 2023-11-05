package kek.team.kokline.security.auth

import io.ktor.server.application.Application
import io.ktor.server.auth.UnauthorizedResponse
import io.ktor.server.auth.authentication
import io.ktor.server.auth.session
import io.ktor.server.response.respond
import kek.team.kokline.models.Preference
import kek.team.kokline.security.actions.Actions.USER_EDIT
import kek.team.kokline.security.actions.Actions.USER_DELETE
import kek.team.kokline.security.sessions.AuthSession
import kek.team.kokline.security.sessions.userDeleteSession
import kek.team.kokline.security.sessions.userEditSession
import kek.team.kokline.service.security.SecurityService
import org.koin.ktor.ext.inject

fun Application.configureUserApiAuth() {

    val securityService: SecurityService by inject<SecurityService>()

    authentication {
        session<AuthSession>(userEditSession) {
            validate { session ->
                if (securityService.validate(session, Preference(session.id, USER_EDIT))) session else null
            }
            challenge {
                call.respond(UnauthorizedResponse())
            }
        }
        session<AuthSession>(userDeleteSession) {
            validate { session ->
                if (securityService.validate(session, Preference(session.id, USER_DELETE))) session else null
            }
            challenge {
                call.respond(UnauthorizedResponse())
            }
        }
    }
}
