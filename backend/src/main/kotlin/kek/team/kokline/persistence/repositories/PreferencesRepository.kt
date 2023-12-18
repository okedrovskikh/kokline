package kek.team.kokline.persistence.repositories

import kek.team.kokline.factories.newOrSupportedTransaction
import kek.team.kokline.models.PreferenceDescription
import kek.team.kokline.persistence.entities.PreferenceEntity
import kek.team.kokline.persistence.entities.PreferencesTable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.selectAll

class PreferencesRepository {

    suspend fun create(preference: PreferenceDescription): List<PreferenceEntity> = newOrSupportedTransaction {
        createAll(listOf(preference))
    }

    suspend fun createAll(preferences: Collection<PreferenceDescription>): List<PreferenceEntity> = newOrSupportedTransaction {
        val rows = withContext(Dispatchers.IO) {
            PreferencesTable.batchInsert(preferences.toTriplets()) {
                this[PreferencesTable.action] = it.first
                this[PreferencesTable.ownerId] = it.second
                this[PreferencesTable.resourceId] = it.third
            }
        }
        rows.map(::wrapRow)
    }

    private fun Collection<PreferenceDescription>.toTriplets() = flatMap { it.toTriplets() }

    private fun PreferenceDescription.toTriplets() = resourcesId.flatMap { resource ->
        ownersId.map { owner ->
            Triple(action, owner, resource)
        }
    }

    suspend fun findAllWithResourceByOwner(id: Long, actionPrefix: String): List<PreferenceEntity> = newOrSupportedTransaction {
        val rows = withContext(Dispatchers.IO) {
            PreferencesTable.selectAll()
                .andWhere { (PreferencesTable.ownerId eq id) and (PreferencesTable.action like "$actionPrefix%") }
                .toList()
        }
        rows.map(::wrapRow)
    }

    suspend fun findAllWithResourceByOwnerAndResource(
        ownerId: Long,
        resourceId: Long,
        actionPrefix: String,
    ): List<PreferenceEntity> = newOrSupportedTransaction {
        val rows = withContext(Dispatchers.IO) {
            PreferencesTable.selectAll()
                .andWhere { (PreferencesTable.ownerId eq ownerId) and (PreferencesTable.resourceId eq resourceId) }
                .andWhere { PreferencesTable.action like "$actionPrefix%" }
                .toList()
        }
        rows.map(::wrapRow)
    }

    suspend fun findAllWithResourceByOwnerAndAction(ownerId: Long, action: String): List<PreferenceEntity> = newOrSupportedTransaction {
        val rows = withContext(Dispatchers.IO) {
            PreferencesTable.selectAll()
                .andWhere { (PreferencesTable.ownerId eq ownerId) and (PreferencesTable.action eq action) }
                .toList()
        }
        rows.map(::wrapRow)
    }

    suspend fun findAllByOwner(id: Long): List<PreferenceEntity> = newOrSupportedTransaction {
        withContext(Dispatchers.IO) {
            PreferencesTable.selectAll()
                .andWhere { PreferencesTable.ownerId eq id }
                .map { PreferenceEntity.wrapRow(it) }
        }
    }

    private fun wrapRow(row: ResultRow) = PreferenceEntity.wrapRow(row)

    suspend fun deleteByOwnerAndResource(ownerId: Long, resourceId: Long, action: String): Boolean = newOrSupportedTransaction {
        val deletedRowsCount = withContext(Dispatchers.IO) {
            PreferencesTable.deleteWhere {
                (PreferencesTable.ownerId eq ownerId) and (PreferencesTable.resourceId eq resourceId) and (PreferencesTable.action eq action)
            }
        }

        if (deletedRowsCount > 1) error("Deleted more than 1 row by customerId = $ownerId and resourceId = $resourceId")

        deletedRowsCount > 0
    }

    suspend fun deleteAllByOwnerId(ownerId: Long): Int = newOrSupportedTransaction {
        withContext(Dispatchers.IO) { PreferencesTable.deleteWhere { PreferencesTable.ownerId eq ownerId } }
    }

    suspend fun deleteAllByResource(resourceId: Long, actionPrefix: String): Int = newOrSupportedTransaction {
        withContext(Dispatchers.IO) {
            PreferencesTable.deleteWhere { (PreferencesTable.resourceId eq resourceId) and (action like "$actionPrefix%") }
        }
    }
}
