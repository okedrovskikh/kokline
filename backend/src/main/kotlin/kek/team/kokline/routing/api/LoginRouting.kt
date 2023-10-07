package kek.team.kokline.routing.api

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.sessions.sessions
import io.ktor.server.sessions.set
import kek.team.kokline.service.login.LoginService
import kek.team.kokline.session.UserSession
import org.koin.ktor.ext.inject

// TODO доделать
fun Route.loginRouting() {

    val service: LoginService by inject<LoginService>()

    route("/login") {
        post("") {
            val credits = call.principal<UserIdPrincipal>()?.name?.toLongOrNull() ?: return@post call.respond(HttpStatusCode.Forbidden)
            println(credits)
            val exist = service.login(credits)
            if (exist) {
                call.sessions.set(UserSession(credits))
                call.respond(HttpStatusCode.Accepted)
            } else {
                call.respond(HttpStatusCode.Forbidden)
            }
        }
    }
}
