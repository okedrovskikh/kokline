package kek.team.kokline.mappers

import kek.team.kokline.exceptions.CriticalException
import kek.team.kokline.models.CriticalLevels
import kek.team.kokline.models.ErrorResponse
import kek.team.kokline.models.WebSocketErrorResponse

class ExceptionsMapper {

    fun mapExceptionToError(throwable: Throwable): ErrorResponse = ErrorResponse(
        message = throwable.message,
        innerErrors = throwable.cause?.let { listOf(mapExceptionToError(it)) }
    )

    fun mapToWebSocketError(throwable: Throwable): WebSocketErrorResponse = WebSocketErrorResponse(
        error = mapExceptionToError(throwable),
        errorStatus = ((throwable as? CriticalException)?.criticalLevel ?: CriticalLevels.NON_CRITICAL).webSocketErrorStatus
    )
}
