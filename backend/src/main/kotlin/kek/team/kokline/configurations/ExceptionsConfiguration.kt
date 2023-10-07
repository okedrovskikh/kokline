package kek.team.kokline.configurations

import io.ktor.http.HttpStatusCode.Companion.InternalServerError
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import kek.team.kokline.exceptions.InternalServerException
import kek.team.kokline.mappers.ExceptionsMapper
import org.koin.ktor.ext.inject

/**
 * depends on KoinConfiguration
 * @see kek.team.kokline.configurations.configureKoin
 */
fun Application.configureExceptions() {

    val mapper: ExceptionsMapper by inject<ExceptionsMapper>()

    install(StatusPages) {
        exception<InternalServerException> { call, cause ->
            call.respond(cause.httpStatusCode, mapper.mapExceptionToError(cause))
        }
        exception<Throwable> { call, cause ->
            call.respondText(text = "$cause", status = InternalServerError)
        }
    }
}
