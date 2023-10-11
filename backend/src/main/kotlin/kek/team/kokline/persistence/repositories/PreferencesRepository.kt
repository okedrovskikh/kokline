package kek.team.kokline.persistence.repositories

import kek.team.kokline.factories.transactionLevel
import kek.team.kokline.persistence.entities.BasePreferenceEntity
import kek.team.kokline.persistence.entities.ChatEntity
import kek.team.kokline.persistence.entities.ChatPreferencesEntity
import kek.team.kokline.persistence.entities.PreferencesTable
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.sql.Join
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.inTopLevelTransaction

abstract class PreferencesRepository<T : Entity<Long>> {
    protected abstract val featurePrefix: String

    fun create(featureName: String, customerId: Long, resourceId: Long): BasePreferenceEntity<T> = inTopLevelTransaction(transactionLevel) {
        if (!featureName.startsWith(featurePrefix)) error("Illegal feature prefix: ${featureName.split(":").first()}")

        val row = PreferencesTable.insert {
            it[PreferencesTable.featureName] = featureName
            it[PreferencesTable.customerId] = customerId
            it[PreferencesTable.resourceId] = resourceId
        }.resultedValues?.single()

        wrapRow(requireNotNull(row))
    }

    fun findAllWithResourceByCustomerAndResourceIds(customerId: Long, resourceId: Long): List<BasePreferenceEntity<T>> {
        val rows = joinWithResource()
            .selectAll()
            .adjustWhere { (PreferencesTable.customerId eq customerId) and (PreferencesTable.resourceId eq resourceId) }
            .adjustWhere { PreferencesTable.featureName like featurePrefix }
            .toList()
        return rows.map(::wrapRow)
    }

    protected abstract fun joinWithResource(): Join // PreferencesTable.join(ChatTable, JoinType.INNER, onColumn = PreferencesTable.resourceId, otherColumn = ChatTable.id)

    protected abstract fun wrapRow(row: ResultRow): BasePreferenceEntity<T>

    fun findAllByCustomerId(id: Long): List<BasePreferenceEntity<out Entity<Long>>> = PreferencesTable.selectAll()
        .adjustWhere { PreferencesTable.customerId eq id }
        .map { BasePreferenceEntity.wrapRow(it) }

    /**
     * Should be run in transaction, cause have checks after deletion
     */
    // TODO проверить как работает inTopLevelTransaction, если поддерживает внешнюю транзакцию, то можно удалить комментарий
    fun deleteByCustomerAndResourceIds(customerId: Long, resourceId: Long): Boolean = inTopLevelTransaction(transactionLevel) {
        val deletedRowsCount = PreferencesTable.deleteWhere {
            (PreferencesTable.customerId eq customerId) and (PreferencesTable.resourceId eq resourceId)
        }

        if (deletedRowsCount > 1) error("Deleted more than 1 row by customerId = $customerId and resourceId = $resourceId")

        deletedRowsCount > 0
    }
}

class ChatPreferencesRepository : PreferencesRepository<ChatEntity>() {
    override val featurePrefix: String = "chat:"

    override fun joinWithResource(): Join {
        TODO("Not yet implemented")
    }

    override fun wrapRow(row: ResultRow): ChatPreferencesEntity = ChatPreferencesEntity.wrapRow(row)
}
