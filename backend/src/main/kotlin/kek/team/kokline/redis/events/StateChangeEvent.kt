package kek.team.kokline.redis.events

data class StateChangeEvent(val operation: StateEvents, val descriptor: String)

enum class StateEvents {
    CHAT_EDITED, CHAT_DELETED, USER_REMOVED,
    USER_EDITED, USER_DELETED,
    MESSAGE_EDITED, MESSAGE_DELETED
}
