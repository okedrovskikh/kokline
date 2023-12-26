package kek.team.kokline.models

data class ErrorResponse(val message: String?, val innerErrors: List<ErrorResponse>?)

data class WebSocketErrorResponse(val error: ErrorResponse, val errorStatus: WebSocketErrorStatus)

enum class WebSocketErrorStatus {
    CRITICAL, NON_CRITICAL
}
