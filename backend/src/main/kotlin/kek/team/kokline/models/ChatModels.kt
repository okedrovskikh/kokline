package kek.team.kokline.models

import kek.team.kokline.security.actions.Actions

data class Chat(
    val id: Long?,
    val name: String,
    val users: List<Long>,
    val avatarUrl: String?,
    val userPreferences: List<Actions>
)

data class ChatCreateRequest(val name: String, val users: List<Long>, val avatarUrl: String?)

data class ChatEditRequest(val name: String, val users: List<Long>, val avatarUrl: String)
