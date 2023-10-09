package kek.team.kokline.mappers

import kek.team.kokline.models.ErrorResponse
import kek.team.kokline.models.WebSocketErrorResponse
import kek.team.kokline.models.WebSocketErrorStatus

class ExceptionsMapper {

    fun mapExceptionToError(throwable: Throwable): ErrorResponse = ErrorResponse(
        message = throwable.message,
        innerErrors = throwable.cause?.let { listOf(mapExceptionToError(it)) }
    )

    fun mapToWebSocketError(throwable: Throwable): WebSocketErrorResponse = WebSocketErrorResponse(
        error = mapExceptionToError(throwable),
        errorStatus = WebSocketErrorStatus.NON_CRITICAL
    )
}
