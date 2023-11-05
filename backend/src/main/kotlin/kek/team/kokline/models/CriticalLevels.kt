package kek.team.kokline.models

enum class CriticalLevels {
    CRITICAL, NON_CRITICAL;

    val webSocketErrorStatus: WebSocketErrorStatus = WebSocketErrorStatus.valueOf(name)
}
