package kek.team.kokline.routing.api

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.request.*
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.sessions.sessions
import kek.team.kokline.models.UserCreateRequest
import kek.team.kokline.security.sessions.authBasic
import kek.team.kokline.security.sessions.authSession
import kek.team.kokline.security.sessions.userSession
import kek.team.kokline.service.user.UserService
import kek.team.kokline.support.utils.authAndCallMethod
import org.koin.ktor.ext.inject

fun Route.authRouting() {

    val service: UserService by inject<UserService>()

    route("/auth") {
        authenticate(authBasic) {
            authAndCallMethod(::post, "/login") {
                val session = authSession()
                call.sessions.set(userSession, session)
                val user = service.getById(session.id)
                call.respond(HttpStatusCode.Accepted, user)
            }
            post("/logout") {
                call.sessions.clear(userSession)
                call.respond(HttpStatusCode.OK)
            }
        }
        post("/signup") {
            val createRequest = call.receive<UserCreateRequest>()
            val user = service.create(createRequest)
            call.respond(HttpStatusCode.Created, user)
        }
    }
}
