package kek.team.kokline.models

data class User(val id: Long, val nickname: String, val chats: List<Long>? = null)

data class UserCreateRequest(val nickname: String, val credits: String)

data class UserEditRequest(val nickname: String)
