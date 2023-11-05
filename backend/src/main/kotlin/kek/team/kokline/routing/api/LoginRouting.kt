package kek.team.kokline.routing.api

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.sessions.sessions
import kek.team.kokline.security.sessions.AuthSession
import kek.team.kokline.security.sessions.authBasic
import kek.team.kokline.security.sessions.authSession
import kek.team.kokline.security.sessions.userSession
import kek.team.kokline.support.utils.authAndCallMethod

fun Route.authRouting() {
    route("/auth") {
        authenticate(authBasic) {
            authAndCallMethod(::post, "/login") {
                val session = authSession()
                call.sessions.set(userSession, session)
                call.respond(HttpStatusCode.Accepted)
            }
            post("/logout") {
                call.sessions.clear(userSession)
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}
