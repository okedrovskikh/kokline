package kek.team.kokline.routing.api

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.sessions.sessions
import kek.team.kokline.service.login.LoginService
import kek.team.kokline.session.UserSession
import org.koin.ktor.ext.inject

private const val userSessionName = "user-session"

fun Route.loginRouting() {

    val service: LoginService by inject<LoginService>()

    route("/auth") {
        post("/login") {
            val credits = call.request.headers["x-credits"] ?: throw BadRequestException("No header x-credits")
            val id = service.login(credits)
            call.sessions.set(userSessionName, UserSession(id))
            call.respond(HttpStatusCode.Accepted)
        }
        post("/logout") {
            call.sessions.clear(userSessionName)
            call.respond(HttpStatusCode.OK)
        }
    }
}
