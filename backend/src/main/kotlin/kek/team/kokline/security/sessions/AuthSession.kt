package kek.team.kokline.security.sessions

import io.ktor.server.auth.Principal
import kek.team.kokline.factories.KoinFactory.koin
import kek.team.kokline.models.BasePreference
import kek.team.kokline.service.security.UserPreferenceService
import kek.team.kokline.support.serialization.DoubleAsLongSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class AuthSession(@Serializable(with = DoubleAsLongSerializer::class) val id: Long) : Principal {

    val preferences: List<BasePreference> get() = service.findAllUserPreferences(id)

    companion object {
        @Transient
        private val service: UserPreferenceService by koin.inject<UserPreferenceService>()
    }
}
