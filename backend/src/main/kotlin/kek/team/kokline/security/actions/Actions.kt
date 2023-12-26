package kek.team.kokline.security.actions

enum class Actions {
    BASIC, USER_EDIT, USER_DELETE, CHAT_READ, CHAT_EDIT, CHAT_DELETE, MESSAGE_EDIT, MESSAGE_DELETE;

    val actionName: String = name.split("_").joinToString(":") { it.lowercase() }

    val prefix: ActionPrefixes = ActionPrefixes.valueOf(name.split("_").first())

    companion object {

        fun getFromString(value: String): Actions = entries.find { it.actionName == value } ?: error("Not found action")
    }
}

enum class ActionPrefixes {
    USER, MESSAGE, CHAT, BASIC;

    val actionPrefix: String = name.lowercase() + ":"
}
