package kek.team.kokline.persistence.repositories

import kek.team.kokline.factories.newOrSupportedTransaction
import kek.team.kokline.persistence.entities.UserEntity
import kek.team.kokline.persistence.entities.UserTable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class UserRepository {

    suspend fun create(nickname: String, credits: ByteArray, name: String, avatarUrl: String?): UserEntity =
        newOrSupportedTransaction {
            withContext(Dispatchers.IO) {
                UserEntity.new {
                    this.nickname = nickname
                    this.credits = credits
                    this.name = name
                    this.avatarUrl = avatarUrl
                }
            }
        }

    suspend fun findAll(): List<UserEntity> = newOrSupportedTransaction {
        withContext(Dispatchers.IO) { UserEntity.all().toList() }
    }

    suspend fun search(search: String): List<UserEntity> = newOrSupportedTransaction {
        withContext(Dispatchers.IO) {
            UserEntity.find {
                UserTable.nickname like "%$search%" or (UserTable.name like "%$search%")
            }
        }.toList()
    }

    suspend fun findById(id: Long): UserEntity? =
        newOrSupportedTransaction { withContext(Dispatchers.IO) { UserEntity.findById(id) } }

    suspend fun findByCredits(nickname: String, credits: ByteArray): UserEntity? = newOrSupportedTransaction {
        withContext(Dispatchers.IO) {
            UserEntity.find {
                (UserTable.credits eq credits) and (UserTable.nickname eq nickname)
            }
        }.let {
            if (it.empty()) null else it.single()
        }
    }

    suspend fun edit(id: Long, nickname: String?, name: String?, avatarUrl: String?): Boolean =
        newOrSupportedTransaction {
            val user: UserEntity = findById(id)!!
            val changedRows = withContext(Dispatchers.IO) {
                UserTable.update({ UserTable.id eq id }) {
                    it[UserTable.nickname] = nickname?:user.nickname
                    it[UserTable.name] = name?:user.name
                    it[UserTable.avatarUrl] = avatarUrl?:user.avatarUrl
                }
            }

            if (changedRows > 1) error("Updated more than 1 row by id: $id")

            changedRows > 0
        }


    suspend fun deleteById(id: Long): Boolean = newOrSupportedTransaction {
        val deletedRows = withContext(Dispatchers.IO) { UserTable.deleteWhere { UserTable.id eq id } }

        if (deletedRows > 1) error("Delete more than 1 row by id: $id")

        deletedRows > 0
    }
}
