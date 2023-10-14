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
import kek.team.kokline.security.sessions.BasicUserSession
import kek.team.kokline.security.sessions.SessionNames.userSession

fun Route.authRouting() {
    route("/auth") {
        authenticate("auth-basic") {
            post("/login") {
                val session = call.principal<BasicUserSession>()
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
