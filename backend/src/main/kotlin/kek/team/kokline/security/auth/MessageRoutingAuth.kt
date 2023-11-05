package kek.team.kokline.security.auth

import io.ktor.server.application.Application
import io.ktor.server.auth.UnauthorizedResponse
import io.ktor.server.auth.authentication
import io.ktor.server.auth.session
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import kek.team.kokline.models.Preference
import kek.team.kokline.models.RequestWithId
import kek.team.kokline.security.actions.Actions.MESSAGE_EDIT
import kek.team.kokline.security.actions.Actions.MESSAGE_DELETE
import kek.team.kokline.security.sessions.AuthSession
import kek.team.kokline.security.sessions.messageDeleteSession
import kek.team.kokline.security.sessions.messageEditSession
import kek.team.kokline.service.security.SecurityService
import kek.team.kokline.support.utils.getIdOrNull
import org.koin.ktor.ext.inject

fun Application.configureMessageApiAuth() {

    val securityService: SecurityService by inject<SecurityService>()

    authentication {
        session<AuthSession>(messageEditSession) {
            validate { session ->
                val request = receive<RequestWithId>()
                if (securityService.validate(session, Preference(request.id, MESSAGE_EDIT))) {
                    session
                } else {
                    null
                }
            }
            challenge {
                call.respond(UnauthorizedResponse())
            }
        }
        session<AuthSession>(messageDeleteSession) {
            validate { session ->
                if (securityService.validate(session, Preference(getIdOrNull(), MESSAGE_DELETE))) {
                    session
                } else {
                    null
                }
            }
            challenge {
                call.respond(UnauthorizedResponse())
            }
        }
    }
}
