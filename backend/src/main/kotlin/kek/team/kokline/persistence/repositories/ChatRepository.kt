package kek.team.kokline.persistence.repositories

import kek.team.kokline.factories.newOrSupportedTransaction
import kek.team.kokline.persistence.entities.ChatEntity
import kek.team.kokline.persistence.entities.UserEntity
import kek.team.kokline.support.utils.toSizedCollection

class ChatRepository {

    suspend fun create(name: String, users: Collection<Long>): ChatEntity = newOrSupportedTransaction {
        ChatEntity.new {
            this.name = name
            this.users = users.map { UserEntity[it] }.toSizedCollection()
        }
    }

    suspend fun findById(id: Long): ChatEntity? = newOrSupportedTransaction { ChatEntity.findById(id) }
}
