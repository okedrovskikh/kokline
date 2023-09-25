package kek.team.kokline.repositories

import kek.team.kokline.entities.UserEntity
import kek.team.kokline.entities.UserTable
import kek.team.kokline.factories.dbQuery
import kek.team.kokline.mappers.UserMapper
import kek.team.kokline.models.User
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.update

class UserRepository(private val mapper: UserMapper) {

    suspend fun create(user: User): User {
        return dbQuery { UserEntity.new { nickname = user.nickname } }.let(mapper::mapToModel)
    }

    suspend fun findAll(): List<User> = dbQuery { UserEntity.all() }.map(mapper::mapToModel)

    suspend fun findById(id: Long): User? = dbQuery { UserEntity.findById(id)?.let(mapper::mapToModel) }

    suspend fun edit(user: User): Boolean = dbQuery {
        UserTable.update({ UserTable.id eq requireNotNull(user.id) }) { it[nickname] = user.nickname } > 0
    }

    suspend fun deleteById(id: Long): Boolean = dbQuery { UserTable.deleteWhere { UserTable.id eq id } > 0 }
}
