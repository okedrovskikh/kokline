package kek.team.kokline.security.auth

import io.ktor.server.application.Application
import io.ktor.server.auth.UnauthorizedResponse
import io.ktor.server.auth.authentication
import io.ktor.server.auth.session
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import kek.team.kokline.models.Preference
import kek.team.kokline.models.RequestWithId
import kek.team.kokline.security.sessions.BasicUserSession
import kek.team.kokline.security.sessions.SessionNames.messageDeleteSession
import kek.team.kokline.security.sessions.SessionNames.messageEditSession
import kek.team.kokline.service.security.SecurityService
import org.koin.ktor.ext.inject

fun Application.configureMessageApiAuth() {

    val securityService: SecurityService by inject<SecurityService>()

    authentication {
        session<BasicUserSession>(messageEditSession) {
            validate { session ->
                val request = receive<RequestWithId>()
                if (securityService.validate(session, Preference(request.id, "message:edit"))) {
                    session
                } else {
                    null
                }
            }
            challenge {
                call.respond(UnauthorizedResponse())
            }
        }
        session<BasicUserSession>(messageDeleteSession) {
            validate { session ->
                if (securityService.validate(session, Preference(parameters["id"]?.toLongOrNull(), "message:delete"))) {
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
