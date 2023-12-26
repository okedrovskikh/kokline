package kek.team.kokline.persistence.repositories

import kek.team.kokline.factories.newOrSupportedTransaction
import kek.team.kokline.persistence.entities.ChatEntity
import kek.team.kokline.persistence.entities.UserEntity
import kek.team.kokline.support.utils.toSizedCollection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ChatRepository {
    suspend fun create(name: String, users: Collection<Long>, avatarUrl: String?): ChatEntity = newOrSupportedTransaction {
        withContext(Dispatchers.IO) {
            ChatEntity.new {
                this.name = name
                this.users = users.map { UserEntity[it] }.toSizedCollection()
                this.avatarUrl = avatarUrl
            }
        }
    }

    suspend fun findById(id: Long): ChatEntity? = newOrSupportedTransaction { withContext(Dispatchers.IO) { ChatEntity.findById(id) } }
}
