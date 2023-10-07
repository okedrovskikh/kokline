package kek.team.kokline.session

import io.ktor.server.auth.Principal

data class UserSession(val id: Long) : Principal
