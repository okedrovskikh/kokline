package kek.team.kokline.persistence.repositories

import kek.team.kokline.persistence.entities.UserEntity
import kek.team.kokline.persistence.entities.UserTable
import kek.team.kokline.factories.dbQuery
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.update

class UserRepository {

    suspend fun create(nickname: String, credits: ByteArray): UserEntity = dbQuery {
        UserEntity.new {
            this.nickname = nickname
            this.credits = credits
        }
    }

    suspend fun findByIdWithChats(id: Long): UserEntity? = dbQuery { UserEntity.findById(id) }

    suspend fun edit(id: Long, nickname: String): Boolean = dbQuery {
        val changedRows = UserTable.update({ UserTable.id eq id }) { it[UserTable.nickname] = nickname }

        if (changedRows == 1) error("Updated more than 1 row by id: $id")

        changedRows > 0
    }


    suspend fun deleteById(id: Long): Boolean = dbQuery {
        val deletedRows = UserTable.deleteWhere { UserTable.id eq id }

        if (deletedRows > 1) error("Delete more than 1 row by id: $id")

        deletedRows > 0
    }

    suspend fun isExist(id: Long): Boolean = dbQuery { UserEntity.findById(id) } != null
}
