package kek.team.kokline.redis.events

enum class Events {
    CHAT_CREATE, CHAT_EDIT, CHAT_DELETE, USER_EDIT, USER_DELETE, CHAT_MESSAGE_CREATE, MESSAGE_EDIT, MESSAGE_DELETE;

    val eventName: String = "events:${name.split("_").joinToString(":") { it.lowercase() }}"

    val eventPrefix: String = name.split("_").first()

    companion object {
        fun getFromString(value: String): Events = Events.entries.find { it.eventName == value } ?: error("Cannot find event = $value")
    }
}
