package kek.team.kokline.security.actions

enum class Actions {
    BASIC, USER_EDIT, USER_DELETE, CHAT_READ, CHAT_EDIT, CHAT_DELETE, MESSAGE_EDIT, MESSAGE_DELETE;

    val actionName: String = name.split("_").joinToString(":") { it.lowercase() }
}

enum class ActionPrefixes {
    USER, MESSAGE, CHAT;

    val actionPrefix: String = name.lowercase() + ":"
}
