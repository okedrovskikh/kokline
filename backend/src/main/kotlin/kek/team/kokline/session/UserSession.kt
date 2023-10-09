package kek.team.kokline.session

import io.ktor.server.auth.Principal

const val userSession = "user-session"

data class UserSession(val id: Long) : Principal
