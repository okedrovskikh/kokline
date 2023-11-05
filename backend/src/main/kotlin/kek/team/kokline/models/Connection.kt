package kek.team.kokline.models

import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.Principal
import io.ktor.server.auth.principal

@JvmInline
value class Connection(val call: ApplicationCall) {
    inline fun <reified T : Principal> session(): T? = call.principal<T>()
}
