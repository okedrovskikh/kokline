package kek.team.kokline.configurations

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.HttpStatusCode
import io.ktor.http.HttpStatusCode.Companion.InternalServerError
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import kek.team.kokline.exceptions.InternalServerException
import kek.team.kokline.mappers.ExceptionsMapper
import org.koin.ktor.ext.inject

private val logger = KotlinLogging.logger { }

/**
 * depends on KoinConfiguration
 * @see kek.team.kokline.configurations.configureKoin
 */
fun Application.configureExceptions() {

    val mapper: ExceptionsMapper by inject<ExceptionsMapper>()

    install(StatusPages) {
        exception<InternalServerException> { call, cause ->
            logger.error(cause) {}
            call.respond(cause.httpStatusCode, mapper.mapExceptionToError(cause))
        }
        exception<BadRequestException> { call, cause ->
            logger.error(cause) {}
            call.respond(HttpStatusCode.BadRequest, mapper.mapExceptionToError(cause))
        }
        exception<Throwable> { call, cause ->
            logger.error(cause) {}
            call.respondText(text = "$cause", status = InternalServerError)
        }
    }
}
