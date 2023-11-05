package kek.team.kokline.service.security

import kek.team.kokline.factories.CoroutinePoolFactory.coroutinePool
import kek.team.kokline.models.BasePreference
import kek.team.kokline.models.BasicPreference
import kek.team.kokline.models.Preference
import kotlinx.coroutines.runBlocking

class UserPreferenceService(private val preferencesService: PreferencesService) {

    fun findAllUserPreferences(userId: Long): List<BasePreference> = runBlocking(coroutinePool) {
        preferencesService.findAllUserPreferences(userId)
    }.flatMap { description ->
        description.resourcesId.map { Preference(it, description.action) } + BasicPreference
    }
}
