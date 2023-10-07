package kek.team.kokline.models

// у payload'а появиться формат, но какой пока не понятно
data class Message(val id: Long?, val payload: String, val chatId: Long)

data class MessageCreateRequest(val payload: String, val chatId: Long)

data class MessageEditRequest(val id: Long, val payload: String)

data class MessagePayload(val text: String)

data class WebSocketMessageRequest(val payload: MessagePayload)
