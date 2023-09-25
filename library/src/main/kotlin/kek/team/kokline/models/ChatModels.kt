package kek.team.kokline.models

data class Chat(val id: Long?, val name: String)

data class ChatCreateRequest(val name: String, val users: List<Long>)

data class ChatEditRequest(val id: Long, val name: String, val users: List<Long>)
