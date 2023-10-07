package kek.team.kokline.service.login

import kek.team.kokline.persistence.repositories.UserRepository

class LoginService(private val repository: UserRepository) {

    suspend fun login(id: Long): Boolean = repository.isExist(id)
}
