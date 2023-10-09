package kek.team.kokline.exceptions

import io.ktor.http.HttpStatusCode
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.Forbidden
import io.ktor.http.HttpStatusCode.Companion.NotFound

sealed class InternalServerException(
    val httpStatusCode: HttpStatusCode,
    message: String? = null,
    cause: Throwable? = null
) : KoklineExceptions(message, cause)

class NotFoundException(message: String, cause: Throwable? = null) : InternalServerException(NotFound, message, cause)

class BadRequestException(message: String, cause: Throwable? = null) : InternalServerException(BadRequest, message, cause)

class ForbiddenException(message: String, cause: Throwable? = null) : InternalServerException(Forbidden, message, cause)
