package kek.team.kokline.security.sessions

import io.ktor.server.auth.Principal
import kek.team.kokline.models.Preference
import kotlinx.serialization.Serializable

@Serializable
data class BasicUserSession(val id: Long, val preferences: List<Preference>) : Principal
