package kek.team.kokline.service.security

import kek.team.kokline.factories.newOrSupportedTransaction
import kek.team.kokline.factories.dbQuery
import kek.team.kokline.mappers.PreferencesMapper
import kek.team.kokline.models.PreferenceDescription
import kek.team.kokline.persistence.entities.PreferenceEntity
import kek.team.kokline.persistence.repositories.PreferencesRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class PreferencesService(private val repository: PreferencesRepository, private val mapper: PreferencesMapper) {

    suspend fun create(preference: PreferenceDescription): Unit = newOrSupportedTransaction {
        repository.create(preference)
    }

    suspend fun createAll(preferences: Collection<PreferenceDescription>): Unit = newOrSupportedTransaction {
        repository.createAll(preferences)
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

    suspend fun deleteUserPreference(userId: Long, resourceId: Long, action: String): Boolean = newOrSupportedTransaction {
        repository.deleteByOwnerAndResource(userId, resourceId, action)
    }

    suspend fun deleteUserPreference(preference: PreferenceDescription): Unit  = coroutineScope {
        newOrSupportedTransaction {
            preference.resourcesId.flatMap { resource ->
                preference.ownersId.map { owner ->
                    async { deleteUserPreference(owner, resource, preference.action) }
                }
            }.awaitAll()
        }
    }

    suspend fun deleteAllUserPreferences(userId: Long): Unit = newOrSupportedTransaction {
        repository.deleteAllByOwnerId(userId)
    }

    suspend fun deleteAllPreferencesByResource(resourceId: Long, actionPrefix: String): Unit = newOrSupportedTransaction {
        repository.deleteAllByResource(resourceId, actionPrefix)
    }
}

private fun List<PreferenceEntity>.groupByAction() = groupBy { it.action }
