package kek.team.kokline.models

data class MessagePayload(val text: String)

// у payload'а появиться формат, но какой пока не понятно
data class Message(val id: Long?, val payload: String, val chatId: Long)

data class WebSocketMessageCreateRequest(val payload: MessagePayload)

data class MessageEditRequest(val id: Long, val payload: String)
