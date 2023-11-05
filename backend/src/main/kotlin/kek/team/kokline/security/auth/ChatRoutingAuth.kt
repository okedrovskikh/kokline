package kek.team.kokline.security.auth

import io.ktor.server.application.Application
import io.ktor.server.auth.UnauthorizedResponse
import io.ktor.server.auth.authentication
import io.ktor.server.auth.session
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import kek.team.kokline.models.Preference
import kek.team.kokline.models.RequestWithId
import kek.team.kokline.security.actions.Actions.CHAT_EDIT
import kek.team.kokline.security.actions.Actions.CHAT_READ
import kek.team.kokline.security.actions.Actions.CHAT_DELETE
import kek.team.kokline.security.sessions.AuthSession
import kek.team.kokline.security.sessions.chatDeleteSession
import kek.team.kokline.security.sessions.chatEditSession
import kek.team.kokline.security.sessions.chatReadSession
import kek.team.kokline.service.security.SecurityService
import kek.team.kokline.support.utils.getIdOrNull
import org.koin.ktor.ext.inject

fun Application.configureChatApiAuth() {

    val securityService: SecurityService by inject<SecurityService>()

    authentication {
        session<AuthSession>(chatEditSession) {
            validate { session ->
                val request = receive<RequestWithId>()
                if (securityService.validate(session, Preference(request.id, CHAT_EDIT))) {
                    session
                } else {
                    null
                }
            }
            challenge {
                call.respond(UnauthorizedResponse())
            }
        }
        session<AuthSession>(chatDeleteSession) {
            validate { session ->
                if (securityService.validate(session, Preference(getIdOrNull(), CHAT_DELETE))) {
                    session
                } else {
                    null
                }
            }
            challenge {
                call.respond(UnauthorizedResponse())
            }
        }
        session<AuthSession>(chatReadSession) {
            validate { session ->
                if (securityService.validate(session, Preference(getIdOrNull(), CHAT_READ))) {
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
