package kek.team.kokline.persistence.repositories

import kek.team.kokline.persistence.entities.UserEntity
import kek.team.kokline.persistence.entities.UserTable
import kek.team.kokline.factories.dbQuery
import kek.team.kokline.mappers.UserMapper
import kek.team.kokline.models.User
import kek.team.kokline.models.UserCreateRequest
import kek.team.kokline.models.UserEditRequest
import kek.team.kokline.models.UserWithChats
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.update

class UserRepository(private val mapper: UserMapper) {

    suspend fun create(user: UserCreateRequest): User {
        return dbQuery { UserEntity.new { nickname = user.nickname } }.let(mapper::mapToUser)
    }

    suspend fun findAll(): List<User> = dbQuery { UserEntity.all() }.map(mapper::mapToUser)

    suspend fun findByIdWithChats(id: Long): UserWithChats? = dbQuery { UserEntity.findById(id) }?.let(mapper::mapToUserWithChats)

    suspend fun edit(user: UserEditRequest): Boolean = dbQuery {
        UserTable.update({ UserTable.id eq requireNotNull(user.id) }) { it[nickname] = user.nickname }
    } > 0

    suspend fun deleteById(id: Long): Boolean = dbQuery { UserTable.deleteWhere { UserTable.id eq id } } > 0
}
