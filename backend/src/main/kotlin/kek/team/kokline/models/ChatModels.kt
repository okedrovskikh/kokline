package kek.team.kokline.models

data class Chat(
    val id: Long?,
    val name: String,
    val users: List<Long>,
    val avatarUrl: String?
)

data class ChatCreateRequest(val name: String, val users: List<Long>, val avatarUrl: String?)

data class ChatEditRequest(val name: String, val users: List<Long>, val avatarUrl: String)
