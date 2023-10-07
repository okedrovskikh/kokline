package kek.team.kokline.redis

enum class RedisChannels(val channelName: String) {
    MESSAGE_CREATE("message:create"),
    MESSAGE_EDIT("message:edit"),
    MESSAGE_DELETE("message:delete"),
    CHAT_EDIT("chat:edit"),
    CHAT_DELETE("chat:delete"),
    USER_EDIT("user:edit"),
    USER_DELETE("user:delete")
}
