package kek.team.kokline.security.actions

enum class Actions {
    USER_EDIT, USER_DELETE, CHAT_READ, CHAT_EDIT, CHAT_DELETE, MESSAGE_EDIT, MESSAGE_DELETE;

    val actionName = name.split("_").joinToString(":") { it.lowercase() }
}
