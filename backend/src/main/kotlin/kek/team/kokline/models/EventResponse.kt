package kek.team.kokline.models

data class EventResponse(val entity: Entity, val resourceId: Long, val operation: Operation)

enum class Entity {
    CHAT, USER, MESSAGE
}

enum class Operation {
    CREATE, UPDATE, DELETE
}
