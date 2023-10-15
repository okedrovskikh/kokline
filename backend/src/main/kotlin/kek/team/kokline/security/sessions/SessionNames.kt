package kek.team.kokline.security.sessions

import kek.team.kokline.security.actions.Actions.USER_EDIT
import kek.team.kokline.security.actions.Actions.USER_DELETE
import kek.team.kokline.security.actions.Actions.CHAT_READ
import kek.team.kokline.security.actions.Actions.CHAT_EDIT
import kek.team.kokline.security.actions.Actions.CHAT_DELETE

const val basicSession = "basic-session"
const val userSession = "user-session"

const val userEditSession = "user-edit-session"
const val userDeleteSession = "user-delete-session"

const val chatReadSession = "chat-read-session"
const val chatEditSession = "chat-edit-session"
const val chatDeleteSession = "chat-delete-session"

const val messageEditSession = "message-edit-session"
const val messageDeleteSession = "message-delete-session"

private val actionBySessionMap = mapOf(
    userEditSession to USER_EDIT,
    userDeleteSession to USER_DELETE,
    chatReadSession to CHAT_READ,
    chatEditSession to CHAT_EDIT,
    chatDeleteSession to CHAT_DELETE
)

fun getActionBySessionName(sessionName: String): String = requireNotNull(actionBySessionMap[sessionName]).actionName
