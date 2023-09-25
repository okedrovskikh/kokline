package kek.team.kokline.models

data class User(val id: Long, val nickname: String)

data class UserWithChats(val id: Long, val nickname: String, val chats: List<Long>)

data class UserCreateRequest(val nickname: String)

data class UserEditRequest(val id: Long, val nickname: String)
