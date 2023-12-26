package kek.team.kokline.routing.api

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import io.ktor.server.sessions.sessions
import kek.team.kokline.models.UserEditRequest
import kek.team.kokline.service.user.UserService
import kek.team.kokline.security.sessions.basicSession
import kek.team.kokline.security.sessions.authSession
import kek.team.kokline.security.sessions.userDeleteSession
import kek.team.kokline.security.sessions.userEditSession
import kek.team.kokline.support.utils.authAndCallMethod
import kek.team.kokline.support.utils.getId
import org.koin.ktor.ext.inject

fun Route.userRouting() {

    val service: UserService by inject<UserService>()

    route("/users") {
        get("") {
            if (call.request.queryParameters["search"] != null) {
                val search = call.request.queryParameters["search"]!!
                call.respond(service.search(search))
                return@get
            }

            call.respond(service.getAll())
        }
        authenticate(basicSession) {
            authAndCallMethod(::get, "/me") {
                val id = authSession().id
                call.respond(service.getById(id))
            }
        }
        authenticate(basicSession) {
            authAndCallMethod(::get, "/{id?}") {
                val id = call.getId()
                call.respond(service.getById(id))
            }
        }
        authenticate(userEditSession) {
            authAndCallMethod(::put, "") {
                val editRequest = call.receive<UserEditRequest>()
                service.edit(authSession().id, editRequest)
                call.respond(HttpStatusCode.OK)
            }
        }
        authenticate(userDeleteSession) {
            authAndCallMethod(::delete, "") {
                service.deleteById(authSession().id)
                call.sessions.clear("user-session")
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}
