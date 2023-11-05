package kek.team.kokline.service.security

import kek.team.kokline.models.BasePreference
import kek.team.kokline.security.sessions.AuthSession

class SecurityService(private val preferencesService: PreferencesService) {

    fun createSession(userId: Long): AuthSession = AuthSession(userId)

    fun validate(session: AuthSession, action: BasePreference): Boolean = session.preferences.contains(action)

    suspend fun haveAccessToActionWithResource(userId: Long, resourceId: Long, action: String): Boolean {
        val preferences = preferencesService.findAllUserPreferenceByResource(userId, resourceId, action)
        return preferences.flatMap { it.resourcesId }.any { it == resourceId }
    }

    suspend fun haveAccessToResource(userId: Long, resourceId: Long, actionPrefix: String, searchActions: List<String>): Boolean {
        val preferences = preferencesService.findAllUserPreferenceByResource(userId, resourceId, actionPrefix)
        return preferences.map { it.action }.any { searchActions.contains(it) }
    }
}
