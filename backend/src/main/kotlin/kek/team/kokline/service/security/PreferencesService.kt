package kek.team.kokline.service.security

import kek.team.kokline.factories.dbQuery
import kek.team.kokline.mappers.PreferencesMapper
import kek.team.kokline.models.PreferenceDescription
import kek.team.kokline.persistence.entities.PreferenceEntity
import kek.team.kokline.persistence.repositories.PreferencesRepository

class PreferencesService(private val repository: PreferencesRepository, private val mapper: PreferencesMapper) {

    suspend fun create(preference: PreferenceDescription): Unit = dbQuery {

        for (ownerId in preference.ownersId) {
            for (resourceId in preference.resourcesId) {
                repository.create(preference.action, ownerId, resourceId)
            }
        }
    }

    /**
     * Execute one query for getting all resources ids
     */
    suspend fun findAllUserPreferences(userId: Long): List<PreferenceDescription> = dbQuery {
        val preferencesByActionMap = repository.findAllByOwner(userId).groupByAction()
        preferencesByActionMap.map { (key, value) -> mapper.mapToDescription(key, value) }
    }

    suspend fun findAllUserPreferencesByAction(userId: Long, action: String): List<PreferenceDescription> = dbQuery {
        val preferencesByActionMap = repository.findAllWithResourceByOwnerAndAction(userId, action).groupByAction()
        preferencesByActionMap.map { (key, value) -> mapper.mapToDescription(key, value) }
    }

    suspend fun findAllUserPreferenceByResource(
        userId: Long,
        resourceId: Long,
        actionPrefix: String, // need cause actionPrefix + resourceId is unique identifier for resource type
    ): List<PreferenceDescription> = dbQuery {
        val preferencesByActionMap = repository.findAllWithResourceByOwnerAndResource(userId, resourceId, actionPrefix).groupByAction()
        preferencesByActionMap.map { (key, value) -> mapper.mapToDescription(key, value) }
    }

    suspend fun deleteUserPreference(userId: Long, resourceId: Long, action: String): Boolean =  dbQuery {
        repository.deleteByOwnerAndResource(userId, resourceId, action)
    }
}

private fun List<PreferenceEntity>.groupByAction() = groupBy { it.action }
