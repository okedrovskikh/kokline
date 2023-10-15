package kek.team.kokline.security.sessions

import io.ktor.server.auth.Principal
import kek.team.kokline.models.Preference
import kek.team.kokline.support.serialization.DoubleAsLongSerializer
import kotlinx.serialization.Serializable

@Serializable
data class BasicUserSession(
    @Serializable(with = DoubleAsLongSerializer::class)
    val id: Long,
    val preferences: List<Preference>
) : Principal
