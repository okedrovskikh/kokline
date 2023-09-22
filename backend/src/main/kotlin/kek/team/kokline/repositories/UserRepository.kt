package kek.team.kokline.repositories

import kek.team.kokline.entities.UserEntity
import kek.team.kokline.factories.dbQuery
import kek.team.kokline.models.User
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update

class UserRepository {

    suspend fun create(user: User): User {
        val statement = dbQuery { UserEntity.insert { it[nickname] = user.nickname } }
        return statement.resultedValues?.singleOrNull()?.let(::resultRowToModel) ?: error("Cannot create user")
    }

    suspend fun findAll(): List<User> = dbQuery { UserEntity.selectAll().map(::resultRowToModel) }

    suspend fun findById(id: Long): User? = dbQuery {
        UserEntity.select { UserEntity.id eq id }.map(::resultRowToModel).singleOrNull()
    }

    suspend fun edit(user: User): Boolean = dbQuery {
        UserEntity.update({ UserEntity.id eq requireNotNull(user.id) }) { it[nickname] = user.nickname } > 0
    }

    suspend fun deleteById(id: Long): Boolean = dbQuery { UserEntity.deleteWhere { UserEntity.id eq id } > 0 }

    private fun resultRowToModel(row: ResultRow): User = User(row[UserEntity.id], row[UserEntity.nickname])
}
