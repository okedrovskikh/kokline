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
import kek.team.kokline.security.sessions.chatDeleteSession
import kek.team.kokline.security.sessions.chatEditSession
import kek.team.kokline.security.sessions.chatReadSession
import kek.team.kokline.service.security.SecurityService
import org.koin.ktor.ext.inject

fun Application.configureChatApiAuth() {

    val securityService: SecurityService by inject<SecurityService>()

    authentication {
        session<BasicUserSession>(chatEditSession) {
            validate { session ->
                val request = receive<RequestWithId>()
                if (securityService.validate(session, Preference(request.id, "chat:edit"))) {
                    session
                } else {
                    null
                }
            }
            challenge {
                call.respond(UnauthorizedResponse())
            }
        }
        session<BasicUserSession>(chatDeleteSession) {
            validate { session ->
                if (securityService.validate(session, Preference(parameters["id"]?.toLongOrNull(), "chat:delete"))) {
                    session
                } else {
                    null
                }
            }
            challenge {
                call.respond(UnauthorizedResponse())
            }
        }
        session<BasicUserSession>(chatReadSession) {
            validate { session ->
                if (securityService.validate(session, Preference(parameters["id"]?.toLongOrNull(), "chat:read"))) {
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
