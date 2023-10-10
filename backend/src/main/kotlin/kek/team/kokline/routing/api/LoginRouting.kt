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
import kek.team.kokline.session.userSession
import org.koin.ktor.ext.inject

fun Route.authRouting() {

    val service: LoginService by inject<LoginService>()

    route("/auth") {
        post("/login") {
            val credits = call.request.headers["x-credits"] ?: throw BadRequestException("No header x-credits")
            val id = service.login(credits)
            call.sessions.set(userSession, UserSession(id))
            call.respond(HttpStatusCode.Accepted)
        }
        post("/logout") {
            call.sessions.clear(userSession)
            call.respond(HttpStatusCode.OK)
        }
    }
}
