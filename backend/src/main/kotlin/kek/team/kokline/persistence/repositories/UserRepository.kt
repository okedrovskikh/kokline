package kek.team.kokline.persistence.repositories

import kek.team.kokline.persistence.entities.UserEntity
import kek.team.kokline.persistence.entities.UserTable
import kek.team.kokline.factories.newOrSupportedTransaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.update

class UserRepository {

    suspend fun create(nickname: String, credits: ByteArray): UserEntity = newOrSupportedTransaction {
        UserEntity.new {
            this.nickname = nickname
            this.credits = credits
        }
    }

    suspend fun findById(id: Long): UserEntity? = newOrSupportedTransaction { UserEntity.findById(id) }

    suspend fun findByCredits(nickname: String, credits: ByteArray): UserEntity? = newOrSupportedTransaction {
        UserEntity.find {
            (UserTable.credits eq credits) and (UserTable.nickname eq nickname)
        }.let {
            if (it.empty()) null else it.single()
        }
    }

    suspend fun edit(id: Long, nickname: String): Boolean = newOrSupportedTransaction {
        val changedRows = UserTable.update({ UserTable.id eq id }) { it[UserTable.nickname] = nickname }

        if (changedRows > 1) error("Updated more than 1 row by id: $id")

        changedRows > 0
    }


    suspend fun deleteById(id: Long): Boolean = newOrSupportedTransaction {
        val deletedRows = UserTable.deleteWhere { UserTable.id eq id }

        if (deletedRows > 1) error("Delete more than 1 row by id: $id")

        deletedRows > 0
    }
}
