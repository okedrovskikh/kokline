package kek.team.kokline.service.security

import kek.team.kokline.models.Preference
import kek.team.kokline.models.basicPreference
import kek.team.kokline.security.sessions.BasicUserSession

class SecurityService(private val preferencesService: PreferencesService) {

    suspend fun createSession(userId: Long): BasicUserSession {
        val preferencesDescriptions = preferencesService.findAllUserPreferences(userId)
        val preferences = preferencesDescriptions.flatMap { description ->
            description.resourcesId.map { Preference(it, description.action) } + basicPreference()
        }
        return BasicUserSession(userId, preferences)
    }

    fun validate(session: BasicUserSession, action: Preference): Boolean = session.preferences.contains(action)

    suspend fun haveAccessToActionWithResource(userId: Long, resourceId: Long, action: String): Boolean {
        val preferences = preferencesService.findAllUserPreferenceByResource(userId, resourceId, action)
        return preferences.flatMap { it.resourcesId }.any { it == resourceId }
    }

    suspend fun haveAccessToResource(userId: Long, resourceId: Long, actionPrefix: String, searchActions: List<String>): Boolean {
        val preferences = preferencesService.findAllUserPreferenceByResource(userId, resourceId, actionPrefix)
        return preferences.map { it.action }.any { searchActions.contains(it) }
    }
}
