package kek.team.kokline.persistence.repositories

import kek.team.kokline.factories.transactionLevel
import kek.team.kokline.persistence.entities.PreferenceEntity
import kek.team.kokline.persistence.entities.PreferencesTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.inTopLevelTransaction

 class PreferencesRepository {

    fun create(action: String, ownerId: Long, resourceId: Long): PreferenceEntity = inTopLevelTransaction(transactionLevel) {
        val row = PreferencesTable.insert {
            it[PreferencesTable.action] = action
            it[PreferencesTable.ownerId] = ownerId
            it[PreferencesTable.resourceId] = resourceId
        }.resultedValues?.single()

        wrapRow(requireNotNull(row))
    }

    fun findAllWithResourceByOwner(id: Long, actionPrefix: String): List<PreferenceEntity> {
        val rows = PreferencesTable.selectAll()
            .adjustWhere { (PreferencesTable.ownerId eq id) and (PreferencesTable.action like actionPrefix) }
            .toList()
        return rows.map(::wrapRow)
    }

    fun findAllWithResourceByOwnerAndResource(ownerId: Long, resourceId: Long, actionPrefix: String): List<PreferenceEntity> {
        val rows = PreferencesTable.selectAll()
            .adjustWhere { (PreferencesTable.ownerId eq ownerId) and (PreferencesTable.resourceId eq resourceId) }
            .adjustWhere { PreferencesTable.action like actionPrefix }
            .toList()
        return rows.map(::wrapRow)
    }

    fun findAllWithResourceByOwnerAndAction(ownerId: Long, action: String): List<PreferenceEntity> {
        val rows = PreferencesTable.selectAll()
            .adjustWhere { (PreferencesTable.ownerId eq ownerId) and (PreferencesTable.action eq action) }
            .toList()
        return rows.map(::wrapRow)
    }

    private fun wrapRow(row: ResultRow) = PreferenceEntity.wrapRow(row)

    fun findAllByOwner(id: Long): List<PreferenceEntity> = PreferencesTable.selectAll()
        .adjustWhere { PreferencesTable.ownerId eq id }
        .map { PreferenceEntity.wrapRow(it) }

    fun deleteByOwnerAndResource(ownerId: Long, resourceId: Long, action: String): Boolean = inTopLevelTransaction(transactionLevel) {
        val deletedRowsCount = PreferencesTable.deleteWhere {
            (PreferencesTable.ownerId eq ownerId) and (PreferencesTable.resourceId eq resourceId) and (PreferencesTable.action eq action)
        }

        if (deletedRowsCount > 1) error("Deleted more than 1 row by customerId = $ownerId and resourceId = $resourceId")

        deletedRowsCount > 0
    }
}
